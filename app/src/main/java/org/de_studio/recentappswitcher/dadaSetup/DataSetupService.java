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
import org.de_studio.recentappswitcher.model.Item;

import java.util.Set;

import io.realm.Realm;

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
                handleActionCreateItems();
            } else if (ACTION_CONVERT.equals(action)) {
                handleActionConvert();
            }
        }
    }

    private void handleActionCreateItems() {


    }


    private void handleActionConvert() {

    }


    private Set<PackageInfo> getAppsList() {
        return Utility.getInstalledApps(getPackageManager());
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
