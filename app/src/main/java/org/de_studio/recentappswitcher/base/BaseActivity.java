package org.de_studio.recentappswitcher.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import org.de_studio.recentappswitcher.utils.RetainFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public abstract class BaseActivity<T,P extends BasePresenter > extends AppCompatActivity implements PresenterView {

    protected RetainFragment<T> retainFragment;
    String tag = getClass().getCanonicalName();
    protected boolean destroyedBySystem;

    @Inject
    protected P presenter;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        retainFragment = RetainFragment.findOrCreate(getSupportFragmentManager(), tag);
        getDataFromRetainFragment();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        if (presenter != null) {
            presenter.onViewAttach(this);
        }
    }

    protected abstract void inject();


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
        presenter.onViewDetach();
        if(destroyedBySystem) onDestroyBySystem(); else onDestroyByUser();
        super.onDestroy();
    }

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
