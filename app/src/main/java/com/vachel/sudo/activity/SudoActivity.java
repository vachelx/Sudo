package com.vachel.sudo.activity;


import android.content.Intent;
import android.os.Handler;

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
import com.vachel.sudo.utils.Utils;
import com.vachel.sudo.widget.InputLayout;
import com.vachel.sudo.widget.SudoBoard;
import com.vachel.sudo.widget.TimerView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SudoActivity extends BaseActivity implements SudoBoard.ISolveListener {
    private TimerView mTimer;
    private boolean hasInit = false;
    private SudoBoard mSudoView;
    private Handler mHandler;
    private SudoPresenter mPresenter;
    private int[] mCruxKey = new int[4];
    private Integer[][] mExamSudo;

    @Override
    int getLayoutId() {
        return R.layout.activity_sudo;
    }

    @Override
    void init() {
        mHandler = new Handler();
        mPresenter = new SudoPresenter();
        Intent intent = getIntent();
        mCruxKey = intent.getIntArrayExtra(Constants.KEY_EXAM);
        mSudoView = findViewById(R.id.sudo_view);
        InputLayout inputView = findViewById(R.id.input_layout);
        mTimer = findViewById(R.id.timer);
        inputView.setOnTextClickListener(mSudoView);
        mSudoView.setSolveListener(this);
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
        long takeTime = mTimer.stopTimer();
        mPresenter.saveSolvedRecord(mCruxKey, takeTime, System.currentTimeMillis(), mExamSudo);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, 5000);
    }
}
