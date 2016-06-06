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

import org.de_studio.recentappswitcher.R;

import java.io.IOException;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 6/3/16.
 */
public class AddContactToFolderAdapter extends CursorAdapter {
    private int mPosition;
    private Realm myRealm;

    public AddContactToFolderAdapter(Context context, Cursor c, int flags, int mPosition) {
        super(context, c, flags);
        this.mPosition = mPosition;
        myRealm = Realm.getDefaultInstance();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView icon = (ImageView) view.findViewById(R.id.add_favorite_list_item_image_view);
        TextView label = (TextView) view.findViewById(R.id.add_favorite_list_item_label_text_view);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.add_favorite_list_item_check_box);
        int startId = (mPosition +1)* 1000;
        long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        label.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        String stringUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        if (stringUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(stringUri));
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
                drawable.setCircular(true);
                icon.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
                icon.setImageResource(R.drawable.ic_icon_home);
            }
        } else {
            icon.setImageResource(R.drawable.ic_icon_home);
        }

//        if (stringUri != null) {
//            Uri uri = Uri.parse(stringUri);
//            icon.setImageURI(uri);
//        }else icon.setImageResource(R.drawable.ic_icon_home);
        if (myRealm.where(Shortcut.class).equalTo("type",Shortcut.TYPE_CONTACT).
                equalTo("contactId", contactId).greaterThan("id", startId -1).
                lessThan("id",startId + 1000).findFirst() != null) {
            checkBox.setChecked(true);
        }else checkBox.setChecked(false);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.item_dialog_favorite_app, parent, false);
    }



}
