package org.de_studio.recentappswitcher.circleFavoriteSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class CircleFavoriteSettingPresenter extends BaseCollectionSettingPresenter {
    private static final String TAG = CircleFavoriteSettingPresenter.class.getSimpleName();

    public CircleFavoriteSettingPresenter(BaseCollectionSettingView view, BaseCollectionSettingModel model) {
        super(view, model);
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_LINEAR, -1));
    }

}
