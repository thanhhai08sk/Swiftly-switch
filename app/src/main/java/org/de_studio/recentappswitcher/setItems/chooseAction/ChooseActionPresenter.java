package org.de_studio.recentappswitcher.setItems.chooseAction;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;

/**
 * Created by HaiNguyen on 11/19/16.
 */

public class ChooseActionPresenter extends BasePresenter {
    ChooseActionView view;
    RealmResults<Item> results;
    Realm realm = Realm.getDefaultInstance();

    public ChooseActionPresenter(ChooseActionView view) {
        this.view = view;
    }

    @Override
    public void onViewAttach() {
        view.loadActions();
        results = realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_ACTION).findAllAsync();
        view.setAdapter(results);

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
        view.clear();
        realm.close();
        view = null;
    }
}
