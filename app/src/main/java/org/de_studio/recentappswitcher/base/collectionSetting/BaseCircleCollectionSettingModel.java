package org.de_studio.recentappswitcher.base.collectionSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 2/17/17.
 */

public abstract class BaseCircleCollectionSettingModel extends BaseCollectionSettingModel {

    public BaseCircleCollectionSettingModel(String defaultLabel, String collectionId) {
        super(defaultLabel, collectionId);
    }

    public void setLongPress(final int mode, final Item shortcutSetItem) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, getCollectionId()).findFirst();
                if (shortcutSetItem != null && shortcutSetItem.type.equals(Item.TYPE_SHORTCUTS_SET)) {
                    Collection longPressCollection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, shortcutSetItem.collectionId).findFirst();
                    if (longPressCollection != null) {
                        collection.longPressCollection = longPressCollection;
                    }
                }
                collection.longClickMode = mode;
                if (mode == Collection.LONG_CLICK_MODE_NONE) {
                    collection.longPressCollection = null;
                }
            }
        });
    }
}
