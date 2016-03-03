package org.de_studio.recentappswitcher;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.HashMap;

/**
 * Created by hai on 3/3/2016.
 */
public class IconPackSettingDialogFragment extends DialogFragment {
    private static final String LOG_TAG = IconPackSettingDialogFragment.class.getSimpleName();
    private ProgressBar progressBar;
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_icon_pack, container);
        mListView = (ListView) rootView.findViewById(R.id.icon_pack_list_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        IconPackManager manager = new IconPackManager();
        HashMap<String, IconPackManager.IconPack> hashMap = manager.getAvailableIconPacks(true);


        return rootView;
    }

}
