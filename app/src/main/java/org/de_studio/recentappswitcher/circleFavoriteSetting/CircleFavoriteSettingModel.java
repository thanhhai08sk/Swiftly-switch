package org.de_studio.recentappswitcher.circleFavoriteSetting;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.Random;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class CircleFavoriteSettingModel {
    private static final String TAG = CircleFavoriteSettingModel.class.getSimpleName();
    private Realm realm = Realm.getDefaultInstance();
    private String collectionId;
    private String defaultLabel;
    private Collection collection;
    public CircleFavoriteSettingModel(String collectionId, String defaultLabel) {
        this.collectionId = collectionId;
        if (collectionId != null && !collectionId.contains(Collection.TYPE_CIRCLE_FAVORITE)) {
            throw new IllegalArgumentException();
        }
        this.defaultLabel = defaultLabel;
    }

    public Collection getCurrentCollection() {
        if (collection == null) {
            collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, getCollectionId()).findFirst();
            if (collection == null) {
                createNewCollection();
                collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, getCollectionId()).findFirst();
            }
        }
        return collection;
    }

    public String getCollectionId() {
        if (collectionId == null) {
            collectionId = getDefaultCollectionId();
        }
        return collectionId;
    }

    public String getDefaultCollectionId() {
        return Collection.TYPE_CIRCLE_FAVORITE + "1";
    }

    public String getCollectionType() {
        return Collection.TYPE_CIRCLE_FAVORITE;
    }

    public void setCurrentCollection(String collectionLabel) {
        if (collectionLabel != null) {
            Collection collectionWithLabel = realm.where(Collection.class).equalTo(Cons.LABEL, collectionLabel).findFirst();
            if (collectionWithLabel != null) {
                collection = collectionWithLabel;
                collectionId = collection.collectionId;
            } else {
                Log.e(TAG, "setCurrentCollection: no collection with this label found: " + collectionLabel);
            }
        }
    }


    public void setCurrentCollectionSize(int size) {
        RealmList<Slot> slots = getCurrentCollection().slots;
        while (slots.size() > size) {
            removeSlot(slots.size() - 1, slots);
        }
        while (slots.size() < size) {
            addNullSlotToList(slots);
        }
    }

    public OrderedRealmCollection<Slot> getSlots() {
        return getCurrentCollection().slots;
    }

    public RealmResults<Collection> getCollectionList() {
        RealmResults<Collection> circleFavoriteCollections = realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType()).findAll();
        if (circleFavoriteCollections.size() ==0) {
            createNewCollection();
        }
        return circleFavoriteCollections;
    }



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

    private void addNullSlotToList(final RealmList<Slot> slots) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Slot nullSlot = new Slot();
                nullSlot.type = Slot.TYPE_NULL;
                nullSlot.slotId = String.valueOf(System.currentTimeMillis() + new Random().nextLong());
                Log.e(TAG, "new slot, id = " + nullSlot.slotId);
                Slot realmSlot = realm.copyToRealm(nullSlot);
                slots.add(realmSlot);
            }
        });

    }

    private void removeSlot(final int position, final RealmList<Slot> slots) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                slots.remove(position);
            }
        });
    }

    public void clear() {
        realm.close();
    }
}
