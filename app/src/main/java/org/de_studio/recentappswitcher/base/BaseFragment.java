package org.de_studio.recentappswitcher.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by HaiNguyen on 11/19/16.
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements PresenterView{
    Unbinder unbinder;
    @Inject
    P presenter;
    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
    }

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        unbinder = ButterKnife.bind(this, view);
        presenter.onViewAttach(this);
        return view;
    }
    @CallSuper
    @Override
    public void onDestroy() {
        presenter.onViewDetach();
        super.onDestroy();
    }

    protected abstract int getLayoutRes();

    protected abstract void inject();
    @CallSuper
    public void clear(){
        unbinder.unbind();
    }
}
