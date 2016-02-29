package org.de_studio.recentappswitcher;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.service.EdgeGestureService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Created by hai on 1/5/2016.
 */
public class FavoriteOrExcludeDialogFragment extends DialogFragment {
    private static final String LOG_TAG = FavoriteOrExcludeDialogFragment.class.getSimpleName();
    public static final String APP_INFORS_KEY = "app_infors";
    public static final int FAVORITE_MODE = 1;
    public static final int EXCLUDE_MODE = 2;
    static ListView mListView;
    private ProgressBar progressBar;
    AppsListArrayAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_favorite_app_fragment_list_view,container);
        mListView = (ListView) rootView.findViewById(R.id.add_favorite_list_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getContext().stopService(new Intent(getContext(), EdgeGestureService.class));
        getActivity().stopService(new Intent(getActivity(), EdgeGestureService.class));
        new LoadInstalledApp().execute();


    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            getActivity().startService(new Intent(getActivity(), EdgeGestureService.class));
        } catch (NullPointerException e) {
            Log.e(LOG_TAG, "Null when get activity from on dismiss");
        }

        super.onDismiss(dialog);
    }

    private class LoadInstalledApp extends AsyncTask<Void, Void, ArrayList<AppInfors>> {
        protected ArrayList<AppInfors> doInBackground(Void... voids) {
            PackageManager packageManager = getActivity().getPackageManager();
            ArrayList<AppInfors> arrayList = new ArrayList<AppInfors>();
            Set<PackageInfo> set = Utility.getInstalledApps(getActivity());
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
                mAdapter = new AppsListArrayAdapter(getActivity(),result,EXCLUDE_MODE);
                mListView.setAdapter(mAdapter);
            }



        }
    }

}
