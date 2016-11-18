package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/18/16.
 */
@Singleton
@Component(modules = {AppModule.class, ChooseAppModule.class})
public interface ChooseAppComponent {
    void inject(ChooseAppView view);
}
