package org.de_studio.recentappswitcher.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;

import butterknife.ButterKnife;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public abstract class BaseActivity extends Activity {
    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        getPresenter().onViewAttach();
    }

    protected abstract void inject();

    protected abstract BasePresenter getPresenter();

    @LayoutRes
    protected abstract int getLayoutId();

    @CallSuper
    @Override
    protected void onDestroy() {
        getPresenter().onViewDetach();
        super.onDestroy();
    }
}
