package org.de_studio.recentappswitcher.folderSetting.addAppToFolder;

import android.content.pm.PackageManager;
import android.os.Bundle;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderView;
import org.de_studio.recentappswitcher.dagger.AddAppsToFolderModule;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerAddAppsToFolderComponent;
import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppFragmentView;

import java.lang.ref.WeakReference;

/**
 * Created by HaiNguyen on 12/3/16.
 */

public class AddAppToFolderView extends BaseAddItemsToFolderView {

    public static AddAppToFolderView newInstance(String folderId) {

        Bundle args = new Bundle();
        args.putString(Cons.SLOT_ID, folderId);
        AddAppToFolderView fragment = new AddAppToFolderView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void loadItems() {
        ChooseAppFragmentView.LoadAppsTask task = new ChooseAppFragmentView.LoadAppsTask(new WeakReference<PackageManager>(getActivity().getPackageManager()));
        task.execute();
    }


    @Override
    protected void inject() {
        DaggerAddAppsToFolderComponent.builder()
                .appModule(new AppModule(getActivity()))
                .addAppsToFolderModule(new AddAppsToFolderModule(this,slotId))
                .build().inject(this);
    }
}
