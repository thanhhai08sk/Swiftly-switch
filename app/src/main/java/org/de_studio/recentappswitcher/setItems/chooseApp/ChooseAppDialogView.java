package org.de_studio.recentappswitcher.setItems.chooseApp;

import android.content.pm.PackageManager;

import org.de_studio.recentappswitcher.base.BaseChooseItemDialogView;

import java.lang.ref.WeakReference;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class ChooseAppDialogView extends BaseChooseItemDialogView {



    @Override
    public void loadItems() {
        ChooseAppFragmentView.LoadAppsTask task = new ChooseAppFragmentView.LoadAppsTask(new WeakReference<PackageManager>(getActivity().getPackageManager()));
        task.execute();
    }

    @Override
    protected void inject() {
    }
}
