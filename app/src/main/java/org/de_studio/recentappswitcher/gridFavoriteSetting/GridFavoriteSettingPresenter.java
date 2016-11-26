package org.de_studio.recentappswitcher.gridFavoriteSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public class GridFavoriteSettingPresenter extends BaseCollectionSettingPresenter {
    private static final String TAG = GridFavoriteSettingPresenter.class.getSimpleName();
    public GridFavoriteSettingPresenter(BaseCollectionSettingView view, BaseCollectionSettingModel model) {
        super(view, model);
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_GRID, Cons.DEFAULT_FAVORITE_GRID_COLUMN_COUNT));
    }
}
