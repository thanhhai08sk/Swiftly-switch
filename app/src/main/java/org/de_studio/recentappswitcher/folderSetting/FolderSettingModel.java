package org.de_studio.recentappswitcher.folderSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by HaiNguyen on 12/2/16.
 */

public class FolderSettingModel extends BaseModel {
    private static final String TAG = FolderSettingModel.class.getSimpleName();
    private Realm realm = Realm.getDefaultInstance();
    private String folderId;
    private Slot folder;

    public FolderSettingModel(String folderId) {
        this.folderId = folderId;
    }

    public void setup() {
        folder = realm.where(Slot.class).equalTo(Cons.TYPE, Slot.TYPE_FOLDER).equalTo(Cons.SLOT_ID, folderId).findFirst();
        if (folder == null) {
            throw new IllegalArgumentException("Can not find folder");
        }
    }

    public RealmList<Item> getFolderItems() {
        return folder.items;
    }

    public void moveItem(int from, int to) {
        realm.beginTransaction();
        folder.items.move(from,to);
        realm.commitTransaction();
    }

    public void removeItem(int position) {
        realm.beginTransaction();
        folder.items.remove(position);
        realm.commitTransaction();
    }



    @Override
    public void clear() {
        realm.close();
    }
}
