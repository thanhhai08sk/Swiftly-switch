package org.de_studio.recentappswitcher.setItems.chooseContact;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseChooseItemPresenter;
import org.de_studio.recentappswitcher.base.BaseChooseItemView;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public class ChooseContactPresenter extends BaseChooseItemPresenter {
    public ChooseContactPresenter(BaseChooseItemView view) {
        super(view);
    }

    @Override
    protected RealmResults<Item> getItemRealmResult() {
        return realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_CONTACT).findAllSortedAsync(Cons.LABEL);
    }
}
