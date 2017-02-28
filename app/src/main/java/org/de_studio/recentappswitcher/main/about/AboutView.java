package org.de_studio.recentappswitcher.main.about;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.de_studio.recentappswitcher.BuildConfig;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by HaiNguyen on 2/28/17.
 */

public class AboutView extends BaseActivity {
    @BindView(R.id.app_version)
    TextView appVersionDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appVersionDescription.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    protected void inject() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.about_view;
    }

    @Override
    public void getDataFromRetainFragment() {

    }

    @Override
    public void onDestroyBySystem() {

    }

    @Override
    public void clear() {

    }
    
    @OnClick(R.id.privacy)
    void onPrivacyClick(){
        MaterialDialog dei =new  MaterialDialog.Builder(this).customView(R.layout.privacy_web_view, false)
                .positiveText(R.string.app_tab_fragment_ok_button)
                .show();
        View view = dei.getCustomView();
        WebView webView = (WebView) view.findViewById(R.id.webview);
        webView.loadUrl("https://www.facebook.com/notes/de-studio/privacy-policy-for-swiftly-switch/733550620153464");

    }

}
