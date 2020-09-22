package com.vachel.sudo.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

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
import com.vachel.sudo.engine.Algorithm;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.EventTag;
import com.vachel.sudo.utils.PreferencesUtils;
import com.vachel.sudo.utils.ToastUtil;
import com.vachel.sudo.utils.Utils;
import com.vachel.sudo.widget.BaseAlertDialog;
import com.vachel.sudo.widget.InputLayout;
import com.vachel.sudo.widget.SudoBoard;
import com.vachel.sudo.widget.TimerView;
import com.vachel.sudo.widget.TrapezoidView;
import com.vachel.sudo.widget.icon.RePlayView;

import org.simple.eventbus.EventBus;

import java.util.TreeSet;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.vachel.sudo.utils.Constants.SHOW_RESUME_ARCHIVE_TIPS;

public class SudoActivity extends BaseActivity implements SudoBoard.IBoardListener {
    private TimerView mTimer;
    private boolean hasInit = false;
    private SudoBoard mSudoView;
    private SudoPresenter mPresenter;
    private int[] mCruxKey = new int[4];
    private Integer[][] mExamSudo;
    private RePlayView mReplayView;
    private InputLayout mInputView;
    private Button mNextGameView;
    private boolean mIsResume;
    private Integer[][] mResumeTmpSudo;
    private TreeSet<Integer>[][] mResumeMarks;
    private long mResumeTime;
    private TrapezoidView mTitle;
    private boolean mSolved;

    @Override
    int getLayoutId() {
        return R.layout.activity_sudo;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mCruxKey = intent.getIntArrayExtra(Constants.KEY_EXAM);
        mIsResume = intent.getBooleanExtra(Constants.KEY_RESUME, false);
        mReplayView.setVisibility(View.GONE);
        mNextGameView.setVisibility(View.GONE);

        mSudoView.resetAll(false);
        initData();
    }

    @Override
    void init() {
        mPresenter = new SudoPresenter();
        initView();

        Intent intent = getIntent();
        mCruxKey = intent.getIntArrayExtra(Constants.KEY_EXAM);
        mIsResume = intent.getBooleanExtra(Constants.KEY_RESUME, false);
        initData();
    }

    private void initData() {
        mTitle.setText(mCruxKey[0] == 0 ? "随机模式" : ("闯关玩法-" + (mCruxKey[3] +1)));
        Observable.create((ObservableOnSubscribe<Integer[][]>) emitter -> {
            if (mIsResume) {
                ArchiveBean archive = ArchiveDataManager.getInstance().getArchiveByDiff(mCruxKey[0], mCruxKey[1]);
                mExamSudo = Utils.parseSudo(archive.getExam());
                mResumeTmpSudo = Utils.parseSudo(archive.getTmp());
                mResumeMarks = Utils.parseMarks(archive.getMark());
                mResumeTime = archive.getTakeTime();
            } else if (mCruxKey[Constants.MODE] == 0) { // 随机模式
                // 随机模式随机生成题目后才拼接key。
                mCruxKey[Constants.INDEX] = RecordDataManager.getInstance().getRecordSizeByClassify(0, mCruxKey[Constants.DIFFICULTY]);
                mExamSudo = Algorithm.getExamSudo(mCruxKey[Constants.DIFFICULTY]);
            } else { // 闯关模式直接从数据库取题目
                Examination examination = ExamDataManager.getInstance().getExam(Utils.getExamKey(mCruxKey));
                mExamSudo = Utils.parseSudo(examination.getExam());
            }
            emitter.onNext(mExamSudo);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(SudoActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(sudo -> {
                    if (mIsResume) {
                        mSudoView.resumeData(sudo, mResumeTmpSudo, mResumeMarks);
                        mTimer.startTimerWithMillis(mResumeTime);
                    } else {
                        mSudoView.initData(sudo);
                        mTimer.startTimer();
                    }
                    hasInit = true;
                });
    }

    private void initView() {
        mSudoView = findViewById(R.id.sudo_view);
        mTitle = findViewById(R.id.mode_title);
        mReplayView = findViewById(R.id.replay);
        mNextGameView = findViewById(R.id.next_game);
        mInputView = findViewById(R.id.input_layout);
        mTimer = findViewById(R.id.timer);
        mInputView.setOnTextClickListener(mSudoView);
        mSudoView.setBoardListener(this);
        mReplayView.setOnClickListener(v -> mSudoView.startCompleteAnim());
        mNextGameView.setOnClickListener(v -> goNextGame());

        boolean showTimer = PreferencesUtils.getBooleanPreference(getApplicationContext(), Constants.SHOW_TIMER, true);
        if (!showTimer) {
            mTimer.setVisibility(View.INVISIBLE);
        }
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
            mTimer.resumeTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.stopTimer();
    }

    @Override
    public void onSolved() {
        final long takeTime = mTimer.stopTimer();
        mSolved = true;
        Observable.create((ObservableOnSubscribe<Long>) emitter -> {
            long result = mPresenter.checkRecord(mCruxKey, takeTime);
            emitter.onNext(result);
            if (result == 0 || result == -1) { // 刷新纪录后保存
                mPresenter.saveSolvedRecord(mCruxKey, takeTime, System.currentTimeMillis(), mExamSudo);
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(SudoActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(result -> {
                    if (result == -1) {
                        mTimer.setText("本题新纪录诞生：" + Utils.parseTakeTime(takeTime, 0));
                    } else if (result == 0) {
                        mTimer.setText("恭喜您！耗时" + Utils.parseTakeTime(takeTime, 0));
                    } else if (result > 0) {
                        mTimer.setText("耗时" + Utils.parseTakeTime(takeTime, 0) + " 本题最快记录为" + Utils.parseTakeTime(result, 0));
                    }
                });

        mReplayView.setVisibility(View.VISIBLE);
        mReplayView.setClickable(true);
        mNextGameView.setVisibility(View.VISIBLE);
        mInputView.setLock(true);

    }

    @Override
    public void completeExam() {
        new BaseAlertDialog()
                .initDialog("", "恭喜你！用时" + Utils.parseTakeTime(mTimer.getTakeTime(), 0) + "。是否进入下一关？")
                .setPositiveTextDefault()
                .setNegativeTextDefault()
                .setListener(new BaseAlertDialog.IDialogListener() {
                    @Override
                    public void onPositiveClick() {
                        goNextGame();
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                })
                .show(getSupportFragmentManager(), "");
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
    }

    @Override
    public void onReset(boolean needTimer) {
        if (needTimer) {
            mTimer.onResetStart();
        }
        mInputView.setLock(false);
        mReplayView.setClickable(false);
        mSolved = false;
    }

    @Override
    public void onTextChanged(Integer[][] sudo) {
        mInputView.updateKeyNumberUseCounts(sudo);
    }

    @Override
    public void saveArchive(final Integer[][] examData, final Integer[][] tmpData, final TreeSet<Integer>[][] marks) {
        final long takeTime = mTimer.getTakeTime();
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            ArchiveBean archiveBean = new ArchiveBean(Utils.getArchiveKey(mCruxKey),
                    Utils.sudoToString(examData), Utils.sudoToString(tmpData), Utils.sudoMarksToString(marks),
                    takeTime, System.currentTimeMillis(), mCruxKey[0], mCruxKey[1], mCruxKey[3]);
            ArchiveDataManager.getInstance().addOrUpdateArchive(archiveBean);
            EventBus.getDefault().post(mCruxKey, EventTag.ON_ARCHIVE_CHANGED);
            emitter.onNext(true);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(SudoActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(result -> {
                    ToastUtil.showShortToast(SudoActivity.this, "存档成功");
                    finish();
                });
    }

    @Override
    public void onBackPressed() {
        if (mSudoView.hasNoFilledData() || mSolved) {
            super.onBackPressed();
            return;
        }
        boolean saveTips = PreferencesUtils.getBooleanPreference(getApplicationContext(), Constants.SAVE_TIPS_WHILE_EXIT, false);
        if (!saveTips) {
            super.onBackPressed();
            return;
        }
        new BaseAlertDialog()
                .initDialog("", "退出前是否保存当前已填入数据？")
                .setNegativeText("否")
                .setPositiveText("是")
                .setListener(new BaseAlertDialog.IDialogListener() {
                    @Override
                    public void onPositiveClick() {
                        mSudoView.onSaveClick();
                    }

                    @Override
                    public void onNegativeClick() {
                        finish();
                    }
                })
                .show(getSupportFragmentManager(), "");
    }
}
