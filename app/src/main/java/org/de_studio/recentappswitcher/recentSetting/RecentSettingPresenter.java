package org.de_studio.recentappswitcher.recentSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class RecentSettingPresenter extends BaseCollectionSettingPresenter<RecentSettingPresenter.View, RecentSettingModel> {

    public RecentSettingPresenter(RecentSettingModel model) {
        super(model);
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_LINEAR, -1),null);
    }



    public interface View extends BaseCollectionSettingPresenter.View {
    }

}
