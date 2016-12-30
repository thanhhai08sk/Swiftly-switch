package org.de_studio.recentappswitcher.dadaSetup;

import android.Manifest;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import java.util.Random;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;

import static org.de_studio.recentappswitcher.MyApplication.getContext;


public class DataSetupService extends IntentService {
    private static final String TAG = DataSetupService.class.getSimpleName();
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_GENERATE_DATA = "org.de_studio.recentappswitcher.dadaSetup.action.GENERATE_DATA";
    public static final String ACTION_CONVERT = "org.de_studio.recentappswitcher.dadaSetup.action.CONVERT";


    public DataSetupService() {
        super("DataSetupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GENERATE_DATA.equals(action)) {
                handleGenerateData();
            } else if (ACTION_CONVERT.equals(action)) {
                handleActionConvert();
            }
        }
    }

    private void handleGenerateData() {
        Realm realm = Realm.getDefaultInstance();
        generateItems(realm);
        generateCollections(realm);
        generateEdges(realm);
        realm.close();
    }


    private void handleActionConvert() {

    }


    private void generateCollections(Realm realm) {
        generateRecent(realm);
        generateCircleFavorite(realm);
        generateGridFavorite(realm);
        generateQuickActions(realm);
        generateBlackList(realm);
    }

    private void generateItems(Realm realm) {
        generateAppItems(realm);
        generateActionItems(realm);
        generateContactItems(realm);
    }

    private void generateEdges(Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection recent = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_RECENT, 1)).findFirst();
                Collection quickAction = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_QUICK_ACTION, 1)).findFirst();
                if (realm.where(Edge.class).equalTo(Cons.EDGE_ID, "edge1").findFirst() == null) {
                    Edge edge1 = new Edge();
                    edge1.edgeId = "edge1";
                    edge1.mode = Edge.MODE_RECENT_AND_QUICK_ACTION;
                    edge1.useGuide = true;
                    edge1.position = Cons.POSITION_RIGHT_CENTRE;
                    edge1.recent = recent;
                    edge1.quickAction = quickAction;
                    edge1.sensitive = Cons.EDGE_SENSITIVE_DEFAULT;
                    edge1.length = Cons.EDGE_LENGTH_DEFAULT;
                    edge1.offset = Cons.EDGE_OFFSET_DEFAULT;
                    edge1.keyboardOption = Edge.KEYBOARD_OPTION_PLACE_UNDER;
                    realm.copyToRealm(edge1);
                }

                if (realm.where(Edge.class).equalTo(Cons.EDGE_ID, "edge2").findFirst() == null) {
                    Edge edge2 = new Edge();
                    edge2.edgeId = "edge2 ";
                    edge2.mode = Edge.MODE_RECENT_AND_QUICK_ACTION;
                    edge2.useGuide = true;
                    edge2.position = Cons.POSITION_LEFT_BOTTOM;
                    edge2.recent = recent;
                    edge2.quickAction = quickAction;
                    edge2.sensitive = Cons.EDGE_SENSITIVE_DEFAULT;
                    edge2.length = Cons.EDGE_LENGTH_DEFAULT;
                    edge2.offset = Cons.EDGE_OFFSET_DEFAULT;
                    edge2.keyboardOption = Edge.KEYBOARD_OPTION_PLACE_UNDER;
                    realm.copyToRealm(edge2);
                }
            }
        });
    }

    private void generateRecent(Realm realm) {
        final String newLabel = Utility.createCollectionLabel(getString(R.string.recent_apps), 1);
        Collection recents = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_RECENT, 1)).findFirst();
        if (recents == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Collection collection = new Collection();
                    collection.type = Collection.TYPE_RECENT;
                    collection.collectionId = Utility.createCollectionId(Collection.TYPE_RECENT, 1);
                    collection.label = newLabel;
                    collection.longClickMode = Collection.LONG_CLICK_MODE_NONE;
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
        }
    }

    private void generateCircleFavorite(Realm realm) {
        final String newLabel = Utility.createCollectionLabel(getString(R.string.circle_favorites), 1);
        Collection circle = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_CIRCLE_FAVORITE, 1)).findFirst();
        if (circle == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Collection collection = new Collection();
                    collection.type = Collection.TYPE_CIRCLE_FAVORITE;
                    collection.collectionId = Utility.createCollectionId(Collection.TYPE_CIRCLE_FAVORITE, 1);
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
        }
    }

    private void generateGridFavorite(Realm realm) {
        final String newLabel = Utility.createCollectionLabel(getString(R.string.grid_favorites), 1);
        Collection grid = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_GRID_FAVORITE, 1)).findFirst();
        if (grid == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Collection collection = new Collection();
                    collection.type = Collection.TYPE_GRID_FAVORITE;
                    collection.collectionId = Utility.createCollectionId(Collection.TYPE_GRID_FAVORITE, 1);
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
        }
    }

    private void generateQuickActions(Realm realm) {
        Collection quickActions = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_QUICK_ACTION, 1)).findFirst();
        final String newLabel = Utility.createCollectionLabel(getString(R.string.main_outer_ring_setting), 1);
        if (quickActions == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    Collection collection = new Collection();
                    collection.type = Collection.TYPE_QUICK_ACTION;
                    collection.collectionId = Utility.createCollectionId(Collection.TYPE_QUICK_ACTION, 1);
                    collection.label = newLabel;
                    collection.longClickMode = Collection.LONG_CLICK_MODE_NONE;
                    realm.copyToRealm(collection);
                }
            });
            final Collection quickActions1 = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Collection.TYPE_QUICK_ACTION + 1).findFirst();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmList<Slot> slots = quickActions1.slots;
                    Item[] items = new Item[4];
                    items[0] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_SHORTCUTS_SET + Collection.TYPE_GRID_FAVORITE + 1).findFirst();
                    if (items[0] ==null) {
                        Collection firstGridFavoriteCollection = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Collection.TYPE_GRID_FAVORITE + 1).findFirst();
                        Item newItem = new Item();
                        newItem.type = Item.TYPE_SHORTCUTS_SET;
                        newItem.itemId = Item.TYPE_SHORTCUTS_SET + firstGridFavoriteCollection.collectionId;
                        newItem.label = firstGridFavoriteCollection.label;
                        newItem.collectionId = firstGridFavoriteCollection.collectionId;
                        Utility.setIconResourceIdsForShortcutsSet(newItem);
                        Item realmItem = realm.copyToRealm(newItem);
                        items[0] = realmItem;
                    }

                    items[1] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_ACTION + Item.ACTION_HOME).findFirst();
                    items[2] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_ACTION + Item.ACTION_BACK).findFirst();
                    items[3] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_ACTION + Item.ACTION_NOTI).findFirst();

                    for (int i = 0; i < 4; i++) {
                        Slot slot = new Slot();
                        slot.type = Slot.TYPE_ITEM;
                        slot.slotId = String.valueOf(System.currentTimeMillis() + new Random().nextLong());
                        slot.stage1Item = items[i];
                        Slot realmSlot = realm.copyToRealm(slot);
                        slots.add(realmSlot);
                    }
                }
            });
        }

    }

    private void generateBlackList(Realm realm) {
        Collection blackList = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_BLACK_LIST, 1)).findFirst();
        if (blackList == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Collection collection = new Collection();
                    collection.type = Collection.TYPE_BLACK_LIST;
                    collection.collectionId = Utility.createCollectionId(Collection.TYPE_BLACK_LIST, 1);
                    collection.label = getString(R.string.main_exclude_app_title);
                    realm.copyToRealm(collection);
                }
            });

        }
    }

    private void generateAppItems(Realm realm) {
        Set<PackageInfo> packageInfos = Utility.getInstalledApps(getPackageManager());
        Item tempItem;
        for (final PackageInfo info : packageInfos) {
            final String itemId = Utility.createAppItemId(info.packageName);
            tempItem = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
            if (tempItem == null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Item item = new Item();
                        item.type = Item.TYPE_APP;
                        item.itemId = itemId;
                        item.packageName = info.packageName;
                        item.label = info.applicationInfo.loadLabel(getPackageManager()).toString();
                        realm.copyToRealm(item);
                    }
                });
            }
        }
    }

    private void generateActionItems(Realm realm) {
        final String[] actionStrings = getResources().getStringArray(R.array.setting_shortcut_array_no_folder);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (String string : actionStrings) {
                    int action = Utility.getActionFromLabel(getApplicationContext(), string);
                    String itemId = Item.TYPE_ACTION + action;
                    Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
                    if (item == null) {
                        Log.e(TAG, "LoadActions - add action " + string);
                        Item newItem = new Item();
                        newItem.type = Item.TYPE_ACTION;
                        newItem.itemId = itemId;
                        newItem.label = string;
                        newItem.action = action;
                        Utility.setIconResourceIdsForAction(newItem);
                        realm.copyToRealm(newItem);
                    }
                }
            }
        });
    }

    private void generateContactItems(Realm realm) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            String sordOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
            ContentResolver cr = getContentResolver();
            final Cursor data = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    sordOrder
            );
            if (data !=null && data.getCount() > 0) {
                data.moveToFirst();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        while (data.moveToNext()) {
                            String number = data.getString(data.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            long contactId = data.getLong(data.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                            int type = data.getInt(data.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
                            String defaultName = data.getString(data.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            String iconUri = data.getString(data.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));


                            Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, Item.TYPE_CONTACT + number).findFirst();
                            if (item == null) {
                                Item newItem = new Item();
                                newItem.type = Item.TYPE_CONTACT;
                                newItem.itemId = Item.TYPE_CONTACT + number;
                                newItem.label = Utility.getContactItemLabel(type, defaultName, getApplicationContext());
                                newItem.number = number;
                                newItem.contactId = contactId;
                                newItem.iconUri = iconUri;
                                realm.copyToRealm(newItem);
                            }
                        }
                    }
                });
            } else {
                Log.e(TAG, "generateContactItems: can not generate contacts");
            }

        }
    }
}
