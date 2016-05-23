package org.de_studio.recentappswitcher.favoriteShortcut;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

/**
 * Created by hai on 2/25/2016.
 */
public class SettingTabFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String LOG_TAG = SettingTabFragment.class.getSimpleName();
    private ListView mListView;
    private ChooseSettingShortcutListViewAdapter mAdapter;
    private int mPosition, mode;


    public static SettingTabFragment newInstance(int sectionNumber) {
        SettingTabFragment fragment = new SettingTabFragment();
        Bundle agument = new Bundle();
        agument.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(agument);
        return fragment;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setmPositioinToNext() {
        if (mPosition < Utility.getSizeOfFavoriteGrid(getContext())-1) {
            mPosition++;
            mAdapter.setmPositionAndMode(mPosition);
        }
    }

    public void setmPositionToBack() {
        if (mPosition > 0) {
            mPosition--;
            mAdapter.setmPositionAndMode(mPosition);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_tab, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_app_tab_list_view);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mAdapter = new ChooseSettingShortcutListViewAdapter(getContext(),mPosition, mode);
        mListView.setAdapter(mAdapter);
        ((ChooseShortcutActivity)getActivity()).setSettingAdapter(mAdapter);
        Log.e(LOG_TAG, "inflate mListView");
//        Toast.makeText(getContext(), "AppTabFragment mPosition = " + mPosition, Toast.LENGTH_SHORT).show();
        return view;
    }

    public ChooseSettingShortcutListViewAdapter getAdapter() {
        return mAdapter;
    }


}
