package org.de_studio.recentappswitcher.setItems.chooseShortcut;

import org.de_studio.recentappswitcher.base.BaseChooseItemPresenter;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public class ChooseShortcutPresenter extends BaseChooseItemPresenter {
    public ChooseShortcutPresenter(BaseModel model) {
        super(model);
    }

    @Override
    protected RealmResults<Item> getItemRealmResult() {
        return null;
    }
}
