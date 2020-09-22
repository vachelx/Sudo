package com.vachel.sudo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.vachel.sudo.R;
import com.vachel.sudo.utils.ScreenInfoUtils;
import com.vachel.sudo.utils.Utils;

public abstract class BaseActivity extends AppCompatActivity {
    protected int mStatusBarHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBefore();
        setContentView(getLayoutId());
        initTitleBarIfNeed();
        init();
    }

    private void initTitleBarIfNeed() {
        if (needFullScreenTitleBar()){
            View rootTitle = findViewById(R.id.root_title_bar);
            if (rootTitle != null) {
                rootTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mStatusBarHeight + Utils.dp2px(this, 47)));
            }
        }
    }

    private void initBefore(){
        if (needFullScreenTitleBar()){
            mStatusBarHeight = ScreenInfoUtils.getStatusBarHeight(this);
            //隐藏状态栏
            ScreenInfoUtils.fullScreen(this);
        }
    }

    abstract @LayoutRes int getLayoutId();
    abstract void init();

    public boolean needFullScreenTitleBar() {
        return false;
    }
}
