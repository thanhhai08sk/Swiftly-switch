package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.gridFavoriteSetting.GridFavoriteSettingModel;
import org.de_studio.recentappswitcher.gridFavoriteSetting.GridFavoriteSettingPresenter;
import org.de_studio.recentappswitcher.gridFavoriteSetting.GridFavoriteSettingView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 11/26/16.
 */
@Singleton
@Module
public class GridFavoriteSettingModule {
    GridFavoriteSettingView view;
    String collectionId;

    public GridFavoriteSettingModule(GridFavoriteSettingView view, String collectionId) {
        this.view = view;
        this.collectionId = collectionId;
    }

    @Provides
    @Singleton
    GridFavoriteSettingPresenter presenter(GridFavoriteSettingModel model) {
        return new GridFavoriteSettingPresenter(model);
    }

    @Provides
    @Singleton
    GridFavoriteSettingModel model() {
        return new GridFavoriteSettingModel(view.getString(R.string.grid_favorites), collectionId);
    }


    @Provides
    @Singleton
    SlotsAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new SlotsAdapter(view, null, false, iconPack, Cons.ITEM_TYPE_ICON_ONLY);
    }

}
