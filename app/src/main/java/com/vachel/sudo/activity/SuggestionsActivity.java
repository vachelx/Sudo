package com.vachel.sudo.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tencent.bugly.crashreport.CrashReport;
import com.vachel.sudo.R;
import com.vachel.sudo.engine.ThreadPoolX;
import com.vachel.sudo.utils.Constants;
import com.vachel.sudo.utils.PreferencesUtils;
import com.vachel.sudo.utils.ToastUtil;
import com.vachel.sudo.utils.Utils;

public class SuggestionsActivity extends BaseActivity implements View.OnClickListener {
    private EditText mSuggestionText;
    private EditText mUserContact;

    @Override
    int getLayoutId() {
        return R.layout.activity_suggestions;
    }

    @Override
    public boolean needFullScreenTitleBar() {
        return true;
    }

    @Override
    void init() {
        findViewById(R.id.back_icon).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
        mSuggestionText = findViewById(R.id.suggestion_text);
        mUserContact = findViewById(R.id.user_contact);
        TextView mLimitLength = findViewById(R.id.suggestion_length);
        mSuggestionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mLimitLength.setText(s.toString().length() + "/600");
            }
        });
        String suggestionText = PreferencesUtils.getStringPreference(this, Constants.SUGGESTION_TEXT, null);
        String contactText = PreferencesUtils.getStringPreference(this, Constants.SUGGESTION_USER_CONTACT, null);
        if (!TextUtils.isEmpty(suggestionText)) {
            mSuggestionText.setText(suggestionText);
        }
        if (!TextUtils.isEmpty(contactText)) {
            mUserContact.setText(contactText);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_icon) {
            onBackPressed();
        } else if (id == R.id.submit) {
            if (!Utils.checkNetworkAvailable(this)) {
                ToastUtil.showShortToast(this, R.string.network_error);
                return;
            }
            String text = mSuggestionText.getText().toString();
            String user = mUserContact.getText().toString();
            if (TextUtils.isEmpty(text)) {
                ToastUtil.showShortToast(this, R.string.check_suggestion_empty);
                return;
            }
            final String tips = TextUtils.isEmpty(user) ? text : "contact:" + user + ";" + text;
            ThreadPoolX.getThreadPool().execute(() -> {
                try {
                    Throwable throwable = new Throwable(tips);
                    CrashReport.postCatchedException(throwable);
                    runOnUiThread(() -> {
                        ToastUtil.showLongToast(SuggestionsActivity.this, R.string.submit_success);
                        mUserContact.setText(null);
                        mSuggestionText.setText(null);
                        PreferencesUtils.setStringPreference(this, Constants.SUGGESTION_TEXT, "");
                        PreferencesUtils.setStringPreference(this, Constants.SUGGESTION_USER_CONTACT, "");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showShortToast(this, R.string.submit_failed);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String text = mSuggestionText.getText().toString();
        String user = mUserContact.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            PreferencesUtils.setStringPreference(this, Constants.SUGGESTION_TEXT, text);
        }
        if (!TextUtils.isEmpty(user)) {
            PreferencesUtils.setStringPreference(this, Constants.SUGGESTION_USER_CONTACT, user);
        }
    }
}
