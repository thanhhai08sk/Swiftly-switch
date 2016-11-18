package org.de_studio.recentappswitcher.setCircleFavorite;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class SetCircleFavoriteModel {
    private static final String TAG = SetCircleFavoriteModel.class.getSimpleName();
    private Realm realm = Realm.getDefaultInstance();
    private String collectionId;
    private String defaultLabel;
    private Collection collection;
    public SetCircleFavoriteModel(String collectionId, String defaultLabel) {
        this.collectionId = collectionId;
        if (collectionId != null && !collectionId.contains(Collection.TYPE_CIRCLE_FAVORITE)) {
            throw new IllegalArgumentException();
        }
        this.defaultLabel = defaultLabel;
    }

    public Collection getCurrentCollection() {
        if (collection == null) {
            if (collectionId == null) {
                collectionId = Collection.TYPE_CIRCLE_FAVORITE + "1";
            }

            collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, collectionId).findFirst();
            if (collection == null) {
                createDefaultCollection();
                collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, collectionId).findFirst();
            }
        }
        return collection;
    }

    public void setCollection(String collectionLabel) {
        if (collectionLabel != null) {
            Collection collectionWithLabel = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_CIRCLE_FAVORITE)
                    .equalTo(Cons.LABEL, collectionLabel).findFirst();
            if (collectionWithLabel == null) {
                throw new IllegalArgumentException("No collection with this label found");
            } else {
                collection = collectionWithLabel;
            }
        }
    }

    public void setCollectionSize(int size) {
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
        RealmResults<Collection> circleFavoriteCollections = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_CIRCLE_FAVORITE).findAll();
        if (circleFavoriteCollections.size() ==0) {
            createDefaultCollection();
        }
        return circleFavoriteCollections;
    }



    private void createDefaultCollection() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                long newCollectionNumber = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_CIRCLE_FAVORITE).count() + 1;

                Collection collection = new Collection();
                collection.type = Collection.TYPE_CIRCLE_FAVORITE;
                collection.collectionId = Collection.TYPE_CIRCLE_FAVORITE + newCollectionNumber;
                collection.label = defaultLabel + " " + newCollectionNumber;
                collection.longClickMode = Collection.LONG_CLICK_MODE_NONE;
                Collection realmCollection = realm.copyToRealm(collection);

                Slot nullSlot = new Slot();
                nullSlot.type = Slot.TYPE_NULL;
                Slot realmSlot = realm.copyToRealm(nullSlot);

                for (int i = 0; i < 6; i++) {
                    realmCollection.slots.add(realmSlot);
                }
                Log.e(TAG, "execute: collection slots size = " + realmCollection.slots.size());

            }
        });
    }

    private void addNullSlotToList(final RealmList<Slot> slots) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Slot nullSlot = new Slot();
                nullSlot.type = Slot.TYPE_NULL;
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
