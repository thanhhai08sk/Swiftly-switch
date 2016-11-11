package org.de_studio.recentappswitcher.base;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public abstract class BasePresenter {
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();


    public abstract void onViewAttach();

    @CallSuper
    public void onViewDetach(){
        compositeSubscription.clear();
    }

    @CallSuper protected final void addSubscription(@NonNull final Subscription subscription) {
        compositeSubscription.add(subscription);
    }
}
