package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.base.adapter.ItemsRealmAdapter;
import org.de_studio.recentappswitcher.folderSetting.FolderSettingModel;
import org.de_studio.recentappswitcher.folderSetting.FolderSettingPresenter;
import org.de_studio.recentappswitcher.folderSetting.FolderSettingView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 12/3/16.
 */
@Module
public class FolderSettingModule {
    FolderSettingView view;
    String folderId;

    public FolderSettingModule(FolderSettingView view, String folderId) {
        this.view = view;
        this.folderId = folderId;
    }

    @Provides
    @Singleton
    FolderSettingPresenter presenter(FolderSettingModel model){
        return new FolderSettingPresenter(model);
    }

    @Provides
    @Singleton
    FolderSettingModel model() {
        return new FolderSettingModel(folderId);
    }

    @Provides
    @Singleton
    ItemsRealmAdapter adapter(@Nullable IconPackManager.IconPack iconPack) {
        return new ItemsRealmAdapter(view, null, true, view.getPackageManager(), iconPack, Cons.ITEM_TYPE_ICON_LABEL);
    }


}
