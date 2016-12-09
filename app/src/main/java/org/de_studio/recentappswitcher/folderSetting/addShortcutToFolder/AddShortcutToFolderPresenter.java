package org.de_studio.recentappswitcher.folderSetting.addShortcutToFolder;

import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderPresenter;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 12/9/16.
 */

public class AddShortcutToFolderPresenter extends BaseAddItemsToFolderPresenter {

    public AddShortcutToFolderPresenter(String folderId) {
        super(folderId);
    }

    @Override
    protected RealmResults<Item> getItemRealmResult() {
        return null;
    }
}
