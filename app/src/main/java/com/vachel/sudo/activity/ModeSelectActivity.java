package com.vachel.sudo.activity;

import android.content.Intent;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vachel.sudo.R;
import com.vachel.sudo.adapter.PickerAdapter;
import com.vachel.sudo.dao.ArchiveBean;
import com.vachel.sudo.helper.PageChangedListener;
import com.vachel.sudo.manager.ArchiveDataManager;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.helper.ScaleTransformer;
import com.vachel.sudo.utils.PreferencesUtils;
import com.vachel.sudo.widget.TabLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ModeSelectActivity extends BaseActivity implements View.OnClickListener {
    private int mSelectMode;
    private int mSelectDifficulty;
    private List<ArchiveBean> mAllArchives;
    private View mResumeView;

    @Override
    int getLayoutId() {
        return R.layout.activity_mode_select;
    }

    @Override
    void init() {
        mResumeView = findViewById(R.id.resume_game);

        initSelectMode();
        initSelectDifficulty();
        updateResumeView(mSelectMode, mSelectDifficulty);

        mResumeView.setOnClickListener(this);
        findViewById(R.id.statistics).setOnClickListener(this);
        findViewById(R.id.start_game).setOnClickListener(this);
    }

    private void updateResumeView(final int mode, final int difficulty) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            emitter.onNext(ArchiveDataManager.getInstance().checkArchiveExist(mode, difficulty));
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<Boolean>autoDisposable(AndroidLifecycleScopeProvider.from(ModeSelectActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(result -> mResumeView.setVisibility(result ? View.VISIBLE : View.GONE));
    }

    private void initSelectDifficulty() {
        final ViewPager pickSelect = findViewById(R.id.pick_difficulty);
        ArrayList<String> data = new ArrayList<>();
        data.add("简单");
        data.add("中等");
        data.add("困难");
        data.add("专家");
        final PickerAdapter adapter = new PickerAdapter(data, position -> pickSelect.setCurrentItem(position, true));
        pickSelect.setAdapter(adapter);
        mSelectDifficulty = PreferencesUtils.getIntegerPreference(this, Constants.GAME_DIFFICULTY, 0);
        pickSelect.setCurrentItem(adapter.getDataCount() * 2000 - 1 + mSelectDifficulty);
        pickSelect.setPageTransformer(false, new ScaleTransformer());
        pickSelect.addOnPageChangeListener(new PageChangedListener() {
            @Override
            public void onSelected(int position) {
                mSelectDifficulty = position % adapter.getDataCount();
                updateResumeView(mSelectMode, mSelectDifficulty);
            }
        });
    }

    private void initSelectMode() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        String[] items = new String[]{
                "随机玩法",
                "解锁玩法"
        };
        tabLayout.setItems(items);
        tabLayout.setOnItemClickListener((view, index) -> {
            mSelectMode = index;
            updateResumeView(index, mSelectDifficulty);
        });
        mSelectMode = PreferencesUtils.getIntegerPreference(this, Constants.GAME_MODE, 0);
        tabLayout.setSelectItem(mSelectMode);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.statistics){
            Intent intent = new Intent(ModeSelectActivity.this, StatisticsActivity.class);
            startActivity(intent);
        }else if(id == R.id.start_game){
            if (mSelectMode == 1){
                Intent intent = new Intent(ModeSelectActivity.this, LevelActivity.class);
                intent.putExtra(Constants.KEY_DIFFICULTY, mSelectDifficulty);
                startActivity(intent);
            } else {
                Intent intent = new Intent(ModeSelectActivity.this, SudoActivity.class);
                int[] nextKey = new int[4];
                nextKey[1] = mSelectDifficulty;
                intent.putExtra(Constants.KEY_EXAM, nextKey);
                startActivity(intent);
            }
        } else if (id == R.id.resume_game){
            Intent intent = new Intent(ModeSelectActivity.this, SudoActivity.class);
            int[] nextKey = new int[4];
            nextKey[0] = mSelectMode;
            nextKey[1] = mSelectDifficulty;
            intent.putExtra(Constants.KEY_EXAM, nextKey);
            intent.putExtra(Constants.KEY_RESUME, true);
            startActivity(intent);
        }
    }
}
