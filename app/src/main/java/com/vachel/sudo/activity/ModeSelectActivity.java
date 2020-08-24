package com.vachel.sudo.activity;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.vachel.sudo.R;
import com.vachel.sudo.adapter.DifficultyAdapter;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.transformer.ScaleTransformer;

public class ModeSelectActivity extends BaseActivity implements View.OnClickListener {
    private int[] mDifficulty = new int[]{
            R.id.difficulty_0,
            R.id.difficulty_1,
            R.id.difficulty_2,
            R.id.difficulty_3};

    @Override
    int getLayoutId() {
        return R.layout.activity_mode_select;
    }

    @Override
    void init() {
        final CheckBox mode = findViewById(R.id.mode_select);
        for (int i = 0; i< mDifficulty.length; i++){
            final int difficulty = i;
            TextView difficultyView = findViewById(mDifficulty[i]);
            difficultyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode.isChecked()){
                        Intent intent = new Intent(ModeSelectActivity.this, LevelActivity.class);
                        intent.putExtra(Constants.KEY_DIFFICULTY, difficulty);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ModeSelectActivity.this, SudoActivity.class);
                        int[] nextKey = new int[4];
                        nextKey[0] = 0;
                        nextKey[1] = difficulty;
                        nextKey[2] = 0;
                        nextKey[3] = 0;
                        intent.putExtra(Constants.KEY_EXAM, nextKey);
                        startActivity(intent);
                    }
                }
            });
        }

        findViewById(R.id.statistics).setOnClickListener(this);
        final ViewPager pickSelect = findViewById(R.id.pick_select);
        final DifficultyAdapter adapter = new DifficultyAdapter();
        pickSelect.setOffscreenPageLimit(3);
        pickSelect.setAdapter(adapter);
        pickSelect.setCurrentItem(adapter.getDataCount()*2000);
        pickSelect.setPageTransformer(false, new ScaleTransformer());
//        pickSelect.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.statistics){
            Intent intent = new Intent(ModeSelectActivity.this, StatisticsActivity.class);
            startActivity(intent);
        }
    }
}
