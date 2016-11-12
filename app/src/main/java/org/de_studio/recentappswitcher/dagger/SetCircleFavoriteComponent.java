package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setCircleFavorite.SetCircleFavoriteView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/12/16.
 */
@Singleton
@Component(modules = {AppModule.class, SetCircleFavoriteModule.class})
public interface SetCircleFavoriteComponent {
    void inject(SetCircleFavoriteView view);
}
