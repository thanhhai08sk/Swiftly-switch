package org.de_studio.recentappswitcher.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.de_studio.recentappswitcher.R;

/**
 * Created by hai on 1/2/2016.
 */
public class EdgeSettingDialogFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edge_setting_dialog,container,false);

    }
}
