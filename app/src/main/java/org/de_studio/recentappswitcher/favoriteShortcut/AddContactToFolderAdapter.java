package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by HaiNguyen on 6/3/16.
 */
public class AddContactToFolderAdapter extends CursorAdapter {
    private int mPosition;
    private Realm myRealm;

    public AddContactToFolderAdapter(Context context, Cursor c, int flags, int mPosition) {
        super(context, c, flags);
        this.mPosition = mPosition;
        myRealm = Realm.getInstance(new RealmConfiguration.Builder(context)
                .name("default.realm")
                .schemaVersion(EdgeGestureService. CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView icon = (ImageView) view.findViewById(R.id.add_favorite_list_item_image_view);
        TextView label = (TextView) view.findViewById(R.id.add_favorite_list_item_label_text_view);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.add_favorite_list_item_check_box);
        int startId = (mPosition +1)* 1000;
        String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
        long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        int type = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
        String defaultName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                label.setText(defaultName);
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                label.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_work)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                label.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_home)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                label.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_main)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                label.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_work_fax)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                label.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_pager)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                label.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_other)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                label.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_custom)));
                break;
            default:
                label.setText(defaultName);
                break;
        }
        String stringUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        if (stringUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(stringUri));
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
                drawable.setCircular(true);
                icon.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
                icon.setImageResource(R.drawable.ic_contact_default);
            }
        } else {
            icon.setImageResource(R.drawable.ic_contact_default);
        }

//        if (stringUri != null) {
//            Uri uri = Uri.parse(stringUri);
//            icon.setImageURI(uri);
//        }else icon.setImageResource(R.drawable.ic_icon_home);
        if (myRealm.where(Shortcut.class).equalTo("type",Shortcut.TYPE_CONTACT).
                equalTo("number", number).greaterThan("id", startId -1).
                lessThan("id",startId + 1000).findFirst() != null) {
            checkBox.setChecked(true);
        }else checkBox.setChecked(false);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.item_dialog_favorite_app, parent, false);
    }



}
