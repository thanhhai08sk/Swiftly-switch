package org.de_studio.recentappswitcher.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import org.de_studio.recentappswitcher.utils.RetainFragment;

import butterknife.ButterKnife;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public abstract class BaseActivity<T> extends AppCompatActivity {

    protected RetainFragment<T> retainFragment;
    String tag = getClass().getCanonicalName();
    protected boolean destroyedBySystem;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        retainFragment = RetainFragment.findOrCreate(getSupportFragmentManager(), tag);
        getDataFromRetainFragment();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        destroyedBySystem = true;
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        destroyedBySystem = false;
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        getPresenter().onViewDetach();
        if(destroyedBySystem) onDestroyBySystem(); else onDestroyByUser();
        super.onDestroy();
    }

    protected abstract void clear();

    public T getData(){ return retainFragment.data; }
    public void setData(T data){ retainFragment.data = data; }

    public void onDestroyByUser(){
        retainFragment.remove(getSupportFragmentManager());
        retainFragment.data = null;
        retainFragment = null;
    }

    public abstract void getDataFromRetainFragment();

    public abstract void onDestroyBySystem();

}
