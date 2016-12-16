package org.de_studio.recentappswitcher.folderSetting.addActionToFolder;

import android.content.Context;
import android.os.Bundle;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderView;
import org.de_studio.recentappswitcher.dagger.AddActionToFolderModule;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerAddActionToFolderComponent;
import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionView;

import java.lang.ref.WeakReference;

/**
 * Created by HaiNguyen on 12/9/16.
 */

public class AddActionToFolderView extends BaseAddItemsToFolderView {
    public static AddActionToFolderView newInstance(String folderId) {

        Bundle args = new Bundle();
        args.putString(Cons.SLOT_ID, folderId);
        AddActionToFolderView fragment = new AddActionToFolderView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void loadItems() {
        ChooseActionView.LoadActionsTask task = new ChooseActionView.LoadActionsTask(new WeakReference<Context>(getActivity()),null);
        task.execute();
    }


    @Override
    protected void inject() {
        DaggerAddActionToFolderComponent.builder()
                .appModule(new AppModule(getActivity()))
                .addActionToFolderModule(new AddActionToFolderModule(this, slotId))
                .build().inject(this);
    }
}
