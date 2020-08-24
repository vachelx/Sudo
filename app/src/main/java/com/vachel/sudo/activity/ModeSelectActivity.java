package com.vachel.sudo.activity;

import android.content.Intent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.vachel.sudo.R;
import com.vachel.sudo.adapter.PickerAdapter;
import com.vachel.sudo.helper.PageChangedListener;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.helper.ScaleTransformer;
import com.vachel.sudo.utils.PreferencesUtils;

import java.util.ArrayList;

public class ModeSelectActivity extends BaseActivity implements View.OnClickListener {
    private int mSelectMode;
    private int mSelectDifficulty;

    @Override
    int getLayoutId() {
        return R.layout.activity_mode_select;
    }

    @Override
    void init() {
        final ViewPager pickMode = findViewById(R.id.pick_mode);
        ArrayList<String> modeData = new ArrayList<>();
        modeData.add("随机模式");
        modeData.add("解锁玩法");
        final PickerAdapter modeAdapter = new PickerAdapter(modeData, position -> pickMode.setCurrentItem(position, true));
        pickMode.setAdapter(modeAdapter);
        mSelectMode = PreferencesUtils.getIntegerPreference(this, Constants.GAME_MODE, 0);
        pickMode.setCurrentItem(modeAdapter.getDataCount() * 20000 - 1 + mSelectMode);
        pickMode.setPageTransformer(false, new ScaleTransformer());
        pickMode.addOnPageChangeListener(new PageChangedListener() {
            @Override
            public void onSelected(int position) {
                mSelectMode = position % modeAdapter.getDataCount();
            }
        });

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
            }
        });

        findViewById(R.id.statistics).setOnClickListener(this);
        findViewById(R.id.start_game).setOnClickListener(this);
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
                nextKey[0] = 0;
                nextKey[1] = mSelectDifficulty;
                nextKey[2] = 0;
                nextKey[3] = 0;
                intent.putExtra(Constants.KEY_EXAM, nextKey);
                startActivity(intent);
            }
        }
    }
}
