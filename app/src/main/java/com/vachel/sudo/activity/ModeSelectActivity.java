package com.vachel.sudo.activity;

import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.nineoldandroids.view.ViewHelper;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vachel.sudo.R;
import com.vachel.sudo.adapter.MyExpandAdapter;
import com.vachel.sudo.adapter.PickerAdapter;
import com.vachel.sudo.helper.PageChangedListener;
import com.vachel.sudo.manager.ArchiveDataManager;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.helper.ScaleTransformer;
import com.vachel.sudo.utils.EventTag;
import com.vachel.sudo.utils.PreferencesUtils;
import com.vachel.sudo.utils.Utils;
import com.vachel.sudo.widget.BaseAlertDialog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ModeSelectActivity extends BaseActivity implements View.OnClickListener {
    private int mSelectDifficulty;
    private View mResumeView;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandList;

    @Override
    int getLayoutId() {
        return R.layout.activity_mode_select;
    }

    @Override
    public boolean needFullScreenTitleBar() {
        return true;
    }

    @Override
    void init() {
        // 初始化模拟状态栏高度
        View statusBar = findViewById(R.id.view_status_bar);
        statusBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mStatusBarHeight));
        View slideStatusBar = findViewById(R.id.root_title_bar);
        slideStatusBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mStatusBarHeight+ Utils.dp2px(this, 47)));

        EventBus.getDefault().register(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                ViewHelper.setTranslationX(mContent, drawerView.getMeasuredWidth() * slideOffset);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mResumeView = findViewById(R.id.resume_game);
        initSelectDifficulty();
        updateResumeView(mSelectDifficulty);
        mResumeView.setOnClickListener(this);
        findViewById(R.id.statistics).setOnClickListener(this);
        findViewById(R.id.start_game).setOnClickListener(this);
        findViewById(R.id.level_mode).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);

        mExpandList = findViewById(R.id.expand_list);
        mExpandList.setGroupIndicator(null);
        mExpandList.setAdapter(new MyExpandAdapter(this));

        findViewById(R.id.suggestions).setOnClickListener(this);
        findViewById(R.id.support).setOnClickListener(this);
        findViewById(R.id.about).setOnClickListener(this);
    }

    private void updateResumeView(final int difficulty) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            emitter.onNext(ArchiveDataManager.getInstance().checkRandomArchiveExist(difficulty));
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<Boolean>autoDisposable(AndroidLifecycleScopeProvider.from(ModeSelectActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(result -> {
                    mResumeView.setClickable(result);
                    mResumeView.setVisibility(result ? View.VISIBLE : View.INVISIBLE);
                });
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
                updateResumeView(mSelectDifficulty);
                PreferencesUtils.setIntegerPrefrence(ModeSelectActivity.this, Constants.GAME_DIFFICULTY, mSelectDifficulty);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.statistics) {
            Intent intent = new Intent(ModeSelectActivity.this, StatisticsActivity.class);
            startActivity(intent);
        } else if (id == R.id.start_game || id == R.id.resume_game) {
            Intent intent = new Intent(ModeSelectActivity.this, SudoActivity.class);
            int[] nextKey = new int[4];
            nextKey[1] = mSelectDifficulty;
            intent.putExtra(Constants.KEY_EXAM, nextKey);
            intent.putExtra(Constants.KEY_RESUME, id == R.id.resume_game);
            startActivity(intent);
        } else if (id == R.id.level_mode) {
            Intent intent = new Intent(ModeSelectActivity.this, LevelActivity.class);
            intent.putExtra(Constants.KEY_DIFFICULTY, mSelectDifficulty);
            startActivity(intent);
        } else if(id == R.id.setting){
            mDrawerLayout.openDrawer(Gravity.LEFT);
        } else if (id == R.id.about) {
            new BaseAlertDialog().initDialog("", "Version 1.0.0\n\nCopyright © 2020 Vachel Design.\nAll rights reserved.")
                    .contentGravityCenter(true)
                    .show(getSupportFragmentManager(), "");
        } else if (id == R.id.support) {
            Intent intent = new Intent(ModeSelectActivity.this, SupportActivity.class);
            startActivity(intent);
//            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else if (id == R.id.suggestions) {

        }
    }

    @Subscriber(tag = EventTag.ON_ARCHIVE_CHANGED, mode = ThreadMode.MAIN)
    public void onSaveArchive(int[] cruxKey) {
        if (cruxKey[0] == 0 && mSelectDifficulty == cruxKey[1]) {
            updateResumeView(mSelectDifficulty);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        super.onBackPressed();
    }
}
