package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.gridFavoriteSetting.GridFavoriteSettingView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/26/16.
 */
@Singleton
@Component(modules = {AppModule.class, GridFavoriteSettingModule.class})
public interface GridFavoriteSettingComponent {
    void inject(GridFavoriteSettingView view);
}
