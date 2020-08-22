package com.vachel.sudo.activity;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.vachel.sudo.R;
import com.vachel.sudo.bean.AnalyzeResult;
import com.vachel.sudo.dao.Record;
import com.vachel.sudo.manager.RecordDataManager;
import com.vachel.sudo.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class StatisticsActivity extends BaseActivity {

    private List<Record> mAllRecords;

    @Override
    int getLayoutId() {
        return R.layout.activity_statistics;
    }

    @Override
    void init() {
        final TextView aveText = findViewById(R.id.ave_text);
        final TextView fastText = findViewById(R.id.fast_text);
        final TextView lastText = findViewById(R.id.last_text);
        Observable.create(new ObservableOnSubscribe<AnalyzeResult>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<AnalyzeResult> emitter) {
                mAllRecords = RecordDataManager.getInstance().getAllRecords();
                AnalyzeResult result = new AnalyzeResult();
                if (mAllRecords.size()> 0){
                    long takeTime = 0;
                    long fastTime = mAllRecords.get(0).getTakeTime();
                    long lastPlayTime = mAllRecords.get(0).getFinishDate();
                    for (Record record : mAllRecords) {
                        takeTime += record.getTakeTime();
                        if (record.getTakeTime() < fastTime) {
                            fastTime = record.getTakeTime();
                        }
                        long doneTime = record.getFinishDate();
                        if (doneTime> lastPlayTime){
                            lastPlayTime = doneTime;
                        }
                    }
                    result.setAveTake(takeTime/ mAllRecords.size());
                    result.setFastTake(fastTime);
                    result.setLastTime(lastPlayTime);
                    result.setSuccess(true);
                } else {
                    result.setSuccess(false);
                }
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.<AnalyzeResult>autoDisposable(AndroidLifecycleScopeProvider.from(StatisticsActivity.this, Lifecycle.Event.ON_DESTROY)))
                .subscribe(new Consumer<AnalyzeResult>() {
                    @Override
                    public void accept(AnalyzeResult analyzeResult) {
                        if (analyzeResult.isSuccess()){
                            aveText.setText( "平均用时："+Utils.parseTakeTime(analyzeResult.getAveTake(), 1)+ "; 闯了"+ mAllRecords.size()+"关");
                            fastText.setText("最快用时："+Utils.parseTakeTime(analyzeResult.getFastTake(), 1));
                            lastText.setText("最近一次："+Utils.parseDate(analyzeResult.getLastTime()));
                        }else {

                        }
                    }
                });

    }
}
