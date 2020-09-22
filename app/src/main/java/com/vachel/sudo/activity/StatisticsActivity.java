package com.vachel.sudo.activity;

import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.ViewPager;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vachel.sudo.R;
import com.vachel.sudo.adapter.PickerAdapter;
import com.vachel.sudo.bean.AnalyzeResult;
import com.vachel.sudo.dao.Record;
import com.vachel.sudo.helper.PageChangedListener;
import com.vachel.sudo.helper.ScaleTransformer;
import com.vachel.sudo.manager.RecordDataManager;
import com.vachel.sudo.utils.Utils;
import com.vachel.sudo.widget.RecordItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class StatisticsActivity extends BaseActivity {

    private int mSelectDifficulty;
    private int mSelectMode;
    private RecordItem mAveTime;
    private RecordItem mFastTime;
    private RecordItem mSlowTime;
    private RecordItem mLastTime;
    private RecordItem mCount;

    @Override
    int getLayoutId() {
        return R.layout.activity_statistics;
    }

    @Override
    public boolean needFullScreenTitleBar() {
        return true;
    }

    @Override
    void init() {
        mSelectMode = 2;
        mSelectDifficulty = 4;
        initSelectDifficulty();
        initSelectMode();
        mAveTime = findViewById(R.id.ave_time);
        mFastTime = findViewById(R.id.fast_time);
        mSlowTime = findViewById(R.id.slow_time);
        mLastTime = findViewById(R.id.last_time);
        mCount = findViewById(R.id.done_count);
        findViewById(R.id.back_icon).setOnClickListener(v -> onBackPressed());
        initData(mSelectMode, mSelectDifficulty);
    }

    private void initData(final int mode, final int difficulty) {
        Observable.create((ObservableOnSubscribe<AnalyzeResult>) emitter -> {
            emitter.onNext(getAnalyzeData(mode, difficulty));
            emitter.onComplete();
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(StatisticsActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(this::updateViewsText);
    }

    private AnalyzeResult getAnalyzeData(int mode, int difficulty) {
        List<Record> allRecords;
        if (mode == 2 && difficulty == 4) {
            allRecords = RecordDataManager.getInstance().getAllRecords();
        } else if (mode == 2) {
            allRecords = RecordDataManager.getInstance().getFilterRecordsByDifficulty(difficulty);
        } else if (difficulty == 4) {
            allRecords = RecordDataManager.getInstance().getFilterRecordsByMode(mode);
        } else {
            allRecords = RecordDataManager.getInstance().getFilterRecords(mode, difficulty);
        }

        AnalyzeResult result = new AnalyzeResult();
        if (allRecords != null && allRecords.size() > 0) {
            long takeTime = 0;
            long fastTime = allRecords.get(0).getTakeTime();
            long slowTime = allRecords.get(0).getTakeTime();
            long lastPlayTime = allRecords.get(0).getFinishDate();
            for (Record record : allRecords) {
                takeTime += record.getTakeTime();
                if (record.getTakeTime() < fastTime) {
                    fastTime = record.getTakeTime();
                }

                if (record.getTakeTime() > slowTime) {
                    slowTime = record.getTakeTime();
                }
                long doneTime = record.getFinishDate();
                if (doneTime > lastPlayTime) {
                    lastPlayTime = doneTime;
                }
            }
            result.setAveTake(takeTime / allRecords.size());
            result.setFastTake(fastTime);
            result.setLastTime(lastPlayTime);
            result.setSlowTime(slowTime);
            result.setSize(allRecords.size());
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
        }
        return result;
    }

    private void updateViewsText(AnalyzeResult analyzeResult) {
        if (analyzeResult.isSuccess()) {
            mAveTime.setItemValue(Utils.parseTakeTime(analyzeResult.getAveTake(), 1));
            mFastTime.setItemValue(Utils.parseTakeTime(analyzeResult.getFastTake(), 1));
            mSlowTime.setItemValue(Utils.parseTakeTime(analyzeResult.getSlowTime(), 1));
            mLastTime.setItemValue(Utils.parseDate(analyzeResult.getLastTime()));
            mCount.setItemValue(analyzeResult.getSize()+"");
        } else {
            String nullTime = StatisticsActivity.this.getString(R.string.default_take_time);
            mAveTime.setItemValue(nullTime);
            mFastTime.setItemValue(nullTime);
            mSlowTime.setItemValue(nullTime);
            mLastTime.setItemValue("暂无记录");
            mCount.setItemValue("0");
        }
    }

    private void initSelectMode() {
        final ViewPager pickSelect = findViewById(R.id.pick_mode);
        ArrayList<String> data = new ArrayList<>();
        data.add("随机模式");
        data.add("闯关玩法");
        data.add("综合");
        final PickerAdapter adapter = new PickerAdapter(data, position -> pickSelect.setCurrentItem(position, true));
        pickSelect.setAdapter(adapter);
        pickSelect.setCurrentItem(adapter.getDataCount() * 2000 - 1 + mSelectMode);
        pickSelect.setPageTransformer(false, new ScaleTransformer());
        pickSelect.addOnPageChangeListener(new PageChangedListener() {
            @Override
            public void onSelected(int position) {
                mSelectMode = position % adapter.getDataCount();
                initData(mSelectMode, mSelectDifficulty);
            }
        });
    }

    private void initSelectDifficulty() {
        final ViewPager pickSelect = findViewById(R.id.pick_difficulty);
        ArrayList<String> data = new ArrayList<>();
        data.add("简单");
        data.add("中等");
        data.add("困难");
        data.add("专家");
        data.add("综合");
        final PickerAdapter adapter = new PickerAdapter(data, position -> pickSelect.setCurrentItem(position, true));
        pickSelect.setAdapter(adapter);
        pickSelect.setCurrentItem(adapter.getDataCount() * 2000 - 1 + mSelectDifficulty);
        pickSelect.setPageTransformer(false, new ScaleTransformer());
        pickSelect.addOnPageChangeListener(new PageChangedListener() {
            @Override
            public void onSelected(int position) {
                mSelectDifficulty = position % adapter.getDataCount();
                initData(mSelectMode, mSelectDifficulty);
            }
        });
    }
}
