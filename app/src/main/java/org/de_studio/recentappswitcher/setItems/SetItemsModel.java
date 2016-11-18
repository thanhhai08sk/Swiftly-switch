package org.de_studio.recentappswitcher.setItems;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class SetItemsModel {
    int itemsType;
    int itemIndex;
    String collectionId;
    String slotId;
    Realm realm = Realm.getDefaultInstance();

    public SetItemsModel(int itemsType, int itemIndex, String collectionId, String slotId) {
        this.itemsType = itemsType;
        this.itemIndex = itemIndex;
        this.collectionId = collectionId;
        this.slotId = slotId;
    }

    public Realm getRealm() {
        return realm;
    }

    public Item getNextItem(int currentIndex) {
        switch (itemsType) {
            case SetItemsView.ITEMS_TYPE_STAGE_1:
                Collection collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, collectionId).findFirst();
                if (collection != null) {
                    RealmList<Slot> slots = collection.slots;
                    if (currentIndex+1 < slots.size()) {
                        return slots.get(currentIndex + 1).getStage1Item();
                    }
                }
                break;
            case SetItemsView.ITEMS_TYPE_STAGE_2:
                Collection collection1 = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, collectionId).findFirst();
                if (collection1 != null) {
                    RealmList<Slot> slots = collection1.slots;
                    if (currentIndex+1 < slots.size()) {
                        return slots.get(currentIndex + 1).getStage2Item();
                    }
                }
                break;
            case SetItemsView.ITEMS_TYPE_FOLDER:
                Slot slot = realm.where(Slot.class).equalTo(Cons.SLOT_ID, slotId).findFirst();
                if (slot != null) {
                    RealmList<Item> items = slot.items;
                    if (currentIndex + 1 < items.size()) {
                        return items.get(currentIndex + 1);
                    }

                }
                break;
        }
        return null;
    }

    public Item getPreviousItem(int currentIndex) {
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

    public void setCurrentItem(Item item) {

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
}
