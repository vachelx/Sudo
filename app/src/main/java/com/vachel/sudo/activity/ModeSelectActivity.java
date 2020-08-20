package com.vachel.sudo.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.vachel.sudo.R;

public class ModeSelectActivity extends BaseActivity {
    private int[] modes = new int[]{
            R.id.mode_0,
            R.id.mode_1,
            R.id.mode_2,
            R.id.mode_3};

    @Override
    int getLayoutId() {
        return R.layout.activity_mode_select;
    }

    @Override
    void init() {
        for (int i = 0; i<modes.length;i++){
            final int mode = i;
            TextView modeView = findViewById(modes[i]);
            modeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ModeSelectActivity.this, SudoActivity.class);
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                }
            });
        }
    }
}
