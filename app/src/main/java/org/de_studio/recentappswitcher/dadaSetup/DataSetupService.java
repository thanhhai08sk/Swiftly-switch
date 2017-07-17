package org.de_studio.recentappswitcher.dadaSetup;

import android.Manifest;
import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.ArraySet;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.DataInfo;
import org.de_studio.recentappswitcher.model.Edge;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Shortcut;
import org.de_studio.recentappswitcher.model.Slot;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static org.de_studio.recentappswitcher.MyApplication.getContext;


public class DataSetupService extends IntentService {
    private static final String TAG = DataSetupService.class.getSimpleName();
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_GENERATE_DATA = "org.de_studio.recentappswitcher.dadaSetup.action.GENERATE_DATA";
    public static final String ACTION_SETUP_GRID = "org.de_studio.recentappswitcher.dadaSetup.action.SETUP_GRID";

    public static final String BROADCAST_GENERATE_DATA_OK = "org.de_studio.recentappswitcher.dadaSetup.action.GENERATE_DATA_OK";


    public DataSetupService() {
        super("DataSetupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GENERATE_DATA.equals(action)) {
                handleGenerateData();
            } else if (ACTION_SETUP_GRID.equals(action)) {
                handleSetupGrid();
            }
        }
    }

    private void handleGenerateData() {
        Log.e(TAG, "handleGenerateData: ");
        Realm realm = Realm.getDefaultInstance();
        DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
        if (dataInfo == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    DataInfo dataInfo1 = new DataInfo();
                    realm.insertOrUpdate(dataInfo1);
                }
            });
        }

        dataInfo = realm.where(DataInfo.class).findFirst();
        generateItems(realm);
        generateCollections(realm,dataInfo);
        generateEdges(realm);
        restoreFromOldRealmOrGenerateInitData(realm, dataInfo);
        realm.close();
        sendBroadcast(new Intent(BROADCAST_GENERATE_DATA_OK));
    }


    private void handleSetupGrid() {
        Log.e(TAG, "handleSetupGrid: ");
        Realm realm = Realm.getDefaultInstance();
        DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
        if (dataInfo.everyThingsOk() && !dataInfo.initGridItemOk) {
            generateInitSetForGrid(realm);
        }
        realm.close();
    }


    private void generateCollections(Realm realm, final DataInfo dataInfo) {
        Log.e(TAG, "generateCollections: ");
        if (dataInfo.everyThingsOk()) {
            return;
        }
        if (!dataInfo.recentOk) {
            generateRecent(realm);
        }

        if (!dataInfo.circleFavoriteOk) {
            generateCircleFavorite(realm);
        }

        if (!dataInfo.gridOk) {
            generateGridFavorite(realm);
        }

        if (!dataInfo.quickActionOk) {
            generateQuickActions(realm);
        }

        if (!dataInfo.blackListOk) {
            generateBlackList(realm);
        }
    }

    private void generateItems(Realm realm) {
        generateAppItems(realm);
        generateActionItems(realm);
        generateContactItems(realm);
    }

    public static void generateEdges(Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection recent = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_RECENT, 1)).findFirst();
                Collection quickAction = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_QUICK_ACTION, 1)).findFirst();
                if (realm.where(Edge.class).equalTo(Cons.EDGE_ID, Edge.EDGE_1_ID).findFirst() == null) {
                    Edge edge1 = new Edge();
                    edge1.edgeId = Edge.EDGE_1_ID;
                    edge1.mode = Edge.MODE_RECENT_AND_QUICK_ACTION;
                    edge1.useGuide = true;
                    edge1.position = Cons.POSITION_RIGHT_CENTRE;
                    edge1.recent = recent;
                    edge1.quickAction = quickAction;
                    edge1.sensitive = Cons.DEFAULT_EDGE_SENSITIVE;
                    edge1.length = Cons.DEFAULT_EDGE_LENGTH;
                    edge1.offset = Cons.DEFAULT_EDGE_OFFSET;
                    edge1.keyboardOption = Edge.KEYBOARD_OPTION_PLACE_UNDER;

                    realm.copyToRealm(edge1);
                    DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
                    Log.e(TAG, "execute: setup edge1 ok");
                    dataInfo.edge1Ok = true;
                } else {
                    Log.e(TAG, "execute: edge1 already exist");
                }

                if (realm.where(Edge.class).equalTo(Cons.EDGE_ID, Edge.EDGE_2_ID).findFirst() == null) {
                    Edge edge2 = new Edge();
                    edge2.edgeId = Edge.EDGE_2_ID;
                    edge2.mode = Edge.MODE_RECENT_AND_QUICK_ACTION;
                    edge2.useGuide = true;
                    edge2.position = Cons.POSITION_LEFT_BOTTOM;
                    edge2.recent = recent;
                    edge2.quickAction = quickAction;
                    edge2.sensitive = Cons.DEFAULT_EDGE_SENSITIVE;
                    edge2.length = Cons.DEFAULT_EDGE_LENGTH;
                    edge2.offset = Cons.DEFAULT_EDGE_OFFSET;
                    edge2.keyboardOption = Edge.KEYBOARD_OPTION_PLACE_UNDER;

                    realm.copyToRealm(edge2);
                    DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
                    Log.e(TAG, "execute: setup edge2 ok");
                    dataInfo.edge2Ok = true;
                } else {
                    Log.e(TAG, "execute: edge2 already exist");
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
                    collection.radius = Cons.CIRCLE_RADIUS_DEFAULT;
                    Collection realmCollection = realm.copyToRealm(collection);
                    for (int i = 0; i < 7; i++) {
                        Slot recentSlot = new Slot();
                        recentSlot.type = Slot.TYPE_RECENT;
                        recentSlot.slotId = String.valueOf(System.currentTimeMillis() + new Random().nextLong());
                        Log.e(TAG, "new slot, id = " + recentSlot.slotId);
                        Slot realmSlot = realm.copyToRealm(recentSlot);
                        realmCollection.slots.add(realmSlot);
                    }
                    DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
                    dataInfo.recentOk = true;
                    Log.e(TAG, "generate recent ok");
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
                    collection.radius = Cons.CIRCLE_RADIUS_DEFAULT;
                    Collection realmCollection = realm.copyToRealm(collection);

                    for (int i = 0; i < 7; i++) {
                        Slot nullSlot = new Slot();
                        nullSlot.type = Slot.TYPE_NULL;
                        nullSlot.slotId = String.valueOf(System.currentTimeMillis() + new Random().nextLong());
                        Log.e(TAG, "new slot, id = " + nullSlot.slotId);
                        Slot realmSlot = realm.copyToRealm(nullSlot);
                        realmCollection.slots.add(realmSlot);
                    }
                    DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
                    dataInfo.circleFavoriteOk = true;
                    Log.e(TAG, "generate circleFav ok");
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
                        Slot realmSlot = Utility.createSlotAndAddToRealm(realm, Slot.TYPE_NULL);
                        realmCollection.slots.add(realmSlot);
                    }
                    DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
                    dataInfo.gridOk = true;
                    Log.e(TAG, "generate grid ok");
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
                        Collection firstGridFavoriteCollection = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_GRID_FAVORITE).findFirst();
                        Item newItem = new Item();
                        newItem.type = Item.TYPE_SHORTCUTS_SET;
                        newItem.itemId = Item.TYPE_SHORTCUTS_SET + firstGridFavoriteCollection.collectionId;
                        newItem.label = firstGridFavoriteCollection.label;
                        newItem.collectionId = firstGridFavoriteCollection.collectionId;
                        Utility.setItemBitmapForShortcutsSet(getApplicationContext(), newItem);
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
                    DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
                    dataInfo.quickActionOk = true;
                    Log.e(TAG, "generate quick action ok");
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

                    DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
                    dataInfo.blackListOk = true;
                    Log.e(TAG, "generate blacklist ok");
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
                        Log.e(TAG, "generate app item: " + info.packageName);
                    }
                });
            }
        }
    }

    private void generateActionItems(Realm realm) {
        Utility.generateActionItems(realm, new WeakReference<Context>(getApplicationContext()));
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
                                Log.e(TAG, "generate contact item: " + newItem.label );
                            }
                        }
                    }
                });
            } else {
                Log.e(TAG, "generateContactItems: can not generate contacts");
            }

        }
    }

    private void restoreFromOldRealmOrGenerateInitData(Realm newRealm, DataInfo dataInfo) {
        Realm gridRealm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(Cons.OLD_REALM_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        Realm pinRealm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("pinApp.realm")
                .schemaVersion(Cons.OLD_REALM_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        Realm circleFavoRealm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("circleFavo.realm")
                .schemaVersion(Cons.OLD_REALM_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        if (!gridRealm.isEmpty()) {
            Log.e(TAG, "restoreFromOldRealmOrGenerateInitData: restore");
            SharedPreferences oldDefaultShared = getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
            SharedPreferences oldExcludeShared = getSharedPreferences(MainActivity.EXCLUDE_SHAREDPREFERENCE, 0);
            SharedPreferences oldEdge1Shared = getSharedPreferences(MainActivity.EDGE_1_SHAREDPREFERENCE, 0);
            SharedPreferences oldEdge2Shared = getSharedPreferences(MainActivity.EDGE_2_SHAREDPREFERENCE, 0);

            SharedPreferences newShared = getSharedPreferences(Cons.SHARED_PREFERENCE_NAME, 0);
            convertGrid(gridRealm, newRealm, oldDefaultShared);
            convertCircleFav(circleFavoRealm, newRealm);
            convertPinnedShortcuts(pinRealm, newRealm);
            convertBlackList(oldExcludeShared, newRealm);
            convertQuickActions(newRealm, oldDefaultShared);
            convertSettings(newRealm, oldDefaultShared, oldEdge1Shared, oldEdge2Shared, newShared);
        } else {
            Log.e(TAG, "restoreFromOldRealmOrGenerateInitData: generate init data");
            if (!dataInfo.initGridItemOk) {
                generateInitSetForGrid(newRealm);
            }
        }


        Log.e(TAG, "restoreFromOldRealmOrGenerateInitData: gridRealm size = " + gridRealm.where(Shortcut.class).findAll().size()
                + "\nisEmpty = " + gridRealm.isEmpty());

    }

    private void convertGrid(Realm oldRealm, Realm newRealm, SharedPreferences oldShared) {
        int gridRow = oldShared.getInt(EdgeSetting.NUM_OF_GRID_ROW_KEY, 5);
        int gridColumn = oldShared.getInt(EdgeSetting.NUM_OF_GRID_COLUMN_KEY, 4);
        int gridGap = oldShared.getInt(EdgeSetting.GAP_OF_SHORTCUT_KEY, 5);
        int marginHorizontal = oldShared.getInt(EdgeSetting.GRID_DISTANCE_FROM_EDGE_KEY, 20);
        int marginVertical = oldShared.getInt(EdgeSetting.GRID_DISTANCE_VERTICAL_FROM_EDGE_KEY, 20);
        Collection newGrid = newRealm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_GRID_FAVORITE).findFirst();
        RealmResults<Shortcut> results = oldRealm.where(Shortcut.class).lessThan("id", 100).findAllSorted("id", Sort.ASCENDING);

        if (newGrid != null) {
            newRealm.beginTransaction();
            Utility.setCollectionSlotsSize(newRealm, newGrid, gridRow * gridColumn);
            newGrid.columnCount = gridColumn;
            newGrid.rowsCount = gridRow;
            newGrid.marginHorizontal = marginHorizontal;
            newGrid.marginVertical = marginVertical;
            newGrid.space = gridGap;
            convertShortcutsToSlotsOfCollection(newRealm, newGrid, results,oldRealm);
            newRealm.commitTransaction();
        } else {
            Log.e(TAG, "convertGrid: cannot find grid collection");
        }
    }

    private void convertCircleFav(Realm oldRealm, Realm newRealm) {
        Collection newCircle = newRealm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_CIRCLE_FAVORITE).findFirst();
        RealmResults<Shortcut> shortcuts = oldRealm.where(Shortcut.class).findAll();


        if (newCircle != null) {
            newRealm.beginTransaction();
            convertShortcutsToSlotsOfCollection(newRealm, newCircle, shortcuts,oldRealm);
            newRealm.commitTransaction();
        } else {
            Log.e(TAG, "convertGrid: cannot find grid collection");
        }
    }

    private void convertPinnedShortcuts(Realm oldRealm, Realm newRealm) {
        Collection recent = newRealm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_RECENT).findFirst();
        RealmResults<Shortcut> shortcuts = oldRealm.where(Shortcut.class).findAll();
        if (recent != null) {
            newRealm.beginTransaction();
            convertShortcutsToSlotsOfCollection(newRealm, recent, shortcuts,oldRealm);
            newRealm.commitTransaction();
        }
    }

    private void convertBlackList(SharedPreferences excludeShared,Realm newRealm) {
        Set<String> excludeSet = excludeShared.getStringSet(EdgeSetting.EXCLUDE_KEY, new HashSet<String>());
        Collection blackList = newRealm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_BLACK_LIST).findFirst();
        if (blackList != null) {
            newRealm.beginTransaction();
            for (String packageName : excludeSet) {
                Item item = newRealm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_APP).equalTo(Cons.PACKAGENAME, packageName).findFirst();
                if (item != null) {
                    blackList.items.add(item);
                }
            }
            newRealm.commitTransaction();
        }
    }

    private void convertShortcutsToSlotsOfCollection(Realm newRealm, Collection newCircle, RealmResults<Shortcut> results, Realm oldRealm) {
        for (Shortcut shortcut : results) {
            Slot slot = newCircle.slots.get(shortcut.getId());
            if (slot != null) {
                Item item = null;
                switch (shortcut.getType()) {

                    case Shortcut.TYPE_FOLDER:
                        item = null;
                        slot.type = Slot.TYPE_FOLDER;
                        RealmResults<Shortcut> shortcuts = oldRealm.where(Shortcut.class).greaterThan("id", (shortcut.getId() + 1) * 1000 - 1)
                                .lessThan("id", (shortcut.getId() + 2) * 1000).findAll();
                        for (Shortcut shortcut1 : shortcuts) {
                            Item item2 = converShortcutToItem(newRealm, shortcut1);
                            if (item2 != null) {
                                slot.items.add(item2);
                            }
                        }
                        break;
                    default:
                        item = converShortcutToItem(newRealm, shortcut);
                        break;
                }
                if (item != null && shortcut.getType() != Shortcut.TYPE_FOLDER) {
                    slot.type = Slot.TYPE_ITEM;
                    slot.stage1Item = item;
                    Log.e(TAG, "convertGrid: ok, label = " + shortcut.getLabel());
                } else {
                    Log.e(TAG, "convertGrid: cannot find item with label " + shortcut.getLabel());
                }
            }
        }
    }

    private Item converShortcutToItem(Realm newRealm, Shortcut shortcut) {
        switch (shortcut.getType()) {
            case Shortcut.TYPE_APP:
                return newRealm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_APP).equalTo(Cons.PACKAGENAME, shortcut.getPackageName()).findFirst();

            case Shortcut.TYPE_ACTION:
                return newRealm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_ACTION).equalTo(Cons.ACTION, shortcut.getAction()).findFirst();
            case Shortcut.TYPE_CONTACT:
                return newRealm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_CONTACT).equalTo(Cons.CONTACT_ID, shortcut.getContactId()).findFirst();

            case Shortcut.TYPE_SHORTCUT:
                Item item1 = new Item();
                item1.type = Item.TYPE_DEVICE_SHORTCUT;
                item1.itemId = Utility.getDeviceShortcutItemId(shortcut.getIntent());
                item1.label = shortcut.getLabel();
                item1.packageName = shortcut.getPackageName();
                item1.intent = shortcut.getIntent();
                if (shortcut.getBitmap() == null) {
                    try {
                        Resources resources = getApplicationContext().getPackageManager().getResourcesForApplication(item1.getPackageName());
                        Bitmap bmp2 = BitmapFactory.decodeResource(resources, shortcut.getResId());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        item1.iconBitmap = stream.toByteArray();
                        bmp2.recycle();
                        stream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onActivityResult: exception when setting item bitmap");
                    }
                } else {
                    item1.iconBitmap = shortcut.getBitmap();
                }

                return newRealm.copyToRealmOrUpdate(item1);
        }
        return null;
    }

    private void convertQuickActions(Realm newRealm, SharedPreferences oldShared) {
        String[] actions = new String[4];
        actions[0] = oldShared.getString(EdgeSetting.ACTION_1_KEY, MainActivity.ACTION_INSTANT_FAVO);
        actions[1] = oldShared.getString(EdgeSetting.ACTION_2_KEY, MainActivity.ACTION_HOME);
        actions[2] = oldShared.getString(EdgeSetting.ACTION_3_KEY, MainActivity.ACTION_BACK);
        actions[3] = oldShared.getString(EdgeSetting.ACTION_4_KEY, MainActivity.ACTION_NOTI);
        Collection quickActions = newRealm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_QUICK_ACTION).findFirst();
        newRealm.beginTransaction();
        if (quickActions != null) {
            for (int i = 0; i < actions.length; i++) {
                Log.e(TAG, "convertQuickActions: action = " + actions[i]);
                Slot slot = quickActions.slots.get(i);
                if (slot != null) {
                    Item item;
                    if (actions[i].equals(MainActivity.ACTION_INSTANT_FAVO)) {
                        item = newRealm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_SHORTCUTS_SET).findFirst();
                    } else {
                        item = newRealm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_ACTION).equalTo(Cons.ACTION, Utility.getActionFromStringAction(actions[i])).findFirst();
                    }
                    if (item != null) {
                        slot.type = Slot.TYPE_ITEM;
                        slot.stage1Item = item;
                    }else Log.e(TAG, "convertQuickActions: item null");
                }else Log.e(TAG, "convertQuickActions: cannot get quickActions's slot");
            }
        }else Log.e(TAG, "convertQuickActions: can not find quickActions collection");
        newRealm.commitTransaction();
    }

    public void convertSettings(Realm newRealm, SharedPreferences oldShared, SharedPreferences edge1Shared, SharedPreferences edge2Shared, SharedPreferences newShared) {
        newShared.edit().putBoolean(Cons.DISABLE_CLOCK_KEY, oldShared.getBoolean(EdgeSetting.DISABLE_CLOCK_KEY, false))
                .putBoolean(Cons.IS_DISABLE_IN_LANDSCAPE_KEY, oldShared.getBoolean(EdgeSetting.IS_DISABLE_IN_LANSCAPE, false))
                .putInt(Cons.CONTACT_ACTION_KEY, oldShared.getInt(EdgeSetting.CONTACT_ACTION, Cons.CONTACT_ACTION_CHOOSE))
                .putInt(Cons.LONG_PRESS_DELAY_KEY, oldShared.getInt(EdgeSetting.HOLD_TIME_KEY, Cons.LONG_PRESS_DELAY_DEFAULT))
                .putString(Cons.ICON_PACK_PACKAGE_NAME_KEY, oldShared.getString(EdgeSetting.ICON_PACK_PACKAGE_NAME_KEY, null))
                .putFloat(Cons.ICON_SCALE_KEY, oldShared.getFloat(EdgeSetting.ICON_SCALE, 1f))
                .putInt(Cons.BACKGROUND_COLOR_KEY, oldShared.getInt(EdgeSetting.BACKGROUND_COLOR_KEY, Cons.BACKGROUND_COLOR_DEFAULT))
                .putBoolean(Cons.USE_ANIMATION_KEY, oldShared.getBoolean(EdgeSetting.ANIMATION_KEY, true))
                .putInt(Cons.ANIMATION_TIME_KEY, oldShared.getInt(EdgeSetting.ANI_TIME_KEY, Cons.ANIMATION_TIME_DEFAULT))
                .putBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, oldShared.getBoolean(EdgeSetting.DISABLE_HAPTIC_FEEDBACK_KEY, true))
                .putBoolean(Cons.HAPTIC_ON_ICON_KEY, oldShared.getBoolean(EdgeSetting.HAPTIC_ON_ICON_KEY, false))
                .putInt(Cons.VIBRATION_DURATION_KEY, oldShared.getInt(EdgeSetting.VIBRATION_DURATION_KEY, Cons.DEFAULT_VIBRATE_DURATION))
                .putBoolean(Cons.EDGE_1_ON_KEY, edge1Shared.getBoolean(EdgeSetting.EDGE_ON_KEY,true))
                .putBoolean(Cons.EDGE_2_ON_KEY,edge2Shared.getBoolean(EdgeSetting.EDGE_ON_KEY,false))
                .commit();

        String[]  edgePositionsArray = getResources().getStringArray(R.array.edge_positions_array);

        final boolean useGuide = oldShared.getBoolean(EdgeSetting.USE_GUIDE_KEY, true);
        final int guideColor = oldShared.getInt(EdgeSetting.GUIDE_COLOR_KEY, Cons.GUIDE_COLOR_DEFAULT);
        final boolean keyboardOption = oldShared.getBoolean(EdgeSetting.AVOID_KEYBOARD_KEY, true);
        final int position1 = Utility.getPositionIntFromString(edge1Shared.getString(EdgeSetting.EDGE_POSITION_KEY, edgePositionsArray[1]), this);
        final int position2 = Utility.getPositionIntFromString(edge2Shared.getString(EdgeSetting.EDGE_POSITION_KEY, edgePositionsArray[5]),this);

        final int length1 = edge1Shared.getInt(EdgeSetting.EDGE_LENGTH_KEY, Cons.DEFAULT_EDGE_LENGTH);
        final int length2 = edge2Shared.getInt(EdgeSetting.EDGE_LENGTH_KEY, Cons.DEFAULT_EDGE_LENGTH);
        final boolean isOnlyFavorite1 = edge1Shared.getBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false);
        final boolean isOnlyFavorite2 = edge2Shared.getBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false);
        final int offset1 = edge1Shared.getInt(EdgeSetting.EDGE_OFFSET_KEY, 0);
        final int offset2 = edge2Shared.getInt(EdgeSetting.EDGE_OFFSET_KEY, 0);
        final int sensitive1 = edge1Shared.getInt(EdgeSetting.EDGE_SENSIIVE_KEY, Cons.DEFAULT_EDGE_SENSITIVE);
        final int sensitive2 = edge2Shared.getInt(EdgeSetting.EDGE_SENSIIVE_KEY, Cons.DEFAULT_EDGE_SENSITIVE);
        final int circleSize = oldShared.getInt(EdgeSetting.CIRCLE_SIZE_KEY, Cons.CIRCLE_RADIUS_DEFAULT);

        newRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Edge> edges = realm.where(Edge.class).findAll();
                Collection recent = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_RECENT).findFirst();
                Collection circle = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_CIRCLE_FAVORITE).findFirst();
                for (Edge edge : edges) {
                    edge.guideColor = guideColor;
                    edge.useGuide = useGuide;
                    edge.keyboardOption = keyboardOption ? Edge.KEYBOARD_OPTION_PLACE_UNDER : Edge.KEYBOARD_OPTION_NONE;
                    if (edge.edgeId.equals(Edge.EDGE_1_ID)) {
                        edge.position = position1;
                        edge.length = length1;
                        edge.offset = offset1;
                        edge.sensitive = sensitive1;
                        if (isOnlyFavorite1) {
                            edge.mode = Edge.MODE_GRID;
                            Collection grid = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_GRID_FAVORITE).findFirst();
                            if (grid != null) {
                                edge.grid = grid;
                            }
                        }
                        edge.mode = isOnlyFavorite1 ? Edge.MODE_GRID : Edge.MODE_RECENT_AND_QUICK_ACTION;
                    } else if (edge.edgeId.equals(Edge.EDGE_2_ID)) {
                        edge.position = position2;
                        edge.length = length2;
                        edge.offset = offset2;
                        edge.sensitive = sensitive2;
                        if (isOnlyFavorite2) {
                            edge.mode = Edge.MODE_GRID;
                            Collection grid = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_GRID_FAVORITE).findFirst();
                            if (grid != null) {
                                edge.grid = grid;
                            }
                        }
                    }

                }
                if (recent != null) {
                    recent.radius = circleSize;
                }
                if (circle != null) {
                    circle.radius = circleSize;
                }
            }
        });



    }

    private void generateInitSetForGrid(Realm realm) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        Collection grid = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, Utility.createCollectionId(Collection.TYPE_GRID_FAVORITE, 1)).findFirst();
        if (grid == null) {
            return;
        }

        realm.beginTransaction();
        Item[] items = new Item[grid.columnCount * grid.rowsCount];
        items[0] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createActionItemId(Item.ACTION_WIFI)).findFirst();
        items[1] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createActionItemId(Item.ACTION_FLASH_LIGHT)).findFirst();
        items[2] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createActionItemId(Item.ACTION_VOLUME)).findFirst();
        items[3] = realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createActionItemId(Item.ACTION_BRIGHTNESS)).findFirst();

        long currentTimeMillis = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY, currentTimeMillis - 7 * 24 * 60 * 60 * 1000, currentTimeMillis);
        Set<String> packageSet = new ArraySet<>();
        if (stats != null) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>(Cons.DATE_DECENDING_COMPARATOR);
            for (UsageStats usageStats : stats) {
                long timeGround = usageStats.getTotalTimeInForeground();
                if (mySortedMap.containsKey(timeGround)) {
                    timeGround = timeGround + 1;
                }
                if (!packageSet.contains(usageStats.getPackageName())) {
                    mySortedMap.put(timeGround, usageStats);
                    packageSet.add(usageStats.getPackageName());
                }
            }
            Set<Long> keysSet = mySortedMap.keySet();
            int i = 4;
            Item item;
            String packa;
            UsageStats usageStats;
            Log.e(TAG, "generateInitSetForGrid: keysSet size = "+ keysSet.size());
            for (Long key : keysSet) {
                if (i < items.length) {
                    usageStats = mySortedMap.get(key);
                    if (usageStats != null) {
                        packa = usageStats.getPackageName();
                        if (packa != null) {
                            item = realm.where(Item.class).equalTo(Cons.ITEM_ID, Utility.createAppItemId(packa)).findFirst();
                            if (item != null) {
                                Log.e(TAG, "generateInitSetForGrid: add item " + packa + "\ntime foreground = " + key);
                                items[i] = item;
                                i++;
                            } else {
                                Log.e(TAG, "generateInitSetForGrid: cannot find item " + packa);
                            }
                        }
                    }

                }
            }

            Slot slot;
            for (int i1 = 0; i1 < items.length; i1++) {
                if (items[i1] != null) {
                    slot = grid.slots.get(i1);
                    if (slot.type.equals(Slot.TYPE_EMPTY) || slot.type.equals(Slot.TYPE_NULL)) {
                        slot.type = Slot.TYPE_ITEM;
                        slot.stage1Item = items[i1];
                    } else {
                        Log.e(TAG, "generateInitSetForGrid: slot already fill");
                    }
                } else {
                    Log.e(TAG, "generateInitSetForGrid: item null, cannot add");
                }
            }
        }
        DataInfo dataInfo = realm.where(DataInfo.class).findFirst();
        dataInfo.initGridItemOk = true;

        realm.commitTransaction();

    }
}
