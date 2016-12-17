package org.de_studio.recentappswitcher.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public abstract class BaseDialogFragment<V extends PresenterView> extends DialogFragment {
    private static final String TAG = BaseFragment.class.getSimpleName();
    Unbinder unbinder;
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
        getPresenter().onViewAttach(getPresenterView());
        return view;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @CallSuper
    @Override
    public void onDestroy() {
        getPresenter().onViewDetach();
        super.onDestroy();
    }

    protected abstract int getLayoutRes();

    protected abstract BasePresenter getPresenter();

    protected abstract V getPresenterView();
    protected abstract void inject();
    @CallSuper
    public void clear(){
        unbinder.unbind();
    }

}
