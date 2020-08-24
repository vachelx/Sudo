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
import com.vachel.sudo.dao.ArchiveBean;
import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.manager.ArchiveDataManager;
import com.vachel.sudo.manager.ExamDataManager;
import com.vachel.sudo.manager.RecordDataManager;
import com.vachel.sudo.presenter.SudoPresenter;
import com.vachel.sudo.utils.Arithmetic;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.InnerHandler;
import com.vachel.sudo.utils.ToastUtil;
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

        mReplayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSudoView.startCompleteAnim();
            }
        });
        mNextGameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNextGame();
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
        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Long> emitter) {
                long result = mPresenter.checkRecord(mCruxKey, takeTime);
                emitter.onNext(result);
                if (result == 0 || result == -1) { // 刷新纪录后保存
                    mPresenter.saveSolvedRecord(mCruxKey, takeTime, System.currentTimeMillis(), mExamSudo);
                }
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<Long>autoDisposable(AndroidLifecycleScopeProvider.from(SudoActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long result) {
                        if (result == -1) {
                            mTimer.setText("本题新纪录诞生：" + Utils.parseTakeTime(takeTime, 0));
                        } else if (result == 0) {
                            mTimer.setText("恭喜您！耗时" + Utils.parseTakeTime(takeTime, 0));
                        } else if (result > 0) {
                            mTimer.setText("耗时" + Utils.parseTakeTime(takeTime, 0) + " 本题最快记录为" + Utils.parseTakeTime(result, 0));
                        }
                    }
                });

        mReplayView.setVisibility(View.VISIBLE);
        mReplayView.setClickable(true);
        mNextGameView.setVisibility(View.VISIBLE);
        mInputView.setLock(true);

    }

    @Override
    public void handleNextFrame(final int size) {
        int defaultDelay = 20;
        if (size < 20) {
            defaultDelay = 400 / size;
        }
        mHandler.sendEmptyMessageDelayed(0, defaultDelay);
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
        mReplayView.setClickable(false);
    }

    @Override
    public void onTextChanged(Integer[][] sudo) {
        mInputView.updateKeyNumberUseCounts(sudo);
    }

    @Override
    public void saveArchive(final Integer[][] examData, final Integer[][] tmpData) {
        final long takeTime = mTimer.getTakeTime();
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) {
                ArchiveBean archiveBean = new ArchiveBean(Utils.getArchiveKey(mCruxKey),
                        Utils.sudoToString(examData), Utils.sudoToString(tmpData),takeTime, System.currentTimeMillis(), mCruxKey[0], mCruxKey[1], mCruxKey[3]);
                ArchiveDataManager.getInstance().addOrUpdateArchive(archiveBean);
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<Boolean>autoDisposable(AndroidLifecycleScopeProvider.from(SudoActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) {
                        ToastUtil.showShortToast(SudoActivity.this, "存档成功");
                    }
                });
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 0){
            mSudoView.invalidate();
        }
    }
}
