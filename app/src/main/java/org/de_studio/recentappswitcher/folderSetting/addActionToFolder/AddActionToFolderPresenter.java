package org.de_studio.recentappswitcher.folderSetting.addActionToFolder;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderPresenter;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 12/9/16.
 */

public class AddActionToFolderPresenter extends BaseAddItemsToFolderPresenter {
    public AddActionToFolderPresenter(String folderId) {
        super(folderId);
    }

    @Override
    protected RealmResults<Item> getItemRealmResult() {
        return realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_ACTION).findAllAsync();
    }
}
