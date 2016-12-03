package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.folderSetting.addAppToFolder.AddAppToFolderView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/3/16.
 */
@Singleton
@Component(modules = {AppModule.class, AddAppsToFolderModule.class})
public interface AddAppsToFolderComponent {
    void inject(AddAppToFolderView view);
}
