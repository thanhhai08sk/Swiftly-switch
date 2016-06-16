package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.de_studio.recentappswitcher.R;

import java.util.List;

/**
 * Created by HaiNguyen on 6/16/16.
 */
public class ShortcutTabFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String TAG = ShortcutTabFragment.class.getSimpleName();
    private ListView mListView;
    private ShortcutListAdapter mAdapter;
    private int mode;


    public static ShortcutTabFragment newInstance(int sectionNumber) {
        ShortcutTabFragment fragment = new ShortcutTabFragment();
        Bundle agument = new Bundle();
        agument.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(agument);
        return fragment;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_tab, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_app_tab_list_view);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


//        ((ChooseShortcutActivity)getActivity()).setSettingAdapter(mAdapter);
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);

        final PackageManager packageManager=getActivity().getPackageManager();
        List<ResolveInfo> resolveInfos =  packageManager.queryIntentActivities(shortcutsIntent, 0);


        mAdapter = new ShortcutListAdapter(getContext(), mode, resolveInfos);
        mListView.setAdapter(mAdapter);
        return view;
    }

    public ShortcutListAdapter getAdapter() {
        return mAdapter;
    }
}
