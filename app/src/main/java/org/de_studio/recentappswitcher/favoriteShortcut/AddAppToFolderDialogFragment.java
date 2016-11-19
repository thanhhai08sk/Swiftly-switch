package org.de_studio.recentappswitcher.favoriteShortcut;

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
import android.widget.Toast;

import org.de_studio.recentappswitcher.AppInfors;
import org.de_studio.recentappswitcher.MyApplication;
import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by HaiNguyen on 5/31/16.
 */
public class AddAppToFolderDialogFragment  extends DialogFragment{
    private static final String LOG_TAG = AddAppToFolderDialogFragment.class.getSimpleName();
    static ListView mListView;
    private ProgressBar progressBar;
    private ArrayList<AppInfors> appInforsArrayList;
    private Realm myRealm;
    private int mPosition;
    AddAppToFolderAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_favorite_app_fragment_list_view, container);
        mListView = (ListView) rootView.findViewById(R.id.add_favorite_list_view);
        myRealm = Realm.getInstance(new RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(EdgeGestureService. CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());
        final int startId = (mPosition +1)*1000;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packageName = appInforsArrayList.get(position).packageName;
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.add_favorite_list_item_check_box);
                int size = (int) myRealm.where(Shortcut.class).greaterThan("id",startId -1).lessThan("id",startId + 1000).count();
                Log.e(LOG_TAG, "pinApp count = " + size);
                if (checkBox != null) {
                    if (checkBox.isChecked()) {
                        myRealm.beginTransaction();
                        Shortcut removeShortcut = myRealm.where(Shortcut.class).greaterThan("id",startId -1).lessThan("id", startId + 1000).equalTo("type", Shortcut.TYPE_APP) .equalTo("packageName",packageName).findFirst();
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
                            newShortcut.setPackageName(packageName);
                            newShortcut.setType(Shortcut.TYPE_APP);
                            try {
                                newShortcut.setLabel((String) getActivity().getPackageManager().getApplicationLabel(getActivity().getPackageManager().getApplicationInfo(packageName, 0)));
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
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
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
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
        new LoadInstalledApp().execute();
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
        ((MyDialogCloseListener) getActivity()).handleDialogClose();
    }

    @Override
    public void onDestroy() {
        if (mAdapter != null) {
            mAdapter.clear();
        }
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
            Set<PackageInfo> set = Utility.getInstalledApps(getActivity().getPackageManager());
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
                mAdapter = new AddAppToFolderAdapter(getActivity(),result, myRealm, mPosition);
                mListView.setAdapter(mAdapter);
            }

        }
    }
    public interface MyDialogCloseListener
    {
        public void handleDialogClose();//or whatever args you want
    }
}
