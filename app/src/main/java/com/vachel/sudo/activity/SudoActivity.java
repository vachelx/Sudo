package com.vachel.sudo.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vachel.sudo.R;
import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.manager.ExamDataManager;
import com.vachel.sudo.manager.RecordDataManager;
import com.vachel.sudo.presenter.SudoPresenter;
import com.vachel.sudo.utils.Arithmetic;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.InnerHandler;
import com.vachel.sudo.utils.ThreadPoolX;
import com.vachel.sudo.utils.Utils;
import com.vachel.sudo.widget.InputLayout;
import com.vachel.sudo.widget.SudoBoard;
import com.vachel.sudo.widget.TimerView;
import com.vachel.sudo.widget.icon.RePlayView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SudoActivity extends BaseActivity implements SudoBoard.IBoardListener, InnerHandler.ILifeCycleMessageHandler {
    private TimerView mTimer;
    private boolean hasInit = false;
    private SudoBoard mSudoView;
    private Handler mHandler;
    private SudoPresenter mPresenter;
    private int[] mCruxKey = new int[4];
    private Integer[][] mExamSudo;
    private RePlayView mReplayView;
    private InputLayout mInputView;
    private Button mNextGameView;

    @Override
    int getLayoutId() {
        return R.layout.activity_sudo;
    }

    @Override
    void init() {
        mHandler = new InnerHandler(this);
        mPresenter = new SudoPresenter();
        Intent intent = getIntent();
        mCruxKey = intent.getIntArrayExtra(Constants.KEY_EXAM);
        mSudoView = findViewById(R.id.sudo_view);
        mReplayView = findViewById(R.id.replay);
        mNextGameView = findViewById(R.id.next_game);
        mInputView = findViewById(R.id.input_layout);
        mTimer = findViewById(R.id.timer);
        mInputView.setOnTextClickListener(mSudoView);
        mSudoView.setBoardListener(this);
        Observable.create(new ObservableOnSubscribe<Integer[][]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer[][]> emitter) {
                if (mCruxKey[Constants.MODE] == 0) { // 随机模式
                    // 随机模式随机生成题目后才拼接key。
                    mCruxKey[Constants.INDEX] = RecordDataManager.getInstance().getRecordSizeByClassify(0, mCruxKey[Constants.DIFFICULTY]);
                    mExamSudo = Arithmetic.getExamSudo(mCruxKey[Constants.DIFFICULTY]);
                } else { // 闯关模式直接从数据库取题目
                    Examination examination = ExamDataManager.getInstance().getExam(Utils.getExamKey(mCruxKey));
                    mExamSudo = Utils.parseSudo(examination.getExam());
                }
                emitter.onNext(mExamSudo);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<Integer[][]>autoDisposable(AndroidLifecycleScopeProvider.from(SudoActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Consumer<Integer[][]>() {
                    @Override
                    public void accept(Integer[][] sudo) {
                        mSudoView.initData(sudo);
                        hasInit = true;
                        mTimer.startTimer();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.pauseTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasInit) {
            mTimer.startTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.stopTimer();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onSolved() {
        final long takeTime = mTimer.stopTimer();
        // 保存记录
        ThreadPoolX.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                mPresenter.saveSolvedRecord(mCruxKey, takeTime, System.currentTimeMillis(), mExamSudo);
            }
        });
        // TODO 显示下一关和复盘按钮
        mReplayView.setVisibility(View.VISIBLE);
        mReplayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSudoView.startCompleteAnim();
            }
        });
        mNextGameView.setVisibility(View.VISIBLE);
        mNextGameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNextGame();
            }
        });
        mInputView.setLock(true);

    }

    @Override
    public void handleNextFrame() {
        mHandler.sendEmptyMessageDelayed(0, 20);
    }

    @Override
    public void jumpNext() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("恭喜你！用时"+ Utils.parseTakeTime(mTimer.getTakeTime(),0)+"。是否进入下一关？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(true);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goNextGame();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void goNextGame() {
        Intent intent = new Intent(SudoActivity.this, SudoActivity.class);
        int[] nextKey = new int[4];
        nextKey[0] = mCruxKey[0];
        nextKey[1] = mCruxKey[1];
        nextKey[2] = mCruxKey[2];
        nextKey[3] = mCruxKey[3] + 1;
        intent.putExtra(Constants.KEY_EXAM, nextKey);
        startActivity(intent);
        finish();
    }

    @Override
    public void onReset() {
        mTimer.onResetStart();
        mInputView.setLock(false);
    }

    @Override
    public void onTextChanged(Integer[][] sudo) {
        mInputView.updateKeyNumberUseCounts(sudo);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 0){
            mSudoView.invalidate();
        }
    }
}
