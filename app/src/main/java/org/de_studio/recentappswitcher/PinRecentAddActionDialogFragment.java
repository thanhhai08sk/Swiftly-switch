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
import android.widget.ListView;

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by HaiNguyen on 7/1/16.
 */
public class PinRecentAddActionDialogFragment extends DialogFragment{
    private static final String TAG = PinRecentAddActionDialogFragment.class.getSimpleName();
    static ListView mListView;
    private Realm myRealm;
    private PinRecentAddActionAdapter mAdapter;
    private String[] stringArray;
    private int position;
    public static final String POSITION_KEY = "position";

    public static PinRecentAddActionDialogFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        PinRecentAddActionDialogFragment fragment = new PinRecentAddActionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

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
                if (stringArray[position].equalsIgnoreCase(getActivity().getString(R.string.setting_shortcut_screen_lock))
                        && (android.os.Build.MANUFACTURER.toLowerCase().contains("samsung") || android.os.Build.MANUFACTURER.toLowerCase().contains("zte"))
                        && Build.VERSION.SDK_INT == Build.VERSION_CODES.M
                        ) {

                    Utility.showTextDialog(getActivity(), R.string.this_feature_does_not_supported_on_samsung_devices);

                } else {
                    int size = (int) myRealm.where(Shortcut.class).count();
                    String item =(String) mAdapter.getItem(position);
                    int action = Utility.getActionFromLabel(getActivity(), item);
                    Shortcut removeShortcut = myRealm.where(Shortcut.class).equalTo("id",PinRecentAddActionDialogFragment.this.position).findFirst();
                    Shortcut newShortcut = new Shortcut();
                    newShortcut.setId(PinRecentAddActionDialogFragment.this.position);
                    newShortcut.setAction(action);
                    newShortcut.setLabel(item);
                    newShortcut.setType(Shortcut.TYPE_ACTION);
                    myRealm.beginTransaction();
                    if (removeShortcut != null) {
                        removeShortcut.deleteFromRealm();
                    }
                    myRealm.copyToRealm(newShortcut);
                    myRealm.commitTransaction();
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
                if (!android.os.Build.MANUFACTURER.toLowerCase().contains("samsung")
                        && !android.os.Build.MANUFACTURER.toLowerCase().contains("zte")
                        && stringArray[position].equalsIgnoreCase(getActivity().getString(R.string.setting_shortcut_screen_lock))
                        || Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
                {
                    Utility.askForAdminPermission(getActivity());
                }

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
}
