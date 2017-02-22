package org.de_studio.recentappswitcher;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by hai on 3/26/2016.
 */
public class PinAppDialogFragment extends DialogFragment {
    private static final String LOG_TAG = PinAppDialogFragment.class.getSimpleName();
    public static final String APP_INFORS_KEY = "app_infors";
    public static final int FAVORITE_MODE = 1;
    public static final int EXCLUDE_MODE = 2;
    static ListView mListView;
    private ProgressBar progressBar;
    private ArrayList<AppInfors> appInforsArrayList;
    private Realm pinRealm;
    InstallAppPinAppAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_favorite_app_fragment_list_view, container);
        mListView = (ListView) rootView.findViewById(R.id.add_favorite_list_view);
        pinRealm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("pinApp.realm")
                .schemaVersion(Cons.OLD_REALM_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packageName = appInforsArrayList.get(position).packageName;
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.add_favorite_list_item_check_box);
                int size = (int)pinRealm.where(Shortcut.class).count();
                Log.e(LOG_TAG, "pinApp count = " + size);
                if (checkBox != null) {
                    if (checkBox.isChecked()) {
                        pinRealm.beginTransaction();
                        Shortcut removeShortcut = pinRealm.where(Shortcut.class).equalTo("packageName",packageName).findFirst();
                        int removeId = removeShortcut.getId();
                        pinRealm.where(Shortcut.class).equalTo("packageName",packageName).findFirst().deleteFromRealm();
                        RealmResults<Shortcut> results = pinRealm.where(Shortcut.class).findAll().sort("id", Sort.ASCENDING);

                        for (int i = 0; i < results.size(); i++) {
                            Log.e(LOG_TAG, "id = " + results.get(i).getId());
                            if (results.get(i).getId() >= removeId) {
                                Log.e(LOG_TAG, "when i = " + i + "result id = " + results.get(i).getId());
                                Shortcut shortcut = results.get(i);
                                int oldId = shortcut.getId();
                                shortcut.setId(oldId - 1);
                            }
                        }
                        pinRealm.commitTransaction();
                    } else {
                        if (size < 6) {
                            Shortcut newShortcut = new Shortcut();
                            newShortcut.setId(size);
                            Log.e(LOG_TAG, "size = " + size);
                            newShortcut.setPackageName(packageName);
                            pinRealm.beginTransaction();
                            pinRealm.copyToRealm(newShortcut);
                            pinRealm.commitTransaction();
                        } else {
                            Toast.makeText(MyApplication.getContext(),getString(R.string.out_of_limit),Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                mAdapter.notifyDataSetChanged();
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
        Utility.stopService(getActivity());
        new LoadInstalledApp().execute();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            Utility.startService(getActivity());
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Null when get activity from on dismiss");
        }

        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        if (pinRealm != null) {
            pinRealm.close();
        }
        if (mAdapter != null) {
            mAdapter.clear();
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
            Set<PackageInfo> set = Utility.getInstalledApps(getActivity().getPackageManager());
            PackageInfo[] array = set.toArray(new PackageInfo[set.size()]);
            for (PackageInfo pack : array) {

//                try {
                AppInfors appInfors = new AppInfors();
                appInfors.label = (String) packageManager.getApplicationLabel(pack.applicationInfo);
                appInfors.packageName = pack.packageName;
//                    appInfors.iconDrawable = packageManager.getApplicationIcon(pack.packageName);
                appInfors.launchIntent = packageManager.getLaunchIntentForPackage(pack.packageName);
//                    Bitmap iconBitmap = Utility.drawableToBitmap(packageManager.getApplicationIcon(pack.packageName));
//                    appInfors.iconBitmap = Bitmap.createScaledBitmap(iconBitmap, (int) (24 * mScale), (int) (24 * mScale), false);
                arrayList.add(appInfors);
//                } catch (PackageManager.NameNotFoundException e) {
//                    Log.e(LOG_TAG, "name not found " + e);
//                }
                Collections.sort(arrayList);

            }
            return arrayList;
        }
        protected void onPostExecute(ArrayList<AppInfors> result) {

            progressBar.setVisibility(View.GONE);
            if (getActivity() != null) {
                appInforsArrayList = result;
                mAdapter = new InstallAppPinAppAdapter(getActivity(),result,pinRealm);
                mListView.setAdapter(mAdapter);
            }

        }
    }
}
