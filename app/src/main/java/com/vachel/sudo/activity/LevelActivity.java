package com.vachel.sudo.activity;

import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vachel.sudo.R;
import com.vachel.sudo.adapter.LevelAdapter;
import com.vachel.sudo.dao.Examination;
import com.vachel.sudo.manager.ExamDataManager;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.widget.TabLayout;
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
        mDifficulty = getIntent().getIntExtra(Constants.KEY_DIFFICULTY, 0);
        final TabLayout tabLayout = findViewById(R.id.diff_tab);
        String[] items = new String[]{
                "简单", "中等", "困难","专家"
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
                .as(AutoDispose.<List<Examination>>autoDisposable(AndroidLifecycleScopeProvider.from(LevelActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(result ->{
                    LevelAdapter myRecyclerAdapter = new LevelAdapter(this, this,  result);
                    mListView.setAdapter(myRecyclerAdapter);
                });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, SudoActivity.class);
        int[] nextKey = new int[4];
        nextKey[0] = 1;
        nextKey[1] = mDifficulty;
        nextKey[3] = position;
        intent.putExtra(Constants.KEY_EXAM, nextKey);
        startActivity(intent);
    }
}
