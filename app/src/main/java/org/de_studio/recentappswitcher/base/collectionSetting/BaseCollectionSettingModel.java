package org.de_studio.recentappswitcher.base.collectionSetting;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.Random;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public abstract class BaseCollectionSettingModel extends BaseModel{
    private static final String TAG = BaseCollectionSettingModel.class.getSimpleName();
    protected Realm realm = Realm.getDefaultInstance();
    protected String collectionId;
    protected String defaultLabel;
    protected Collection collection;

    public BaseCollectionSettingModel(String defaultLabel, String collectionId) {
        this.defaultLabel = defaultLabel;
        this.collectionId = collectionId;
    }

    public void setup() {
        collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, getCollectionId()).findFirst();
        if (collection == null) {
            createNewCollection();
            collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, getCollectionId()).findFirst();
        }
    }


    public Collection getCurrentCollection() {
        return collection;
    }

    public String getCollectionId() {
        if (collectionId == null) {
            collectionId = getDefaultCollectionId();
        }
        return collectionId;
    }

    public  String getDefaultCollectionId() {
        return getCollectionType() + "1";
    }

    public abstract String getCollectionType();

    public void setCurrentCollection(String collectionLabel) {
        if (collectionLabel != null) {
            Collection collectionWithLabel = realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType()).equalTo(Cons.LABEL, collectionLabel).findFirst();
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
        return realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType()).findAll();
    }


    public abstract String createNewCollection();

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