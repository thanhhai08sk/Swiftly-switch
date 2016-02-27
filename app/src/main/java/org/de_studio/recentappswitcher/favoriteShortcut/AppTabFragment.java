package org.de_studio.recentappswitcher.favoriteShortcut;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.de_studio.recentappswitcher.AppInfors;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Created by hai on 2/23/2016.
 */

public class AppTabFragment extends Fragment{
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String LOG_TAG = AppTabFragment.class.getSimpleName();
    private ListView mListView;
    private ChooseAppListViewAdapter mAdapter;
    private int mPosition;

    public static AppTabFragment newInstance(int sectionNumber) {
        AppTabFragment fragment = new AppTabFragment();
        Bundle agument = new Bundle();
        agument.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(agument);
        return fragment;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public void setmPositioinToNext() {
        if (mPosition < Utility.getSizeOfFavoriteGrid(getContext())-1) {
            mPosition++;
            mAdapter.setmPosition(mPosition);
        }
    }

    public void setmPositionToBack() {
        if (mPosition > 0) {
            mPosition--;
            mAdapter.setmPosition(mPosition);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_tab, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_app_tab_list_view);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        Log.e(LOG_TAG, "inflate mListView");
//        Toast.makeText(getContext(), "AppTabFragment mPosition = " + mPosition, Toast.LENGTH_SHORT).show();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadInstalledApp().execute();
    }

    private class LoadInstalledApp extends AsyncTask<Void, Void, ArrayList<AppInfors>> {
        protected ArrayList<AppInfors> doInBackground(Void... voids) {
            Float mScale = getResources().getDisplayMetrics().density;
            PackageManager packageManager = getActivity().getPackageManager();
            ArrayList<AppInfors> arrayList = new ArrayList<AppInfors>();
            Set<PackageInfo> set = Utility.getInstalledApps(getContext());
            PackageInfo[] array = set.toArray(new PackageInfo[set.size()]);
            for (PackageInfo pack : array){

                try {
                    AppInfors appInfors = new AppInfors();
                    appInfors.label =(String) packageManager.getApplicationLabel(pack.applicationInfo);
                    appInfors.packageName = pack.packageName;
//                    appInfors.iconDrawable = packageManager.getApplicationIcon(pack.packageName);
                    appInfors.launchIntent = packageManager.getLaunchIntentForPackage(pack.packageName);
                    Bitmap iconBitmap = Utility.drawableToBitmap(packageManager.getApplicationIcon(pack.packageName));
                    appInfors.iconBitmap = Bitmap.createScaledBitmap(iconBitmap,(int)(24*mScale),(int)(24*mScale),false);
                    arrayList.add(appInfors);
                }catch (PackageManager.NameNotFoundException e){
                    Log.e(LOG_TAG, "name not found " + e);
                }
                Collections.sort(arrayList);

            }
            return arrayList;
        }
        protected void onPostExecute(ArrayList<AppInfors> result) {
            mAdapter = new ChooseAppListViewAdapter(getContext(), result, mPosition);
            mListView.setAdapter(mAdapter);
            ((ChooseShortcutActivity) getActivity()).setAppAdapter(mAdapter);
            if (mListView == null) {
                Log.e(LOG_TAG, "mListView = null");
            }
//            synchronized(mListView){
//                mListView.notify();
//            }

            Log.e(LOG_TAG, "OnPostExecute, mListview size = " + mListView.getCount());

        }
    }

}
