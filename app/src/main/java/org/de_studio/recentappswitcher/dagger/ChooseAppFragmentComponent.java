package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppFragmentView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/18/16.
 */
@Singleton
@Component(modules = {AppModule.class, ChooseAppFragmentModule.class})
public interface ChooseAppFragmentComponent {
    void inject(ChooseAppFragmentView view);
}
