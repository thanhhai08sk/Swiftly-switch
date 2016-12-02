package org.de_studio.recentappswitcher.folderSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 12/2/16.
 */

public class FolderSettingModel extends BaseModel {
    private static final String TAG = FolderSettingModel.class.getSimpleName();
    protected Realm realm = Realm.getDefaultInstance();
    protected String slotId;
    protected Slot folder;

    public FolderSettingModel(String slotId) {
        this.slotId = slotId;
    }

    public void setup() {
        folder = realm.where(Slot.class).equalTo(Cons.TYPE, Slot.TYPE_FOLDER).equalTo(Cons.SLOT_ID, slotId).findFirst();
        if (folder == null) {
            throw new IllegalArgumentException("Can not find folder");
        }
    }



    @Override
    public void clear() {
        realm.close();
    }
}
