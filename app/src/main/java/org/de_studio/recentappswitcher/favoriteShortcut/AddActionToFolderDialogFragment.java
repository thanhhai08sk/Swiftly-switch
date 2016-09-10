package org.de_studio.recentappswitcher.favoriteShortcut;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import org.de_studio.recentappswitcher.MyApplication;
import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.shortcut.LockAdmin;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by HaiNguyen on 6/3/16.
 */
public class AddActionToFolderDialogFragment extends DialogFragment {
    private static final String LOG_TAG = AddActionToFolderDialogFragment.class.getSimpleName();
    static ListView mListView;
    private ProgressBar progressBar;
    private Realm myRealm;
    private int mPosition;
    private String[] stringArray;
    AddActionToFolderAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_favorite_app_fragment_list_view, container);
        mListView = (ListView) rootView.findViewById(R.id.add_favorite_list_view);
        stringArray = getActivity().getResources().getStringArray(R.array.setting_shortcut_array);
        myRealm = Realm.getInstance(new RealmConfiguration.Builder(getContext())
                .name("default.realm")
                .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        mAdapter = new AddActionToFolderAdapter(getActivity(), myRealm, mPosition);
        mListView.setAdapter(mAdapter);
        final int startId = (mPosition +1)*1000;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.add_favorite_list_item_check_box);
                int size = (int) myRealm.where(Shortcut.class).greaterThan("id",startId -1).lessThan("id",startId + 1000).count();
                String item =(String) mAdapter.getItem(position);
                int action = Utility.getActionFromLabel(getActivity(), item);
                if (checkBox != null) {
                    if (checkBox.isChecked()) {
                        myRealm.beginTransaction();
                        Shortcut removeShortcut = myRealm.where(Shortcut.class).greaterThan("id",startId -1).lessThan("id", startId + 1000).equalTo("type",Shortcut.TYPE_ACTION) .equalTo("action",action).findFirst();
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
                                    notiIntent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                    getActivity().startActivity(notiIntent);

                                }
                            });
                    builder.show();
                }

                if (stringArray[position].equalsIgnoreCase(getActivity().getString(R.string.setting_shortcut_screen_lock))) {
                    final ComponentName cm = new ComponentName(getContext(), LockAdmin.class);
                    final DevicePolicyManager pm = (DevicePolicyManager) getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
                    if (!pm.isAdminActive(cm)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.admin_permission)
                                .setMessage(R.string.admin_permission_explain)
                                .setPositiveButton(R.string.go_to_setting, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cm);
                                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                                getActivity().getString(R.string.admin_desc));
                                        startActivity(intent);

                                    }
                                });
                        builder.show();
                    }


                }



                    mAdapter.notifyDataSetChanged();
            }
        });
//        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.VISIBLE);
        return rootView;
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
        Utility.stopService(getActivity());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Utility.getFolderThumbnail(myRealm, mPosition, getActivity());
        try {
            Utility.startService(getActivity());
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Null when get activity from on dismiss");
        }
        super.onDismiss(dialog);
        ((AddAppToFolderDialogFragment.MyDialogCloseListener) getActivity()).handleDialogClose();
    }

}
