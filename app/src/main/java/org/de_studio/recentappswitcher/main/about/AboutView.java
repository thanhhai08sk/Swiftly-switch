package org.de_studio.recentappswitcher.main.about;

import android.os.Bundle;
import android.widget.TextView;

import org.de_studio.recentappswitcher.BuildConfig;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseActivity;

import butterknife.BindView;

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

}
