package org.de_studio.recentappswitcher.folderSetting.addContactToFolder;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.base.addItemsToFolder.BaseAddItemsToFolderPresenter;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 12/9/16.
 */

public class AddContactToFolderPresenter extends BaseAddItemsToFolderPresenter {
    public AddContactToFolderPresenter(BaseModel model, String folderId) {
        super(model, folderId);
    }

    @Override
    protected RealmResults<Item> getItemRealmResult() {
        return realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_CONTACT).findAllSortedAsync(Cons.LABEL);
    }
}
