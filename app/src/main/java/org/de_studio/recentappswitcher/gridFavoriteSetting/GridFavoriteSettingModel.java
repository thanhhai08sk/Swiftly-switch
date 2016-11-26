package org.de_studio.recentappswitcher.gridFavoriteSetting;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.Random;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public class GridFavoriteSettingModel extends BaseCollectionSettingModel {
    private static final String TAG = GridFavoriteSettingModel.class.getSimpleName();

    public GridFavoriteSettingModel(String defaultLabel, String collectionId) {
        super(defaultLabel, collectionId);
    }

    @Override
    public String getCollectionType() {
        return Collection.TYPE_GRID_FAVORITE;
    }

    @Override
    public String createNewCollection() {
        final long newCollectionNumber = realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType()).count() + 1;
        final String newLabel = Utility.createCollectionLabel(defaultLabel, newCollectionNumber);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Collection collection = new Collection();
                collection.type = getCollectionType();
                collection.collectionId = Utility.createCollectionId(getCollectionType(), newCollectionNumber);
                collection.label = newLabel;
                collection.rowsCount = Cons.DEFAULT_FAVORITE_GRID_ROW_COUNT;
                collection.columnCount = Cons.DEFAULT_FAVORITE_GRID_COLUMN_COUNT;
                collection.longClickMode = Collection.LONG_CLICK_MODE_NONE;
                Collection realmCollection = realm.copyToRealm(collection);

                for (int i = 0; i < collection.rowsCount * collection.columnCount; i++) {
                    Slot nullSlot = new Slot();
                    nullSlot.type = Slot.TYPE_NULL;
                    nullSlot.slotId = String.valueOf(System.currentTimeMillis() + new Random().nextLong());
                    Log.e(TAG, "new slot, id = " + nullSlot.slotId);
                    Slot realmSlot = realm.copyToRealm(nullSlot);
                    realmCollection.slots.add(realmSlot);
                }
            }
        });
        return newLabel;
    }

}
