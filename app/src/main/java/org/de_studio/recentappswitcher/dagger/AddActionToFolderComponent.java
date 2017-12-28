package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.folderSetting.addActionToFolder.AddActionToFolderView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/9/16.
 */
@Singleton
@Component(modules = {AppModule.class, AddActionToFolderModule.class})
public interface AddActionToFolderComponent {
    void inject(AddActionToFolderView view);

}
