package org.de_studio.recentappswitcher.quickActionSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.MyApplication;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by HaiNguyen on 12/10/16.
 */

public class QuickActionSettingModel extends BaseCollectionSettingModel {
    private static final String TAG = QuickActionSettingModel.class.getSimpleName();
    PublishSubject<Void> loadItemsOk = PublishSubject.create();
    CompositeSubscription subscription = new CompositeSubscription();
    public QuickActionSettingModel(String defaultLabel, String collectionId) {
        super(defaultLabel, collectionId);
    }


    @Override
    public void setup() {
        super.setup();
        subscription.add(
                Observable.combineLatest(collectionReadySubject, loadItemsOk, new Func2<Collection, Void, Collection>() {
                    @Override
                    public Collection call(Collection collection, Void aVoid) {
                        if (collection.slots.size() == 0) {
                            return collection;
                        } else {
                            return null;
                        }
                    }
                }).subscribe(new Action1<Collection>() {
                    @Override
                    public void call(final Collection collection) {
                        if (collection != null) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmList<Slot> slots = collection.slots;
                                    Item[] items = new Item[4];
                                    items[0] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_SHORTCUTS_SET + Collection.TYPE_GRID_FAVORITE + 1).findFirst();
                                    if (items[0] ==null) {
                                        Collection firstGridFavoriteCollection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Collection.TYPE_GRID_FAVORITE + 1).findFirst();
                                        Item newItem = new Item();
                                        newItem.type = Item.TYPE_SHORTCUTS_SET;
                                        newItem.itemId = Item.TYPE_SHORTCUTS_SET + firstGridFavoriteCollection.collectionId;
                                        newItem.label = firstGridFavoriteCollection.label;
                                        newItem.collectionId = firstGridFavoriteCollection.collectionId;
                                        Utility.setItemBitmapForShortcutsSet(MyApplication.getContext(), newItem);
                                        Item realmItem = realm.copyToRealm(newItem);
                                        items[0] = realmItem;
                                    }

                                    items[1] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_ACTION + Item.ACTION_BACK).findFirst();
                                    items[2] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_ACTION + Item.ACTION_LAST_APP).findFirst();
                                    items[3] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_ACTION + Item.ACTION_NOTI).findFirst();

                                    for (int i = 0; i < 4; i++) {
                                        Slot slot = new Slot();
                                        slot.type = Slot.TYPE_ITEM;
                                        slot.slotId = String.valueOf(System.currentTimeMillis() + new Random().nextLong());
                                        slot.stage1Item = items[i];
                                        if (i == 0) {
                                            slot.instant = true;
                                        }
                                        Slot realmSlot = realm.copyToRealm(slot);
                                        slots.add(realmSlot);
                                    }
                                }
                            });
                        }
                    }
                })
        );
    }

    @Override
    public String getCollectionType() {
        return Collection.TYPE_QUICK_ACTION;
    }


    public void setVisibilityOption(final int option) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection collection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, getCollectionId()).findFirst();
                if (collection != null) {
                    collection.visibilityOption = option;
                }
            }
        });
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
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Collection collection = new Collection();
                collection.type = getCollectionType();
                collection.collectionId = newId;
                collection.label = newLabel;
                collection.longClickMode = Collection.LONG_CLICK_MODE_NONE;
                realm.copyToRealm(collection);
            }
        });
        return newId;
    }

    public void setSlotInstant(final int slotPosition) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Slot slot = getSlots().get(slotPosition);
                if (slot != null) {
                    slot.instant = !slot.instant;
                }
            }
        });
    }

    @Override
    public void clear() {
        super.clear();
        subscription.clear();
    }

    public void loadItemsOk() {
        loadItemsOk.onNext(null);
    }
}
