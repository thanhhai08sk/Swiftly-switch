package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.folderSetting.addShortcutToFolder.AddShortcutToFolderView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/9/16.
 */
@Singleton
@Component(modules = {AppModule.class, AddShortcutToFolderModule.class})
public interface AddShortcutToFolderComponent {
    void inject(AddShortcutToFolderView view);
}
