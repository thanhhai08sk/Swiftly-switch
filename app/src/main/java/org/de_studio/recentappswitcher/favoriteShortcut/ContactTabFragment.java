package org.de_studio.recentappswitcher.favoriteShortcut;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.de_studio.recentappswitcher.R;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 5/30/16.
 */
public class ContactTabFragment extends android.support.v4.app.Fragment
            implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener{
    private Realm myRealm;
    private int mPosition;
    private static final String LOG_TAG = ContactTabFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private final static String[] FROM_COLUMNS = {
            ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
    };
    private static final int CONTACT_ID_INDEX = 0;
    private static final int LOOKUP_KEY_INDEX = 1;


    private static final String[] PROJECTION =
            {
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                    ContactsContract.CommonDataKinds.Phone.NUMBER

            };

    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    ListView mContactsList;
    long mContactId;
    String mContactKey;
    Uri mContactUri;
    private ContactCursorAdapter mAdapter;

    public ContactTabFragment() {}
    public static ContactTabFragment newInstance(int sectionNumber) {
        ContactTabFragment fragment = new ContactTabFragment();
        Bundle agument = new Bundle();
        agument.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(agument);
        return fragment;
    }
    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


            return inflater.inflate(R.layout.fragment_contacts,
                    container, false);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContactsList =
                (ListView) getActivity().findViewById(R.id.list_view);
        mAdapter = new ContactCursorAdapter(getActivity(), null, 0, mPosition);
        mContactsList.setAdapter(mAdapter);
        mContactsList.setOnItemClickListener(this);
        myRealm = Realm.getDefaultInstance();
//        mCursorAdapter = new SimpleCursorAdapter(
//                getActivity(),
//                R.layout.item_contact_list,
//                null,
//                FROM_COLUMNS, TO_IDS,
//                0);
        getLoaderManager().initLoader(0, null, this);
    }
    @Override
    public void onItemClick(
            AdapterView<?> parent, View item, int position, long rowID) {
        Cursor cursor =((ContactCursorAdapter) parent.getAdapter()).getCursor();
        cursor.moveToPosition(position);
        String stringUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        myRealm.beginTransaction();
        RealmResults<Shortcut> oldShortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findAll();
        Log.e(LOG_TAG, "mPosition = " + mPosition);
        oldShortcut.clear();
        Shortcut shortcut = new Shortcut();
        shortcut.setType(Shortcut.TYPE_CONTACT);
        shortcut.setId(mPosition);
        shortcut.setContactId(cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        shortcut.setName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        myRealm.copyToRealm(shortcut);
        if (stringUri != null) {
            shortcut.setThumbnaiUri(stringUri);
        }
        myRealm.commitTransaction();
        ((ContactCursorAdapter) parent.getAdapter()).notifyDataSetChanged();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        return new CursorLoader(
                getActivity(),
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }
}
