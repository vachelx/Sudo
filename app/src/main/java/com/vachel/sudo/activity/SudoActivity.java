package com.vachel.sudo.activity;


import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vachel.sudo.R;
import com.vachel.sudo.utils.Arithmetic;
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

    @Override
    int getLayoutId() {
        return R.layout.activity_sudo;
    }

    @Override
    void init() {
        mHandler = new Handler();
        Intent intent = getIntent();
        final int mode = intent.getIntExtra("mode", 0);
        mSudoView = findViewById(R.id.sudo_view);
        InputLayout inputView = findViewById(R.id.input_layout);
        mTimer = findViewById(R.id.timer);
        inputView.setOnTextClickListener(mSudoView);
        mSudoView.setSolveListener(this);
        Observable.create(new ObservableOnSubscribe<Integer[][]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer[][]> emitter) {
                Integer[][] examSudo = Arithmetic.getExamSudo(mode);
                emitter.onNext(examSudo);
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
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SudoActivity.this, SudoActivity.class);
                intent.putExtra("mode",  getIntent().getIntExtra("mode", 0));
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}
