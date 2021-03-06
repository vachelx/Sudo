package com.vachel.sudo.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.nineoldandroids.view.ViewHelper;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vachel.sudo.R;
import com.vachel.sudo.adapter.MyExpandAdapter;
import com.vachel.sudo.adapter.PickerAdapter;
import com.vachel.sudo.engine.ThreadPoolX;
import com.vachel.sudo.helper.PageChangedListener;
import com.vachel.sudo.manager.ArchiveDataManager;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.helper.ScaleTransformer;
import com.vachel.sudo.utils.EventTag;
import com.vachel.sudo.utils.PreferencesUtils;
import com.vachel.sudo.utils.Utils;
import com.vachel.sudo.widget.BaseAlertDialog;
import com.vachel.sudo.widget.IconView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private int mSelectDifficulty;
    private View mResumeView;
    private DrawerLayout mDrawerLayout;
    private ExpandableListView mExpandList;
    private IconView mIconView;

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
//        mExpandList.expandGroup(0);

        findViewById(R.id.suggestions).setOnClickListener(this);
        findViewById(R.id.support).setOnClickListener(this);
        findViewById(R.id.about).setOnClickListener(this);
        mIconView = findViewById(R.id.icon_view);
//        mIconView.setOnClickListener(this);
    }

    private void updateResumeView(final int difficulty) {
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            emitter.onNext(ArchiveDataManager.getInstance().checkRandomArchiveExist(difficulty));
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<Boolean>autoDisposable(AndroidLifecycleScopeProvider.from(MainActivity.this, Lifecycle.Event.ON_DESTROY)))
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
                PreferencesUtils.setIntegerPrefrence(MainActivity.this, Constants.GAME_DIFFICULTY, mSelectDifficulty);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.statistics) {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        } else if (id == R.id.start_game || id == R.id.resume_game) {
            Intent intent = new Intent(MainActivity.this, SudoActivity.class);
            int[] nextKey = new int[4];
            nextKey[1] = mSelectDifficulty;
            intent.putExtra(Constants.KEY_EXAM, nextKey);
            intent.putExtra(Constants.KEY_RESUME, id == R.id.resume_game);
            startActivity(intent);
        } else if (id == R.id.level_mode) {
            Intent intent = new Intent(MainActivity.this, LevelActivity.class);
            intent.putExtra(Constants.KEY_DIFFICULTY, mSelectDifficulty);
            startActivity(intent);
        } else if(id == R.id.setting){
            mDrawerLayout.openDrawer(Gravity.LEFT);
        } else if (id == R.id.about) {
            new BaseAlertDialog().initDialog("", "Version " + getAppVersion() + "\n\nCopyright © 2020 Vachel Design.\nAll rights reserved.")
                    .contentGravityCenter(true)
                    .show(getSupportFragmentManager(), "");
        } else if (id == R.id.support) {
            Intent intent = new Intent(MainActivity.this, SupportActivity.class);
            startActivity(intent);
        } else if (id == R.id.suggestions) {
            Intent intent = new Intent(MainActivity.this, SuggestionsActivity.class);
            startActivity(intent);
        } else if (id == R.id.icon_view){
            handleSaveClick();
        }
    }

    public String getAppVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName + "."+info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Subscriber(tag = EventTag.ON_ARCHIVE_CHANGED, mode = ThreadMode.MAIN)
    public void onSaveArchive(int[] cruxKey) {
        if (cruxKey[0] == 0 && mSelectDifficulty == cruxKey[1]) {
            updateResumeView(mSelectDifficulty);
        }
    }

    @Subscriber(tag = EventTag.SHOW_RESUME_ARCHIVE_TIPS, mode = ThreadMode.MAIN)
    public void onSaveArchive(boolean show) {
        if (mExpandList.isGroupExpanded(0)){
            mExpandList.collapseGroup(0);
            mExpandList.expandGroup(0);
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkPermission(0);
//        }
//    }

    private void checkPermission(int index) {
        int hasPermission = ContextCompat.checkSelfPermission(this, Utils.mPermissionList[index]);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Utils.mPermissionList[index]}, index + 1);
        } else if (index < Utils.mPermissionList.length - 1) {
            checkPermission(index + 1);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        boolean success = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
////        switch (requestCode) {
////            case 1: // WRITE_EXTERNAL_STORAGE
////                break;
////        }
//        if (requestCode < Utils.mPermissionList.length) {
//            checkPermission(requestCode);
//        }
//    }

    private synchronized Bitmap getQrCodeBitmap() {
        mIconView.setDrawingCacheEnabled(true);
        mIconView.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        Bitmap mQrContentBitmap = mIconView.getDrawingCache();
        mQrContentBitmap = Bitmap.createBitmap(mQrContentBitmap, 0, 0, mIconView.getWidth(), mIconView.getHeight());
        mIconView.setDrawingCacheEnabled(false);
        return mQrContentBitmap;
    }

    private void handleSaveClick() {
        final Bitmap bitmap = getQrCodeBitmap();
        ThreadPoolX.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                String displayName = "icon_bitmap.jpg";
                if (Build.VERSION.SDK_INT >= 29) {
                    Utils.saveBitmapWithAndroidQ(bitmap, displayName);
                } else {
                    Utils.saveBitmapFile(bitmap, Utils.getSaveFilePath(displayName));
                }

            }
        });
    }
}
