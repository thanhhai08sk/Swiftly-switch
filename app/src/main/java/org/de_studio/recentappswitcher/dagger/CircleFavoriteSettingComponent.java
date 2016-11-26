package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.circleFavoriteSetting.CircleFavoriteSettingView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/12/16.
 */
@Singleton
@Component(modules = {AppModule.class, CircleFavoriteSettingModule.class})
public interface CircleFavoriteSettingComponent {
    void inject(CircleFavoriteSettingView view);
}
