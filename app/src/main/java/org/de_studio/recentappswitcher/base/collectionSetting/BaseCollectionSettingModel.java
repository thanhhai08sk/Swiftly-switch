package org.de_studio.recentappswitcher.base.collectionSetting;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;
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
        collection = realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType()).findFirst();
        if (collection == null) {
            createNewCollection();
            collection = realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType()).findFirst();
        }
            RealmObject.addChangeListener(collection,this);
            collectionReadySubject.onNext(collection);
            collectionChangedSubject.onNext(null);

    }

    public PublishSubject<Void> onCollectionChanged() {
        return collectionChangedSubject;
    }


    public Collection getCurrentCollection() {
        return collection;
    }

    public String getCollectionId() {
        if (collection != null) {
            return collection.collectionId;
        }
        return null;
    }


    public  String getDefaultCollectionId() {
        return getCollectionType() + "1";
    }

    public abstract String getCollectionType();

    public void setCurrentCollection(String collectionId) {
        if (collectionId != null) {
            Collection collectionWithLabel = realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType()).equalTo(Cons.COLLECTION_ID, collectionId).findFirst();
            if (collectionWithLabel != null) {
                if (collection != null) {
                    RealmObject.removeChangeListeners(collection);
                }
                collection = collectionWithLabel;
                RealmObject.addChangeListener(collection, this);
                collectionChangedSubject.onNext(null);
                this.collectionId = collection.collectionId;
            } else {
                Log.e(TAG, "setCurrentCollection: no collection with this id found: " + collectionId);
            }
            collectionReadySubject.onNext(collection);
        }
    }

    public void setCurrentCollectionToAnotherOne() {
        Collection anotherCollection = realm.where(Collection.class).equalTo(Cons.TYPE, getCollectionType())
                .notEqualTo(Cons.COLLECTION_ID, collectionId)
                .findFirst();

        if (anotherCollection != null) {
            if (collection != null) {
                RealmObject.removeChangeListeners(collection);
            }
            collection = anotherCollection;
            RealmObject.addChangeListener(collection, this);
            collectionChangedSubject.onNext(null);
            this.collectionId = collection.collectionId;
        } else {
            Log.e(TAG, "setCurrentCollectionToAnotherOne: there are no another collection");
        }
    }

    public void setCollectionLabel(final String label) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                collection.label = label;
            }
        });
    }

    public String getPlaceUsingThis(String collectionId) {
        Collection collectionThatUseThis = realm.where(Collection.class).equalTo("longPressCollection.collectionId", collectionId).findFirst();
        if (collectionThatUseThis == null) {
            collectionThatUseThis = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_QUICK_ACTION)
                    .equalTo("slots.stage1Item.itemId", Utility.createShortcutSetItemId(collectionId))
                    .or()
                    .equalTo("slots.stage2Item.itemId", Utility.createShortcutSetItemId(collectionId))
                    .findFirst();
        }

        if (collectionThatUseThis != null) {
            return collectionThatUseThis.label;
        }

        String collectionFieldNameInEdge = null;
        switch (getCollectionType()) {
            case Collection.TYPE_CIRCLE_FAVORITE:
                collectionFieldNameInEdge = "circleFav";
                break;
            case Collection.TYPE_GRID_FAVORITE:
                collectionFieldNameInEdge = "grid";
                break;
            case Collection.TYPE_QUICK_ACTION:
                collectionFieldNameInEdge = "quickAction";
                break;
            case Collection.TYPE_RECENT:
                collectionFieldNameInEdge = "recent";
                break;
        }
        Edge edgeThatUseThis = realm.where(Edge.class).equalTo(collectionFieldNameInEdge + ".collectionId", collectionId).findFirst();
        if (edgeThatUseThis != null) {
            return edgeThatUseThis.edgeId;
        }
        return null;
    }

    public void deleteCollection(final String collectionId) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Item shortcutSetItem = realm.where(Item.class).equalTo(Cons.COLLECTION_ID, collectionId).findFirst();
                if (shortcutSetItem != null) {
                    shortcutSetItem.deleteFromRealm();
                }
                Collection collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, collectionId).findFirst();
                if (collection != null) {
                    collection.slots.deleteAllFromRealm();
                    collection.deleteFromRealm();
                }
            }
        });
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

    public void setCurrentCollectionCircleSize(final int size) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, getCollectionId()).findFirst();
                collection.radius = size;
            }
        });
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

    public void swapItem(int first, int second) {
        Log.e(TAG, "swapItem: first = " + first + " second = " + second);
        realm.beginTransaction();
        collection.slots.move(first, second);
        collection.slots.move(first < second ? second - 1 : second + 1, first);
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

    public void setStayOnScreen() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                collection.stayOnScreen = collection.stayOnScreen!=null &&!collection.stayOnScreen;
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
