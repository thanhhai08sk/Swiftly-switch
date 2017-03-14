package org.de_studio.recentappswitcher.base;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class ContactLoader implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ContactLoader.class.getSimpleName();
    Context context;

    public ContactLoader(Context context) {
        this.context = context;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sordOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        return new CursorLoader(
                context,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                sordOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,final Cursor data) {
        if (data == null) {
            return;
        }
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
                        newItem.label = Utility.getContactItemLabel(type, defaultName, context);
                        newItem.number = number;
                        newItem.contactId = contactId;
                        newItem.iconUri = iconUri;
                        realm.copyToRealm(newItem);
                    } else if (iconUri != null &&
                            (item.iconUri == null || !item.iconUri.equals(iconUri))) {
                        Log.e(TAG, "execute: update contact icon " + defaultName);
                        item.iconUri = iconUri;
                    }
                }
            }
        });

        realm.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
