package org.de_studio.recentappswitcher;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by hai on 1/5/2016.
 */
public class AddFavoriteAppsDialogFragment extends DialogFragment {
    public static final String APP_INFORS_KEY = "app_infors";
    static ListView mListView;
    static Set<PackageInfo> mInfos;
    static AppsListArrayAdapter adapter;
    static private ArrayList<AppInfors> appInforsArrayList;

    public void setAppInforsArrayList(ArrayList<AppInfors> set){
        appInforsArrayList = set;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInfos = Utility.getInstalledApps(getContext());
        adapter = new AppsListArrayAdapter(getContext(),appInforsArrayList);
        View rootView = inflater.inflate(R.layout.add_favorite_app_fragment_list_view,container);
        mListView = (ListView) rootView.findViewById(R.id.add_favorite_list_view);
        mListView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
