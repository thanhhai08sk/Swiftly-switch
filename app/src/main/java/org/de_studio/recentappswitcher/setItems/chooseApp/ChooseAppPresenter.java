package org.de_studio.recentappswitcher.setItems.chooseApp;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseChooseItemPresenter;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class ChooseAppPresenter extends BaseChooseItemPresenter {
    private static final String TAG = ChooseAppPresenter.class.getSimpleName();

    public ChooseAppPresenter(BaseModel model) {
        super(model);
    }

    @Override
    protected RealmResults<Item> getItemRealmResult() {
        return realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_APP).findAllSortedAsync(Cons.LABEL);
    }
}
