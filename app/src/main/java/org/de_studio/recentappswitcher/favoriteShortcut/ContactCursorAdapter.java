package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.R;

/**
 * Created by HaiNguyen on 5/30/16.
 */
public class ContactCursorAdapter extends CursorAdapter {
    private Context context;

    public ContactCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView avatar =(ImageView) view.findViewById(R.id.avatar);
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        String stringUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        if (stringUri != null) {
            Uri uri = Uri.parse(stringUri);
            avatar.setImageURI(uri);
        }else avatar.setImageDrawable(null);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.item_contact_list, parent, false);
    }
}
