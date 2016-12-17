package org.de_studio.recentappswitcher.setItems.chooseShortcutsSet;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseChooseItemPresenter;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class ChooseShortcutsSetPresenter extends BaseChooseItemPresenter {

    public ChooseShortcutsSetPresenter(BaseModel model) {
        super(model);
    }

    @Override
    protected RealmResults<Item> getItemRealmResult() {
        return realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_SHORTCUTS_SET).findAllAsync();
    }
}
