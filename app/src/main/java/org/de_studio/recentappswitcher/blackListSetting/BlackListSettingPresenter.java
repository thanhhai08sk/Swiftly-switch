package org.de_studio.recentappswitcher.blackListSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class BlackListSettingPresenter extends BaseCollectionSettingPresenter<BlackListSettingPresenter.View, BlackListSettingModel> {
    public BlackListSettingPresenter(BlackListSettingModel model) {
        super(model);
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_LINEAR, -1),null);
    }

    public interface View extends BaseCollectionSettingPresenter.View {

    }
}
