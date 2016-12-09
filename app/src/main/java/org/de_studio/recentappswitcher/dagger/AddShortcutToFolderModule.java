package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.base.adapter.ItemsListWithCheckBoxAdapter;
import org.de_studio.recentappswitcher.base.adapter.ShortcutListAdapter;
import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderPresenter;
import org.de_studio.recentappswitcher.folderSetting.addShortcutToFolder.AddShortcutToFolderPresenter;
import org.de_studio.recentappswitcher.folderSetting.addShortcutToFolder.AddShortcutToFolderView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 12/9/16.
 */
@Module
public class AddShortcutToFolderModule {
    AddShortcutToFolderView view;
    String slotId;

    public AddShortcutToFolderModule(AddShortcutToFolderView view, String slotId) {
        this.view = view;
        this.slotId = slotId;
    }

    @Provides
    @Singleton
    BaseAddItemsToFolderPresenter presenter() {
        return new AddShortcutToFolderPresenter(slotId);
    }
    @Provides
    @Singleton
    ShortcutListAdapter adapter() {
        return new ShortcutListAdapter(view.getActivity(), null);
    }

    @Provides
    @Singleton
    ItemsListWithCheckBoxAdapter fakeAdapter(@Nullable IconPackManager.IconPack iconPack){
        return new ItemsListWithCheckBoxAdapter(view.getActivity(), null, view.getActivity().getPackageManager(), iconPack, null);
    }

}
