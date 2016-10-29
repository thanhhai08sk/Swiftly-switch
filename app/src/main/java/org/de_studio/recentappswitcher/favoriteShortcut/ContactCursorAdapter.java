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
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by HaiNguyen on 5/30/16.
 */
public class ContactCursorAdapter extends CursorAdapter {
    private Context context;
    private int mPosition;
    private Realm myRealm;
    private Shortcut shortcut;
    private AppListAdapter.AppChangeListener listener;

    public ContactCursorAdapter(Context context, Cursor c, int flags, int mPosition, int mode) {
        super(context, c, flags);
        this.context = context;
        this.mPosition = mPosition;
//        myRealm = Realm.getInstance(new RealmConfiguration.Builder(context)
//                .name("default.realm")
//                .schemaVersion(EdgeGestureService. CURRENT_SCHEMA_VERSION)
//                .migration(new MyRealmMigration())
//                .build());

        if (mode == FavoriteSettingActivity.MODE_GRID || mode == FavoriteSettingActivity.MODE_FOLDER) {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder(context)
                    .name("default.realm")
                    .schemaVersion(EdgeGestureService. CURRENT_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        } else {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder(context)
                    .name("circleFavo.realm")
                    .schemaVersion(EdgeGestureService. CURRENT_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        }
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        shortcut = myRealm.where(Shortcut.class).equalTo("id", mPosition).findFirst();
        ImageView avatar =(ImageView) view.findViewById(R.id.avatar);
        TextView name = (TextView) view.findViewById(R.id.name);
        long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.radio_button);
        String defaultName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        int type = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                name.setText(defaultName);
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                name.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_work)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                name.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_home)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                name.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_main)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                name.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_work_fax)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                name.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_pager)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                name.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_other)));
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                name.setText(String.format("%s(%s)", defaultName, context.getString(R.string.contact_type_custom)));
                break;
            default:
                name.setText(defaultName);
                break;
        }
        String stringUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        if (stringUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(stringUri));
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
                drawable.setCircular(true);
                avatar.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
                avatar.setImageResource(R.drawable.ic_contact_default);
            }
        } else {
            avatar.setImageResource(R.drawable.ic_contact_default);
        }
        if (shortcut != null && shortcut.getType() == Shortcut.TYPE_CONTACT && shortcut.getNumber().equalsIgnoreCase(number)) {
            radioButton.setChecked(true);
        }else radioButton.setChecked(false);

    }
    public void setmPositionAndMode(int position) {
        mPosition = position;
        ContactCursorAdapter.this.notifyDataSetChanged();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.item_contact_list, parent, false);
    }
    public interface ContactChangeListener{
        void onContactChange();
    }
    public void registerListener(AppListAdapter.AppChangeListener listener) {
        this.listener = listener;
    }

    public AppListAdapter.AppChangeListener getListener() {
        return listener;
    }

    public void clear() {
        if (myRealm != null) {
            myRealm.close();
        }
    }
}
