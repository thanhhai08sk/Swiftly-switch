package org.de_studio.recentappswitcher;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
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

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by HaiNguyen on 7/1/16.
 */
public class PinRecentAddContactDialogFragment extends DialogFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = PinRecentAddContactDialogFragment.class.getSimpleName();
    static ListView mListView;
    private Realm myRealm;
    private PinRecentAddContactAdapter mAdapter;
    public static final String POSITION_KEY = "position";
    private int position;

    public static PinRecentAddContactDialogFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        PinRecentAddContactDialogFragment fragment = new PinRecentAddContactDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_favorite_app_fragment_list_view, container);
        mListView = (ListView) rootView.findViewById(R.id.add_favorite_list_view);
        mAdapter = new PinRecentAddContactAdapter(getContext(),null,0);
        mListView.setAdapter(mAdapter);


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
                    121);
            dismiss();
        }


        myRealm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("pinApp.realm")
                .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.add_favorite_list_item_check_box);
                int size = (int) myRealm.where(Shortcut.class).count();
                Cursor cursor = mAdapter.getCursor();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                Long contactId = cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String thumbnailUri = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Shortcut removeShortcut = myRealm.where(Shortcut.class).equalTo("id", PinRecentAddContactDialogFragment.this.position).findFirst();

                Shortcut newShortcut = new Shortcut();
                newShortcut.setId(PinRecentAddContactDialogFragment.this.position);
//                            Log.e(TAG, "size = " + size);
                newShortcut.setName(name);
                newShortcut.setContactId(contactId);
                newShortcut.setType(Shortcut.TYPE_CONTACT);
                newShortcut.setThumbnaiUri(thumbnailUri);
                newShortcut.setNumber(number);
                myRealm.beginTransaction();
                if (removeShortcut != null) {
                    removeShortcut.deleteFromRealm();
                }
                myRealm.copyToRealm(newShortcut);
                myRealm.commitTransaction();

//                if (checkBox != null) {
//                    if (checkBox.isChecked()) {
//                        myRealm.beginTransaction();
//                        Shortcut removeShortcut = myRealm.where(Shortcut.class).equalTo("type", Shortcut.TYPE_CONTACT).
//                                equalTo("contactId", contactId).findFirst();
//                        int removeId = removeShortcut.getId();
//                        Log.e(TAG, "removeID = " + removeId);
//                        removeShortcut.deleteFromRealm();
//                        RealmResults<Shortcut> results = myRealm.where(Shortcut.class).findAll().sort("id", Sort.ASCENDING);
//                        for (int i = 0; i < results.size(); i++) {
//                            Log.e(TAG, "id = " + results.get(i).getId());
//                            if (results.get(i).getId() >= removeId) {
//                                Log.e(TAG, "when i = " + i + "result id = " + results.get(i).getId());
//                                Shortcut shortcut = results.get(i);
//                                int oldId = shortcut.getId();
//                                shortcut.setId(oldId - 1);
//                            }
//                        }
//                        myRealm.commitTransaction();
//                    } else {
//                        if (size < 6) {
//                            Shortcut newShortcut = new Shortcut();
//                            newShortcut.setId(size);
////                            Log.e(TAG, "size = " + size);
//                            newShortcut.setName(name);
//                            newShortcut.setContactId(contactId);
//                            newShortcut.setType(Shortcut.TYPE_CONTACT);
//                            newShortcut.setThumbnaiUri(thumbnailUri);
//                            newShortcut.setNumber(number);
//                            myRealm.beginTransaction();
//                            myRealm.copyToRealm(newShortcut);
//                            myRealm.commitTransaction();
//                        } else {
//                            Toast.makeText(MyApplication.getContext(), getString(R.string.out_of_limit), Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                }

                mAdapter.notifyDataSetChanged();
                dismiss();
            }
            });
        return rootView;
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
        position = getArguments().getInt(POSITION_KEY);
        Utility.stopService(getActivity());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            Utility.startService(getActivity());
        } catch (NullPointerException e) {
            Log.e(TAG, "Null when get activity from on dismiss");
        }

        super.onDismiss(dialog);
//        ((AddAppToFolderDialogFragment.MyDialogCloseListener) getActivity()).handleDialogClose();
    }

    @Override
    public void onDestroy() {
        if (myRealm != null) {
            myRealm.close();
        }
        super.onDestroy();
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
