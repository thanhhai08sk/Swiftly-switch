package org.de_studio.recentappswitcher.folderSetting.addAppToFolder;

import android.content.pm.PackageManager;

import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderView;
import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppView;

import java.lang.ref.WeakReference;

/**
 * Created by HaiNguyen on 12/3/16.
 */

public class AddAppToFolderView extends BaseAddItemsToFolderView {

    @Override
    public void loadItems() {
        ChooseAppView.LoadAppsTask task = new ChooseAppView.LoadAppsTask(new WeakReference<PackageManager>(getActivity().getPackageManager()));
        task.execute();
    }


    @Override
    protected void inject() {

    }
}
