package com.vachel.sudo.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.view.View;

import com.vachel.sudo.R;
import com.vachel.sudo.utils.ToastUtil;

public class SupportActivity extends BaseActivity implements View.OnClickListener {
    @Override
    int getLayoutId() {
        return R.layout.activity_support;
    }

    @Override
    public boolean needFullScreenTitleBar() {
        return true;
    }

    @Override
    void init() {
        findViewById(R.id.contact_email).setOnLongClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(SupportActivity.CLIPBOARD_SERVICE);
            if (cm != null) {
                ClipData mClipData = ClipData.newPlainText("Label", getString(R.string.support_contact));
                cm.setPrimaryClip(mClipData);
                ToastUtil.showShortToast(SupportActivity.this, R.string.copy_support_contact);
            }
            return true;
        });

        findViewById(R.id.contact_qq).setOnLongClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(SupportActivity.CLIPBOARD_SERVICE);
            if (cm != null) {
                ClipData mClipData = ClipData.newPlainText("Label", getString(R.string.contact_qq));
                cm.setPrimaryClip(mClipData);
                ToastUtil.showShortToast(SupportActivity.this, R.string.copy_contact_qq);
            }
            return true;
        });
        findViewById(R.id.back_icon).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_icon) {
            onBackPressed();
        }
    }
}
