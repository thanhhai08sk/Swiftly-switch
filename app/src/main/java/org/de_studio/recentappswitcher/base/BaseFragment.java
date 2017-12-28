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
    private static final String TAG = BaseFragment.class.getSimpleName();
    Unbinder unbinder;
    @Inject
    protected P presenter;
    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onViewAttach(this);
    }

    @Override
    public void onDestroyView() {
        presenter.onViewDetach();
        super.onDestroyView();
    }

    @CallSuper
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    protected abstract int getLayoutRes();

    protected abstract void inject();
    @CallSuper
    public void clear(){
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
