package com.vachel.sudo.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.view.View;

import com.vachel.sudo.R;
import com.vachel.sudo.utils.ToastUtil;

import java.net.URISyntaxException;

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
        findViewById(R.id.support_pay).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_icon) {
            onBackPressed();
        }else if (v.getId() == R.id.support_pay){
            jumpToSupport();
        }
    }

    private void jumpToSupport(){
        try {
            String intentUrl = URL_FORMAT.replace("{urlCode}", "fkx19104h39ewbn4uuq75ab");
            Intent intent = Intent.parseUri(intentUrl, Intent.URI_INTENT_SCHEME);
            startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private static final String URL_FORMAT =
            "intent://platformapi/startapp?saId=10000007&" +
                    "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s" +
                    "%3Dweb-other&_t=1472443966571#Intent;" + "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
}
