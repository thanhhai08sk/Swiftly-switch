package org.de_studio.recentappswitcher.base;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public abstract class BasePresenter<V extends PresenterView, M extends BaseModel> {
    protected V view;
    protected M model;
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    public BasePresenter(M model) {
        this.model = model;
    }

    @CallSuper
    public void onViewAttach(V view) {
        if (this.view != null) {
            throw new IllegalStateException("View " + this.view + " is already attached. Cannot attach " + view);
        }
        this.view = view;
    }

    @CallSuper
    public void onViewDetach(){
        if (view == null) {
            throw new IllegalStateException("View is already detached");
        }
        view.clear();
        if (model != null) {
            model.clear();
        }
        view = null;
        compositeSubscription.clear();
    }

    @CallSuper protected final void addSubscription(@NonNull final Subscription subscription) {
        compositeSubscription.add(subscription);
    }
}
