package org.de_studio.recentappswitcher.quickActionSetting;

import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;

/**
 * Created by HaiNguyen on 12/10/16.
 */

public class QuickActionSettingPresenter extends BaseCollectionSettingPresenter<QuickActionSettingPresenter.View, QuickActionSettingModel> {

    public QuickActionSettingPresenter(QuickActionSettingModel model) {
        super(model);
    }

    @Override
    public void setRecyclerView() {

    }

    public interface View extends BaseCollectionSettingPresenter.View {
    }

}
