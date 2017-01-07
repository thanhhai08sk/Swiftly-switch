package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.circleFavoriteSetting.CircleFavoriteSettingModel;
import org.de_studio.recentappswitcher.circleFavoriteSetting.CircleFavoriteSettingPresenter;
import org.de_studio.recentappswitcher.circleFavoriteSetting.CircleFavoriteSettingView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 11/12/16.
 */
@Module
public class CircleFavoriteSettingModule {
    CircleFavoriteSettingView view;
    String collectionId;

    public CircleFavoriteSettingModule(CircleFavoriteSettingView view, String collectionId) {
        this.view = view;
        this.collectionId = collectionId;
    }

    @Provides
    @Singleton
    CircleFavoriteSettingPresenter presenter(CircleFavoriteSettingModel model) {
        return new CircleFavoriteSettingPresenter(model);
    }

    @Provides
    @Singleton
    CircleFavoriteSettingModel model() {
        return new CircleFavoriteSettingModel(view.getString(R.string.circle_favorites), collectionId);
    }


    @Provides
    @Singleton
    SlotsAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new SlotsAdapter(view, null, false, iconPack, Cons.ITEM_TYPE_ICON_LABEL);
    }




}
