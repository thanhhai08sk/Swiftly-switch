package org.de_studio.recentappswitcher.setItems.chooseShortcutsSet;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseChooseItemPresenter;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class ChooseShortcutsSetPresenter extends BaseChooseItemPresenter {
    private static final String TAG = ChooseShortcutsSetPresenter.class.getSimpleName();
    String collectionType;

    public ChooseShortcutsSetPresenter(BaseModel model, String collectionType) {
        super(model);
        this.collectionType = collectionType;
    }

    @Override
    protected RealmResults<Item> getItemRealmResult() {
        if (collectionType == null) {

            Log.e(TAG, "getItemRealmResult: collection null");
        }
        switch (collectionType) {
            case Collection.TYPE_RECENT:
                return realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_SHORTCUTS_SET)
                        .contains(Cons.COLLECTION_ID, Collection.TYPE_GRID_FAVORITE)
                        .or()
                        .contains(Cons.COLLECTION_ID, Collection.TYPE_CIRCLE_FAVORITE)
                        .findAllAsync();
            case Collection.TYPE_CIRCLE_FAVORITE:
                return realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_SHORTCUTS_SET)
                        .contains(Cons.COLLECTION_ID, Collection.TYPE_GRID_FAVORITE)
                        .or()
                        .contains(Cons.COLLECTION_ID, Collection.TYPE_RECENT)
                        .findAllAsync();
            default:
                return realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_SHORTCUTS_SET)
                        .findAllAsync();
        }
    }
}
