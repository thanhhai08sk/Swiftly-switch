package org.de_studio.recentappswitcher.circleFavoriteSetting;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.Random;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class CircleFavoriteSettingModel extends BaseCollectionSettingModel {
    private static final String TAG = CircleFavoriteSettingModel.class.getSimpleName();

    public CircleFavoriteSettingModel(String defaultLabel, String collectionId) {
        super(defaultLabel, collectionId);
    }



    public String getCollectionType() {
        return Collection.TYPE_CIRCLE_FAVORITE;
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
                collection.longClickMode = Collection.LONG_CLICK_MODE_NONE;
                collection.radius = Cons.CIRCLE_RADIUS_DEFAULT;
                Collection realmCollection = realm.copyToRealm(collection);

                for (int i = 0; i < 6; i++) {
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
