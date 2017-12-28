package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppDialogView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/17/16.
 */
@Singleton
@Component(modules = {AppModule.class, ChooseAppDialogModule.class})
public interface ChooseAppDialogComponent {
    void inject(ChooseAppDialogView view);
}
