package org.de_studio.recentappswitcher.main;

import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.DataInfo;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class MainModel extends BaseModel {
    Realm realm = Realm.getDefaultInstance();

    public boolean checkIfDataSetupOk() {
        DataInfo dataInfo = realm.where(DataInfo.class).findFirst();

        return dataInfo != null && dataInfo.everyThingsOk();
    }
    public void clear() {
        realm.close();
    }
}
