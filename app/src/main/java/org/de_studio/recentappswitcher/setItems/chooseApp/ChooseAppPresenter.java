package org.de_studio.recentappswitcher.setItems.chooseApp;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.functions.Action1;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class ChooseAppPresenter extends BasePresenter {
    private static final String TAG = ChooseAppPresenter.class.getSimpleName();
    ChooseAppView view;
    ChooseAppModel model;
    Realm realm = Realm.getDefaultInstance();
    RealmResults<Item> results;

    public ChooseAppPresenter(ChooseAppView view, ChooseAppModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onViewAttach() {
        view.loadApps();
        view.setProgressBar(true);
        results = realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_APP).findAllSortedAsync(Cons.LABEL);
        view.setAdapter(results);
        results.addChangeListener(new RealmChangeListener<RealmResults<Item>>() {
            @Override
            public void onChange(RealmResults<Item> element) {
                if (element.size()>0) {
                    view.setProgressBar(false);
                }
            }
        });

        addSubscription(
                view.onCurrentItemChange().subscribe(new Action1<Item>() {
                    @Override
                    public void call(Item item) {
                        Log.e(TAG, "call: on current item change " + item.label);
                        view.setCurrentItem(item);
                    }
                })
        );
    }

    public void onItemClick(Item item) {
        if (item != null) {
            view.onSetItemSubject().onNext(item);
        }
    }

    @Override
    public void onViewDetach() {
        super.onViewDetach();
        results.removeChangeListeners();
        view.clear();
        model.clear();
        realm.close();
        view = null;
    }
}
