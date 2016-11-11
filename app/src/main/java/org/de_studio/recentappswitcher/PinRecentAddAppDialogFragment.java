package org.de_studio.recentappswitcher;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by HaiNguyen on 7/1/16.
 */
public class PinRecentAddAppDialogFragment extends DialogFragment {

    private static final String TAG = PinRecentAddAppDialogFragment.class.getSimpleName();
    static ListView mListView;
    private ProgressBar progressBar;
    private ArrayList<AppInfors> appInforsArrayList;
    private Realm myRealm;
    public static final String POSITION_KEY = "position";
    PinRecentAddAppAdapter mAdapter;
    private int position;

    public static PinRecentAddAppDialogFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        PinRecentAddAppDialogFragment fragment = new PinRecentAddAppDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_favorite_app_fragment_list_view, container);
        mListView = (ListView) rootView.findViewById(R.id.add_favorite_list_view);
        myRealm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("pinApp.realm")
                .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packageName = appInforsArrayList.get(position).packageName;
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.add_favorite_list_item_check_box);
                int size = (int) myRealm.where(Shortcut.class).count();
                Log.e(TAG, "pinApp count = " + size);
                Shortcut removeShortcut = myRealm.where(Shortcut.class).equalTo("id",PinRecentAddAppDialogFragment.this.position).findFirst();
                Shortcut newShortcut = new Shortcut();
                newShortcut.setId(PinRecentAddAppDialogFragment.this.position);
                newShortcut.setType(Shortcut.TYPE_APP);
                Log.e(TAG, "size = " + size);
                newShortcut.setPackageName(packageName);
                try {
                    newShortcut.setLabel((String) getContext().getPackageManager().getApplicationLabel(getContext().getPackageManager().getApplicationInfo(packageName, 0)));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onItemClick: NameNotFound");
                }
                myRealm.beginTransaction();
                if (removeShortcut != null) {
                    removeShortcut.deleteFromRealm();
                }
                myRealm.copyToRealm(newShortcut);
                myRealm.commitTransaction();
//                if (checkBox != null) {
//                    if (checkBox.isChecked()) {
//                        myRealm.beginTransaction();
//                        Shortcut removeShortcut = myRealm.where(Shortcut.class).equalTo("packageName",packageName).findFirst();
//                        int removeId = removeShortcut.getId();
//                        myRealm.where(Shortcut.class).equalTo("packageName",packageName).findFirst().deleteFromRealm();
//                        RealmResults<Shortcut> results = myRealm.where(Shortcut.class).findAll().sort("id", Sort.ASCENDING);
//
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
//                            newShortcut.setType(Shortcut.TYPE_APP);
//                            Log.e(TAG, "size = " + size);
//                            newShortcut.setPackageName(packageName);
//                            try {
//                                newShortcut.setLabel((String) getContext().getPackageManager().getApplicationLabel(getContext().getPackageManager().getApplicationInfo(packageName, 0)));
//                            } catch (PackageManager.NameNotFoundException e) {
//                                e.printStackTrace();
//                                Log.e(TAG, "onItemClick: NameNotFound");
//                            }
//                            myRealm.beginTransaction();
//                            myRealm.copyToRealm(newShortcut);
//                            myRealm.commitTransaction();
//                        } else {
//                            Toast.makeText(MyApplication.getContext(),getString(R.string.out_of_limit),Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                }

                mAdapter.notifyDataSetChanged();
                dismiss();
            }
        });
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
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
        Utility.stopService(getActivity());;
        position = getArguments().getInt(POSITION_KEY);
        new LoadInstalledApp().execute();
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

    private class LoadInstalledApp extends AsyncTask<Void, Void, ArrayList<AppInfors>> {
        protected ArrayList<AppInfors> doInBackground(Void... voids) {

            PackageManager packageManager;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                packageManager = getContext().getPackageManager();
            } else {
                packageManager = getActivity().getPackageManager();
            }
            ArrayList<AppInfors> arrayList = new ArrayList<AppInfors>();
            Set<PackageInfo> set = Utility.getInstalledApps(getActivity());
            PackageInfo[] array = set.toArray(new PackageInfo[set.size()]);
            for (PackageInfo pack : array) {
                AppInfors appInfors = new AppInfors();
                appInfors.label = (String) packageManager.getApplicationLabel(pack.applicationInfo);
                appInfors.packageName = pack.packageName;
                appInfors.launchIntent = packageManager.getLaunchIntentForPackage(pack.packageName);
                arrayList.add(appInfors);
                Collections.sort(arrayList);

            }
            return arrayList;
        }
        protected void onPostExecute(ArrayList<AppInfors> result) {

            progressBar.setVisibility(View.GONE);
            if (getActivity() != null) {
                appInforsArrayList = result;
                mAdapter = new PinRecentAddAppAdapter(getActivity(),result, myRealm);
                mListView.setAdapter(mAdapter);
            }

        }

    }

    public void setPosition(int position) {
        this.position = position;
    }

}
