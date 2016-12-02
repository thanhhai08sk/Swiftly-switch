package org.de_studio.recentappswitcher.setItems;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class SetItemsModel extends BaseModel {
    private static final String TAG = SetItemsModel.class.getSimpleName();
    int itemsType;
    String collectionId;
    String slotId;
    Realm realm = Realm.getDefaultInstance();

    public SetItemsModel(int itemsType, String collectionId, String slotId) {
        this.itemsType = itemsType;
        this.collectionId = collectionId;
        this.slotId = slotId;
    }

    public Realm getRealm() {
        return realm;
    }

    public Item getNextItem(int currentIndex) {
        Log.e(TAG, "getNextItem: " + currentIndex);
        switch (itemsType) {
            case SetItemsView.ITEMS_TYPE_STAGE_1:
                Slot slot = getSlot(currentIndex + 1);
                return slot != null ? slot.stage1Item : null;
            case SetItemsView.ITEMS_TYPE_STAGE_2:
                Slot slot1 = getSlot(currentIndex + 1);
                return slot1 != null ? slot1.stage2Item : null;
            case SetItemsView.ITEMS_TYPE_FOLDER:
                RealmList<Item> items = getItemsList();
                return (items != null && currentIndex + 1 < items.size()) ? items.get(currentIndex + 1) : null;
        }
        return null;
    }

    public int getMaxIndex() {
        switch (itemsType) {
            case SetItemsView.ITEMS_TYPE_FOLDER:
                return -1;
            default:
                Collection collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, collectionId).findFirst();
                return collection.slots.size() - 1;
        }
    }

    public Item getPreviousItem(int currentIndex) {
        Log.e(TAG, "getPreviousItem: " + currentIndex);
        switch (itemsType) {
            case SetItemsView.ITEMS_TYPE_STAGE_1:
                Slot slot = getSlot(currentIndex - 1);
                return slot != null ? slot.stage1Item : null;
            case SetItemsView.ITEMS_TYPE_STAGE_2:
                Slot slot1 = getSlot(currentIndex - 1);
                return slot1 != null ? slot1.stage2Item : null;
            case SetItemsView.ITEMS_TYPE_FOLDER:
                RealmList<Item> items = getItemsList();
                return (items != null && currentIndex - 1 >= 0) ? items.get(currentIndex - 1) : null;
        }
        return null;
    }

    public Item getCurrentItem(int currentIndex) {
        Log.e(TAG, "getCurrentItem: " + currentIndex);
        return getNextItem(currentIndex - 1);
    }

    public void setCurrentItem(final Item item, final int currentIndex) {
        switch (itemsType) {
            case SetItemsView.ITEMS_TYPE_STAGE_1:
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Slot slot = getSlot(currentIndex);
                        if (slot != null) {
                            slot.type = Slot.TYPE_ITEM;
                            Log.e(TAG, "execute: set stage1 = " + item.label + "\ntype " + item.type + "\npackage " + item.packageName
                            + "\ncollection = " + collectionId + "\nindex = " + currentIndex);
                            slot.stage1Item = item;
                        }
                    }
                });
                break;
            case SetItemsView.ITEMS_TYPE_STAGE_2:
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Slot slot = getSlot(currentIndex);
                        if (slot != null) {
                            slot.type = Slot.TYPE_ITEM;
                            slot.stage2Item = item;
                        }
                    }
                });
                break;
            case SetItemsView.ITEMS_TYPE_FOLDER:
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmList<Item> items = getItemsList();
                        if (items != null) {
                            items.remove(currentIndex);
                            items.add(currentIndex, item);
                        }
                    }
                });
        }
    }

    private Slot getSlot(int itemIndex) {
        Collection collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, collectionId).findFirst();
        if (collection != null) {
            RealmList<Slot> slots = collection.slots;
            if (itemIndex >=0 && itemIndex < slots.size()) {
                return slots.get(itemIndex);
            }
        }
        return null;
    }

    private RealmList<Item> getItemsList() {
        Slot slot = realm.where(Slot.class).equalTo(Cons.SLOT_ID, slotId).findFirst();
        if (slot != null) {
            return slot.items;
        }
        return null;
    }

    public void clear() {
        realm.close();
    }
}
