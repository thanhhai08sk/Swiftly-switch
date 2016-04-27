package org.de_studio.recentappswitcher.favoriteShortcut;


import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.AppInfors;
import org.de_studio.recentappswitcher.MyApplication;
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
    private ProgressBar progressBar;
    private Context mContext;

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

    public void setmContext(Context context) {
        mContext = context;
    }

    public void setmPositioinToNext() {
        if (mPosition < Utility.getSizeOfFavoriteGrid(getContext())-1 &&  mAdapter !=null) {
            mPosition++;
            try {
                mAdapter.setmPosition(mPosition);
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "mAdapter = null");
            }

        }
    }

    public void setmPositionToBack() {
        if (mPosition > 0 && mAdapter != null) {
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
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
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
            PackageManager packageManager = getActivity().getPackageManager();
            ArrayList<AppInfors> arrayList = new ArrayList<AppInfors>();
            Set<PackageInfo> set = Utility.getInstalledApps(getContext());
            PackageInfo[] array = set.toArray(new PackageInfo[set.size()]);
            for (PackageInfo pack : array){

//                try {
                    AppInfors appInfors = new AppInfors();
                    appInfors.label =(String) packageManager.getApplicationLabel(pack.applicationInfo);
                    appInfors.packageName = pack.packageName;
//                    appInfors.iconDrawable = packageManager.getApplicationIcon(pack.packageName);
//                    appInfors.launchIntent = packageManager.getLaunchIntentForPackage(pack.packageName);
                    arrayList.add(appInfors);
//                }catch (PackageManager.NameNotFoundException e){
//                    Log.e(LOG_TAG, "name not found " + e);
//                }
                Collections.sort(arrayList);

            }
            return arrayList;
        }
        protected void onPostExecute(ArrayList<AppInfors> result) {
            progressBar.setVisibility(View.GONE);
            Context context;
            Application application = (Application)MyApplication.getContext();
            MyApplication app = (MyApplication)application;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                context = getContext();
                Log.e(LOG_TAG, "getContext");
            } else {
                Log.e(LOG_TAG, "getActivity");
                context = getActivity();
            }
            mAdapter = new ChooseAppListViewAdapter(app, result, mPosition);
            mListView.setAdapter(mAdapter);
            try {
                ((ChooseShortcutActivity) mContext).setAppAdapter(mAdapter);
            } catch (NullPointerException e) {
                try {
                    ((ChooseShortcutActivity) context).setAppAdapter(mAdapter);
                } catch (NullPointerException e1) {
                    Log.e(LOG_TAG, "null context");
                }

            }

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
