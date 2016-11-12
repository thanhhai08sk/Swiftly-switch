package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.setCircleFavorite.SetCircleFavoriteModel;
import org.de_studio.recentappswitcher.setCircleFavorite.SetCircleFavoritePresenter;
import org.de_studio.recentappswitcher.setCircleFavorite.SetCircleFavoriteView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 11/12/16.
 */
@Module
@Singleton
public class SetCircleFavoriteModule {
    SetCircleFavoriteView view;
    String collectionId;

    public SetCircleFavoriteModule(SetCircleFavoriteView view, String collectionId) {
        this.view = view;
        this.collectionId = collectionId;
    }

    @Provides
    @Singleton
    SetCircleFavoritePresenter presenter(SetCircleFavoriteModel model) {
        return new SetCircleFavoritePresenter(view, model);
    }

    @Provides
    @Singleton
    SetCircleFavoriteModel model() {
        return new SetCircleFavoriteModel(collectionId, view.getString(R.string.circle_favorites));
    }


    @Provides
    @Singleton
    SlotsAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new SlotsAdapter(view, null, iconPack);
    }




}
