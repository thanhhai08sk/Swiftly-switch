package org.de_studio.recentappswitcher;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by HaiNguyen on 7/1/16.
 */
public class PinRecentAddActionDialogFragment extends DialogFragment{
    private static final String TAG = PinRecentAddActionDialogFragment.class.getSimpleName();
    static ListView mListView;
    private Realm myRealm;
    private PinRecentAddActionAdapter mAdapter;
    private String[] stringArray;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_favorite_app_fragment_list_view, container);
        myRealm = Realm.getInstance(new RealmConfiguration.Builder(getContext())
                .name("pinApp.realm")
                .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        stringArray = getActivity().getResources().getStringArray(R.array.setting_shortcut_array_no_folder);
        mListView = (ListView) rootView.findViewById(R.id.add_favorite_list_view);
        mAdapter = new PinRecentAddActionAdapter(getActivity(), myRealm);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.add_favorite_list_item_check_box);
                int size = (int) myRealm.where(Shortcut.class).count();
                String item =(String) mAdapter.getItem(position);
                int action = Utility.getActionFromLabel(getActivity(), item);
                if (checkBox != null) {
                    if (checkBox.isChecked()) {
                        myRealm.beginTransaction();
                        Shortcut removeShortcut = myRealm.where(Shortcut.class).equalTo("type",Shortcut.TYPE_ACTION) .equalTo("action",action).findFirst();
                        int removeId = removeShortcut.getId();
                        Log.e(TAG, "removeID = " + removeId);
                        removeShortcut.deleteFromRealm();
                        RealmResults<Shortcut> results = myRealm.where(Shortcut.class).findAll().sort("id", Sort.ASCENDING);
                        for (int i = 0; i < results.size(); i++) {
                            Log.e(TAG, "id = " + results.get(i).getId());
                            if (results.get(i).getId() >= removeId) {
                                Log.e(TAG, "when i = " + i + "result id = " + results.get(i).getId());
                                Shortcut shortcut = results.get(i);
                                int oldId = shortcut.getId();
                                shortcut.setId(oldId - 1);
                            }
                        }
                        myRealm.commitTransaction();
                    } else {
//                        if (stringArray[position].equalsIgnoreCase(getActivity().getString(R.string.setting_shortcut_folder))) {
//                            checkBox.setChecked(false);
//                            Toast.makeText(getContext(), getString(R.string.out_of_limit), Toast.LENGTH_SHORT).show();
//                        }
                        if (size < 6) {
                            Shortcut newShortcut = new Shortcut();
                            newShortcut.setId(size);
                            newShortcut.setAction(action);
                            newShortcut.setLabel(item);
                            newShortcut.setType(Shortcut.TYPE_ACTION);
                            myRealm.beginTransaction();
                            myRealm.copyToRealm(newShortcut);
                            myRealm.commitTransaction();
                        } else {
                            Toast.makeText(MyApplication.getContext(),getString(R.string.out_of_limit),Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                if ((stringArray[position].equalsIgnoreCase(getActivity().getString(R.string.setting_shortcut_rotation)) ||
                        stringArray[position].equalsIgnoreCase(getActivity().getString(R.string.setting_shortcut_brightness))) &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        !Settings.System.canWrite(getActivity())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.write_setting_permission)
                            .setMessage(R.string.write_setting_permission_explain)
                            .setPositiveButton(R.string.go_to_setting, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent notiIntent = new Intent();
                                    notiIntent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                    getActivity().startActivity(notiIntent);

                                }
                            });
                    builder.show();
                }
                if (stringArray[position].equalsIgnoreCase(getActivity().getString(R.string.setting_shortcut_screen_lock))) {
                    Utility.askForAdminPermission(getActivity());
                }

                mAdapter.notifyDataSetChanged();
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
        getActivity().stopService(new Intent(getActivity(), EdgeGestureService.class));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            getActivity().startService(new Intent(getActivity(), EdgeGestureService.class));
        } catch (NullPointerException e) {
            Log.e(TAG, "Null when get activity from on dismiss");
        }

        super.onDismiss(dialog);
//        ((AddAppToFolderDialogFragment.MyDialogCloseListener) getActivity()).handleDialogClose();
    }

}
