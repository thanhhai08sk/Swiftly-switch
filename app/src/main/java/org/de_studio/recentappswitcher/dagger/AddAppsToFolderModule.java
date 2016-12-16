package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.base.adapter.ItemsListWithCheckBoxAdapter;
import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderPresenter;
import org.de_studio.recentappswitcher.folderSetting.addAppToFolder.AddAppToFolderPresenter;
import org.de_studio.recentappswitcher.folderSetting.addAppToFolder.AddAppToFolderView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 12/3/16.
 */
@Module
public class AddAppsToFolderModule {
    AddAppToFolderView view;
    String slotId;

    public AddAppsToFolderModule(AddAppToFolderView view, String slotId) {
        this.view = view;
        this.slotId = slotId;
    }

    @Provides
    @Singleton
    BaseAddItemsToFolderPresenter presenter(){
        return new AddAppToFolderPresenter(null,slotId);
    }

    @Provides
    @Singleton
    ItemsListWithCheckBoxAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new ItemsListWithCheckBoxAdapter(view.getActivity(), null, view.getActivity().getPackageManager(), iconPack, null);
    }
}
