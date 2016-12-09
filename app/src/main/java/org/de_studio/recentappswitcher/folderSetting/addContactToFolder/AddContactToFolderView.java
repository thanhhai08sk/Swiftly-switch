package org.de_studio.recentappswitcher.folderSetting.addContactToFolder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderView;
import org.de_studio.recentappswitcher.dagger.AddContactToFolderModule;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerAddContactToFolderComponent;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 12/9/16.
 */

public class AddContactToFolderView extends BaseAddItemsToFolderView implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = AddContactToFolderView.class.getSimpleName();


    public static AddContactToFolderView newInstance(String folderId) {

        Bundle args = new Bundle();
        args.putString(Cons.SLOT_ID, folderId);
        AddContactToFolderView fragment = new AddContactToFolderView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void loadItems() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
                    121);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sordOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        return new CursorLoader(
                getActivity(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                sordOrder
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,final Cursor data) {
        Log.e(TAG, "onLoadFinished: load contact, size = " + data.getCount());
        Realm realm = Realm.getDefaultInstance();
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
                        newItem.label = Utility.getContactItemLabel(type, defaultName, getActivity());
                        newItem.number = number;
                        newItem.contactId = contactId;
                        newItem.iconUri = iconUri;
                        realm.copyToRealm(newItem);
                    }
                }
            }
        });

        realm.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void inject() {
        DaggerAddContactToFolderComponent.builder()
                .appModule(new AppModule(getActivity()))
                .addContactToFolderModule(new AddContactToFolderModule(this, slotId))
                .build().inject(this);
    }
}
