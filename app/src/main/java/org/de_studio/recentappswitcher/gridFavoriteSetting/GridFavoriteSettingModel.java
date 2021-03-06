package org.de_studio.recentappswitcher.gridFavoriteSetting;

import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.Random;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public class GridFavoriteSettingModel extends BaseCollectionSettingModel {
    private static final String TAG = GridFavoriteSettingModel.class.getSimpleName();

    public GridFavoriteSettingModel(String defaultLabel, String collectionId) {
        super(defaultLabel, collectionId);
    }

    @Override
    public String getCollectionType() {
        return Collection.TYPE_GRID_FAVORITE;
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
                collection.rowsCount = Cons.DEFAULT_FAVORITE_GRID_ROW_COUNT;
                collection.columnCount = Cons.DEFAULT_FAVORITE_GRID_COLUMN_COUNT;
                collection.longClickMode = Collection.LONG_CLICK_MODE_NONE;
                collection.position = Collection.POSITION_TRIGGER;
                collection.marginHorizontal = Cons.DEFAULT_FAVORITE_GRID_HORIZONTAL_MARGIN;
                collection.marginVertical = Cons.DEFAULT_FAVORITE_GRID_VERTICAL_MARGIN;
                collection.space = Cons.DEFAULT_FAVORITE_GRID_SPACE;
                Collection realmCollection = realm.copyToRealm(collection);

                for (int i = 0; i < collection.rowsCount * collection.columnCount; i++) {
                    Slot nullSlot = new Slot();
                    nullSlot.type = Slot.TYPE_NULL;
                    nullSlot.slotId = String.valueOf(System.currentTimeMillis() + new Random().nextLong());
                    Log.e(TAG, "new slot, id = " + nullSlot.slotId);
                    Slot realmSlot = realm.copyToRealm(nullSlot);
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
            switch (removeSlot.type) {
                case Slot.TYPE_NULL:
                    collection.slots.add(position, Utility.createSlotAndAddToRealm(realm,Slot.TYPE_EMPTY));
                    break;
                case Slot.TYPE_EMPTY:
                    collection.slots.add(position, Utility.createSlotAndAddToRealm(realm, Slot.TYPE_NULL));
                    break;
                default:
                    collection.slots.add(position, Utility.createSlotAndAddToRealm(realm, Slot.TYPE_NULL));
                    break;
            }
            removeSlot.deleteFromRealm();
        }
        realm.commitTransaction();
    }

    public void setHorizontalMargin(final int value) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                collection.marginHorizontal = value;
            }
        });
    }

    public void setVerticalMargin(final int value) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                collection.marginVertical = value;
            }
        });
    }

    public void setPosition(final int position) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                collection.position = position;
            }
        });
    }

    public void setColumnsCount(final int value) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                collection.columnCount = value;
            }
        });
        setCurrentCollectionSize(collection.columnCount * collection.rowsCount);
    }

    public void setRowsCount(final int value) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                collection.rowsCount = value;
            }
        });
        setCurrentCollectionSize(collection.columnCount * collection.rowsCount);
    }

    public void setShortcutsSpace(final int value) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                collection.space = value;
            }
        });
    }



}
