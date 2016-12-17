package org.de_studio.recentappswitcher.base.collectionSetting;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public abstract class BaseCollectionSettingModel extends BaseModel implements RealmChangeListener<Collection> {
    private static final String TAG = BaseCollectionSettingModel.class.getSimpleName();
    protected Realm realm = Realm.getDefaultInstance();
    protected String collectionId;
    protected String defaultLabel;
    protected Collection collection;
    protected PublishSubject<Void> collectionChangedSubject = PublishSubject.create();
    protected PublishSubject<Collection> collectionReadySubject = PublishSubject.create();

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
        RealmObject.addChangeListener(collection,this);
        collectionReadySubject.onNext(collection);
    }

    public PublishSubject<Void> onCollectionChanged() {
        return collectionChangedSubject;
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
                if (collection != null) {
                    RealmObject.removeChangeListeners(collection);
                }
                collection = collectionWithLabel;
                RealmObject.addChangeListener(collection, this);
                collectionId = collection.collectionId;
            } else {
                Log.e(TAG, "setCurrentCollection: no collection with this label found: " + collectionLabel);
            }
            collectionReadySubject.onNext(collection);
        }
    }


    public void setCurrentCollectionSize(int size) {
        RealmList<Slot> slots = getCurrentCollection().slots;
        while (slots.size() > size) {
            removeSlot(slots.size() - 1, slots);
        }
        while (slots.size() < size) {
            addDefaultSlotToList(slots);
        }
    }

    public void setSlotAsFolder(int slotIndex) {
        Slot slot = collection.slots.get(slotIndex);
        if (slot != null) {
            final String slotId = slot.slotId;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Slot slot1 = realm.where(Slot.class).equalTo(Cons.SLOT_ID, slotId).findFirst();
                    if (slot1 != null) {
                        slot1.type = Slot.TYPE_FOLDER;
                    }
                }
            });
        }
    }

    public OrderedRealmCollection<Slot> getSlots() {
        return getCurrentCollection().slots;
    }

    public String getSlotId(int slotIndex) {
        return getSlots().get(slotIndex).slotId;
    }

    public RealmResults<Collection> getCollectionList() {
        return realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType()).findAll();
    }


    public abstract String createNewCollection();

    private void addDefaultSlotToList(final RealmList<Slot> slots) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                slots.add(createDefaultSlot());
            }
        });

    }


    public Slot createDefaultSlot() {
        if (collection.type.equals(Collection.TYPE_RECENT)) {
            return Utility.createSlotAndAddToRealm(realm, Slot.TYPE_RECENT);
        } else {
            return Utility.createSlotAndAddToRealm(realm, Slot.TYPE_NULL);
        }
    }


    private void removeSlot(final int position, final RealmList<Slot> slots) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                slots.remove(position);
            }
        });
    }

    public void moveItem(int from, int to) {
        realm.beginTransaction();
        collection.slots.move(from, to);
        realm.commitTransaction();
    }

    public void removeItem(int position) {
        realm.beginTransaction();
        Slot removeSlot = collection.slots.get(position);
        collection.slots.remove(position);
        switch (removeSlot.type) {
            case Slot.TYPE_NULL:
                collection.slots.add(Utility.createSlotAndAddToRealm(realm,Slot.TYPE_EMPTY));
                removeSlot.deleteFromRealm();
                break;
            case Slot.TYPE_EMPTY:
                collection.slots.add(Utility.createSlotAndAddToRealm(realm, Slot.TYPE_NULL));
                removeSlot.deleteFromRealm();
                break;

            default:
                collection.slots.add(Utility.createSlotAndAddToRealm(realm, Slot.TYPE_NULL));
                break;
        }
        realm.commitTransaction();
    }

    public void setItemToSlotStage1(final Item item, final String slotId) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Slot slot = realm.where(Slot.class).equalTo(Cons.SLOT_ID, slotId).findFirst();
                if (slot != null && item != null) {
                    if (slot.type.equals(Slot.TYPE_EMPTY) || slot.type.equals(Slot.TYPE_NULL)) {
                        slot.type = Slot.TYPE_ITEM;
                    }
                    slot.stage1Item = item;
                }
            }
        });
    }

    public void clear() {
        realm.close();
    }

    @Override
    public void onChange(Collection element) {
        collectionChangedSubject.onNext(null);
    }
}
