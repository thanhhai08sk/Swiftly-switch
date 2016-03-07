package org.de_studio.recentappswitcher;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.HashMap;

/**
 * Created by hai on 3/3/2016.
 */
public class IconPackSettingDialogFragment extends DialogFragment {
    private static final String LOG_TAG = IconPackSettingDialogFragment.class.getSimpleName();
    private ProgressBar progressBar;
    static ListView mListView;
    private IconPackListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_icon_pack, container);
        mListView = (ListView) rootView.findViewById(R.id.icon_pack_list_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        IconPackManager manager = new IconPackManager();
        manager.setContext(getActivity());
        HashMap<String, IconPackManager.IconPack> hashMap = manager.getAvailableIconPacks(true);
        mAdapter = new IconPackListAdapter(getActivity(), hashMap);
        mListView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().stopService(new Intent(getActivity(), EdgeGestureService.class));
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}