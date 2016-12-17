package org.de_studio.recentappswitcher.blackListSetting;

import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;
import org.de_studio.recentappswitcher.model.Collection;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class BlackListSettingModel extends BaseCollectionSettingModel {
    private static final String TAG = BlackListSettingModel.class.getSimpleName();
    public BlackListSettingModel(String defaultLabel, String collectionId) {
        super(defaultLabel, collectionId);
    }

    @Override
    public String getCollectionType() {
        return Collection.TYPE_BLACK_LIST;
    }

    @Override
    public String createNewCollection() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection collection = new Collection();
                collection.type = getCollectionType();
                collection.collectionId = Utility.createCollectionId(getCollectionType(), 1);
                collection.label = defaultLabel;
                realm.copyToRealm(collection);
            }
        });
        return defaultLabel;
    }
}
