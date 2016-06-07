package org.de_studio.recentappswitcher.favoriteShortcut;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 5/30/16.
 */
public class ContactTabFragment extends android.support.v4.app.Fragment
            implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener{
    private Realm myRealm;
    private int mPosition, mode;
    private static final String TAG = ContactTabFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int MY_PERMISSIONS_REQUEST = 222;
    ListView mContactsList;
    LinearLayout permissionLayout;
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
    public void setMode(int mode) {
        this.mode = mode;
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
        permissionLayout = (LinearLayout) getActivity().findViewById(R.id.permission_missing);
        mAdapter = new ContactCursorAdapter(getActivity(), null, 0, mPosition);
        mContactsList.setAdapter(mAdapter);
        mContactsList.setOnItemClickListener(this);
        myRealm = Realm.getDefaultInstance();
        ((ChooseShortcutActivity)getActivity()).setContactAdapter(mAdapter);
//        mCursorAdapter = new SimpleCursorAdapter(
//                getActivity(),
//                R.layout.item_contact_list,
//                null,
//                FROM_COLUMNS, TO_IDS,
//                0);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionLayout.setVisibility(View.VISIBLE);

        } else {
            getLoaderManager().initLoader(0, null, this);
            permissionLayout.setVisibility(View.GONE);
        }
        permissionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE,Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST);
            }
        });

    }

    public void setmPositioinToNext() {
        if (mPosition < Utility.getSizeOfFavoriteGrid(getContext())-1 &&  mAdapter !=null) {
            mPosition++;
            try {
                mAdapter.setmPositionAndMode(mPosition);
            } catch (NullPointerException e) {
                Log.e(TAG, "mAdapter = null");
            }

        }
    }

    public void setmPositionToBack() {
        if (mPosition > 0 && mAdapter != null) {
            mPosition--;
            mAdapter.setmPositionAndMode(mPosition);
        }
    }
    @Override
    public void onItemClick(
            AdapterView<?> parent, View item, int position, long rowID) {
        ContactCursorAdapter adapter = (ContactCursorAdapter) parent.getAdapter();
        Cursor cursor =adapter.getCursor();
        cursor.moveToPosition(position);
        String stringUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
        myRealm.beginTransaction();
        RealmResults<Shortcut> oldShortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findAll();
        Log.e(TAG, "mPosition = " + mPosition);
        oldShortcut.clear();
        Shortcut shortcut = new Shortcut();
        shortcut.setType(Shortcut.TYPE_CONTACT);
        shortcut.setId(mPosition);
        shortcut.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));
//        shortcut.setThumbnaiUri(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)));
        shortcut.setContactId(cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        shortcut.setName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
        if (stringUri != null) {
            shortcut.setThumbnaiUri(stringUri);
        }
        myRealm.copyToRealm(shortcut);
        myRealm.commitTransaction();
        adapter.notifyDataSetChanged();
        adapter.getListener().onAppChange();

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                Log.e(TAG, "onRequestPermissionsResult: result size = " + grantResults.length);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLoaderManager().initLoader(0, null, this);
                }
                permissionLayout.setVisibility(View.GONE);
            }

        }
    }
}
