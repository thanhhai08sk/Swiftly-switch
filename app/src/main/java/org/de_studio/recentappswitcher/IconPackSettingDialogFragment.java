package org.de_studio.recentappswitcher;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

import java.util.HashMap;

/**
 * Created by hai on 3/3/2016.
 */
public class IconPackSettingDialogFragment extends DialogFragment {
    private static final String LOG_TAG = IconPackSettingDialogFragment.class.getSimpleName();
    static ListView mListView;
    private IconPackListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_icon_pack, container);
        mListView = (ListView) rootView.findViewById(R.id.icon_pack_list_view);
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
        Utility.stopService(getActivity());
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
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout((int)getResources().getDimension(R.dimen.icon_pack_dialog_width), (int)getResources().getDimension(R.dimen.icon_pack_dialog_heigh));
        window.setGravity(Gravity.CENTER);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
