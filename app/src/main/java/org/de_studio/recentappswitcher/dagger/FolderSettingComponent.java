package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.folderSetting.FolderSettingView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/3/16.
 */
@Singleton
@Component(modules = {AppModule.class, FolderSettingModule.class})
public interface FolderSettingComponent {
    void inject(FolderSettingView view);
}
