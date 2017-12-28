package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.main.MainView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/12/16.
 */
@Singleton
@Component(modules = {MainModule.class, AppModule.class})
public interface MainComponent {
    void inject(MainView view);
}
