package org.de_studio.recentappswitcher.recentSetting;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCircleCollectionSettingModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.Random;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class RecentSettingModel extends BaseCircleCollectionSettingModel {
    private static final String TAG = RecentSettingModel.class.getSimpleName();
    public RecentSettingModel(String defaultLabel, String collectionId) {
        super(defaultLabel, collectionId);
    }

    @Override
    public String getCollectionType() {
        return Collection.TYPE_RECENT;
    }

    @Override
    public String createNewCollection() {
        long newCollectionNumber = realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType()).count() + 1;
        if (newCollectionNumber != 1) {
            Random random = new Random();
            newCollectionNumber = random.nextInt(999) + 2;
            while (realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(getCollectionType(), newCollectionNumber)).findFirst() != null) {
                newCollectionNumber = random.nextInt(999) + 2;
            }
        }
        final String newLabel = Utility.createCollectionLabel(defaultLabel, newCollectionNumber);
        final String newId = Utility.createCollectionId(getCollectionType(), newCollectionNumber);
        collectionId = newId;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Collection collection = new Collection();
                collection.type = getCollectionType();
                collection.collectionId = newId;
                collection.label = newLabel;
                collection.longClickMode = Collection.LONG_CLICK_MODE_NONE;
                collection.radius = Cons.CIRCLE_RADIUS_DEFAULT;
                Collection realmCollection = realm.copyToRealm(collection);

                for (int i = 0; i < 6; i++) {
                    Slot recentSlot = new Slot();
                    recentSlot.type = Slot.TYPE_RECENT;
                    recentSlot.slotId = String.valueOf(System.currentTimeMillis() + new Random().nextLong());
                    Log.e(TAG, "new slot, id = " + recentSlot.slotId);
                    Slot realmSlot = realm.copyToRealm(recentSlot);
                    realmCollection.slots.add(realmSlot);
                }
            }
        });
        return newId;
    }

    @Override
    public void removeItem(int position) {
        realm.beginTransaction();
        Slot removeSlot = collection.slots.get(position);
        if (!removeSlot.type.equals(Slot.TYPE_RECENT)) {
            collection.slots.remove(position);
            Slot recentSlot = new Slot();
            recentSlot.slotId = Utility.createSlotId();
            recentSlot.type = Slot.TYPE_RECENT;
            Slot realmRecentSlot = realm.copyToRealm(recentSlot);
            collection.slots.add(realmRecentSlot);
        }
        realm.commitTransaction();
    }

    public void setSlotAsRecent(int slotIndex) {
        Slot slot = collection.slots.get(slotIndex);
        if (slot != null) {
            final String slotId = slot.slotId;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Slot slot1 = realm.where(Slot.class).equalTo(Cons.SLOT_ID, slotId).findFirst();
                    if (slot1 != null) {
                        slot1.type = Slot.TYPE_RECENT;
                    }
                }
            });
        }
    }
}
