package org.de_studio.recentappswitcher.quickActionSetting;

import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;

/**
 * Created by HaiNguyen on 12/10/16.
 */

public class QuickActionSettingModel extends BaseCollectionSettingModel {
    public QuickActionSettingModel(String defaultLabel, String collectionId) {
        super(defaultLabel, collectionId);
    }

    @Override
    public String getCollectionType() {
        return null;
    }

    @Override
    public String createNewCollection() {
        return null;
    }
}
