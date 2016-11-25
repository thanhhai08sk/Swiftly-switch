package org.de_studio.recentappswitcher.base;

import org.de_studio.recentappswitcher.model.Item;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.functions.Action1;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public abstract class BaseChooseItemPresenter extends BasePresenter {
    protected BaseChooseItemView view;
    protected RealmResults<Item> results;
    protected Realm realm = Realm.getDefaultInstance();

    public BaseChooseItemPresenter(BaseChooseItemView view) {
        this.view = view;
    }

    @Override
    public void onViewAttach() {
        view.loadItems();
        view.setProgressBar(true);
        results = getItemRealmResult();
        if (results != null) {
            view.setAdapter(results);
            results.addChangeListener(new RealmChangeListener<RealmResults<Item>>() {
                @Override
                public void onChange(RealmResults<Item> element) {
                    if (element.size()>0) {
                        view.setProgressBar(false);
                    }
                }
            });
        }

        addSubscription(
                view.onCurrentItemChange().subscribe(new Action1<Item>() {
                    @Override
                    public void call(Item item) {
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
        if (results != null) {
            results.removeChangeListeners();
        }
        view.clear();
        realm.close();
        view = null;
    }

    protected abstract RealmResults<Item> getItemRealmResult();
}
