package com.vachel.sudo.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.widget.CompoundButton;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vachel.sudo.R;
import com.vachel.sudo.adapter.LevelAdapter;
import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.engine.ThreadPoolX;
import com.vachel.sudo.manager.ArchiveDataManager;
import com.vachel.sudo.manager.ExamDataManager;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.EventTag;
import com.vachel.sudo.utils.Utils;
import com.vachel.sudo.widget.BaseAlertDialog;
import com.vachel.sudo.widget.TabLayout;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class LevelActivity extends BaseActivity implements LevelAdapter.IOnItemClickListener {
    private int mDifficulty;
    private RecyclerView mListView;

    @Override
    int getLayoutId() {
        return R.layout.activity_level;
    }

    @Override
    void init() {
        EventBus.getDefault().register(this);
        mDifficulty = getIntent().getIntExtra(Constants.KEY_DIFFICULTY, 0);
        final TabLayout tabLayout = findViewById(R.id.diff_tab);
        String[] items = new String[]{
                "简单", "中等", "困难", "专家"
        };
        tabLayout.setItems(items);
        tabLayout.setSelectItem(mDifficulty);
        tabLayout.setOnItemClickListener((view, index) -> updateSelectDifficulty(mDifficulty = index));


        mListView = findViewById(R.id.level_list);
        GridLayoutManager manager =
                new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false);
        mListView.setLayoutManager(manager);
        updateSelectDifficulty(mDifficulty);
    }

    private void updateSelectDifficulty(final int difficulty) {
        Observable.create((ObservableOnSubscribe<List<Examination>>) emitter -> {
            List<Examination> exams = ExamDataManager.getInstance().getExamsByDiff(1, difficulty);
            emitter.onNext(exams);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(LevelActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(result -> {
                    LevelAdapter myRecyclerAdapter = new LevelAdapter(this, this, result);
                    mListView.setAdapter(myRecyclerAdapter);
                });
    }

    @Override
    public void onItemClick(int position) {
        final int[] nextKey = new int[4];
        nextKey[0] = 1;
        nextKey[1] = mDifficulty;
        nextKey[3] = position;

        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            emitter.onNext(ArchiveDataManager.getInstance().checkArchiveExist(Utils.getArchiveKey(nextKey)));
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(LevelActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(hasArchive -> {
                    if (hasArchive) {
                        final BaseAlertDialog baseAlertDialog = new BaseAlertDialog();
                        baseAlertDialog.initDialog("提示", "黄色题号表示有存档记录，双击题号可以直接恢复存档");
                        baseAlertDialog.setNegativeTextDefault();
                        baseAlertDialog.setPositiveTextDefault();
                        baseAlertDialog.setShowCheckBox(true, new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            }
                        });
                        baseAlertDialog.show(this.getSupportFragmentManager(), "");
//                        AlertDialog.Builder builder = new AlertDialog.Builder(LevelActivity.this);
//                        builder.setTitle("提示");
//                        builder.setMessage("黄色题号表示有存档记录，双击题号可以直接恢复存档");
//                        builder.setNegativeButton("新游戏", (dialog, which) -> {
//                            startSudoActivity(nextKey, false, true);
//                        });
//                        builder.setCancelable(true);
//                        builder.setPositiveButton("恢复存档", (dialog, which) -> startSudoActivity(nextKey, true, true));
//                        final AlertDialog alertDialog = builder.create();
//                        alertDialog.show();
                    } else {
                        startSudoActivity(nextKey, false, false);
                    }
                });
    }

    private void startSudoActivity(final int[] nextKey, boolean resume, boolean deleteArchive) {
        if (deleteArchive) {
            ThreadPoolX.getThreadPool().execute(() -> {
                ArchiveDataManager.getInstance().deleteArchive(Utils.getArchiveKey(nextKey));
                EventBus.getDefault().post(nextKey, EventTag.ON_ARCHIVE_CHANGED);
            });
        }
        Intent intent = new Intent(this, SudoActivity.class);
        intent.putExtra(Constants.KEY_EXAM, nextKey);
        intent.putExtra(Constants.KEY_RESUME, resume);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = EventTag.EXAM_SOLVED, mode = ThreadMode.MAIN)
    public void onExamSolved(int[] cruxKey) {
        // 当前页面的题被解决后刷新解锁进度
        if (cruxKey[0] == 1 && mDifficulty == cruxKey[1]) {
            final String examKey = Utils.getExamKey(cruxKey);
            Observable.create((ObservableOnSubscribe<Examination>) emitter -> {
                Examination exams = ExamDataManager.getInstance().getExam(examKey);
                emitter.onNext(exams);
                emitter.onComplete();
            }).subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread())
                    .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(LevelActivity.this, Lifecycle.Event.ON_DESTROY)))
                    .subscribe(result -> {
                        RecyclerView.Adapter adapter = mListView.getAdapter();
                        if (adapter != null) {
                            ((LevelAdapter) adapter).updateExamination(result);
                        }
                    });
        }
    }

    @Subscriber(tag = EventTag.ON_ARCHIVE_CHANGED, mode = ThreadMode.MAIN)
    public void onSaveArchive(int[] cruxKey) {
        if (cruxKey[0] == 1 && mDifficulty == cruxKey[1]) {
            RecyclerView.Adapter adapter = mListView.getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

}
