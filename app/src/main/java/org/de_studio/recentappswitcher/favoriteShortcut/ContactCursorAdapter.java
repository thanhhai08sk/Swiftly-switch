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

import org.de_studio.recentappswitcher.R;

import java.io.IOException;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 5/30/16.
 */
public class ContactCursorAdapter extends CursorAdapter {
    private Context context;
    private int mPosition;
    private Realm myRealm;
    private Shortcut shortcut;
    private AppListAdapter.AppChangeListener listener;

    public ContactCursorAdapter(Context context, Cursor c, int flags, int mPosition) {
        super(context, c, flags);
        this.context = context;
        this.mPosition = mPosition;
        myRealm = Realm.getDefaultInstance();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        shortcut = myRealm.where(Shortcut.class).equalTo("id", mPosition).findFirst();
        ImageView avatar =(ImageView) view.findViewById(R.id.avatar);
        TextView name = (TextView) view.findViewById(R.id.name);
        long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.radio_button);
        name.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
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
        if (shortcut != null && shortcut.getType() == Shortcut.TYPE_CONTACT && shortcut.getContactId() == contactId) {
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
}
