package org.de_studio.recentappswitcher.favoriteShortcut;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.MyApplication;
import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by HaiNguyen on 6/3/16.
 */
public class AddContactToFolderDialogFragment extends DialogFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = AddContactToFolderDialogFragment.class.getSimpleName();
    static ListView mListView;
    private Realm myRealm;
    private int mPosition;
    AddContactToFolderAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_favorite_app_fragment_list_view, container);
    }

    public void setmPosition(int position) {
        mPosition = position;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().stopService(new Intent(getActivity(), EdgeGestureService.class));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (ListView) getView(). findViewById(R.id.add_favorite_list_view);
        myRealm = Realm.getInstance(new RealmConfiguration.Builder(getActivity())
                .name("default.realm")
                .schemaVersion(EdgeGestureService. CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        mAdapter = new AddContactToFolderAdapter(getActivity(),null,0,mPosition);
        mListView.setAdapter(mAdapter);
        final int startId = (mPosition +1)*1000;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.add_favorite_list_item_check_box);
                int size = (int) myRealm.where(Shortcut.class).greaterThan("id",startId -1).lessThan("id",startId + 1000).count();
                String name = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                Long contactId = mAdapter.getCursor().getLong(mAdapter.getCursor().getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String thumbnailUri = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                String number = mAdapter.getCursor().getString(mAdapter.getCursor().getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                if (checkBox != null) {
                    if (checkBox.isChecked()) {
                        myRealm.beginTransaction();
                        Shortcut removeShortcut = myRealm.where(Shortcut.class).greaterThan("id",startId -1).
                                lessThan("id", startId + 1000).equalTo("type",Shortcut.TYPE_CONTACT) .
                                equalTo("contactId",contactId).findFirst();
                        int removeId = removeShortcut.getId();
                        Log.e(LOG_TAG, "removeID = " + removeId);
                        removeShortcut.deleteFromRealm();
                        RealmResults<Shortcut> results = myRealm.where(Shortcut.class).greaterThan("id",startId -1).lessThan("id",startId + 1000).findAll().sort("id", Sort.ASCENDING);
                        for (int i = startId; i < startId+ results.size(); i++) {
                            Log.e(LOG_TAG, "id = " + results.get(i- startId).getId());
                            if (results.get(i - startId).getId() >= removeId) {
//                                Log.e(LOG_TAG, "when i = " + i + "result id = " + results.get(i - startId).getId());
                                Shortcut shortcut = results.get(i - startId);
                                int oldId = shortcut.getId();
                                shortcut.setId(oldId - 1);
                            }
                        }
                        myRealm.commitTransaction();
                    } else {
                        if (size < 16) {
                            Shortcut newShortcut = new Shortcut();
                            newShortcut.setId(startId+ size);
//                            Log.e(LOG_TAG, "size = " + size);
                            newShortcut.setName(name);
                            newShortcut.setContactId(contactId);
                            newShortcut.setType(Shortcut.TYPE_CONTACT);
                            newShortcut.setThumbnaiUri(thumbnailUri);
                            newShortcut.setNumber(number);
                            myRealm.beginTransaction();
                            myRealm.copyToRealm(newShortcut);
                            myRealm.commitTransaction();
                        } else {
                            Toast.makeText(MyApplication.getContext(),getString(R.string.out_of_limit),Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        });
        myRealm = Realm.getInstance(new RealmConfiguration.Builder(getContext())
                .name("default.realm")
                .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
                    121);
            dismiss();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Utility.getFolderThumbnail(myRealm, mPosition, getActivity());
        try {
            getActivity().startService(new Intent(getActivity(), EdgeGestureService.class));
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Null when get activity from on dismiss");
        }
        super.onDismiss(dialog);
        ((AddAppToFolderDialogFragment.MyDialogCloseListener) getActivity()).handleDialogClose();
    }
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }
}
