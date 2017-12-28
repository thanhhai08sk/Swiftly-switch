package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.folderSetting.addContactToFolder.AddContactToFolderView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/9/16.
 */
@Singleton
@Component(modules = {AppModule.class, AddContactToFolderModule.class})
public interface AddContactToFolderComponent {
    void inject(AddContactToFolderView view);
}
