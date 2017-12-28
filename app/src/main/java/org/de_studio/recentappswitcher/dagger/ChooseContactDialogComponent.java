package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseContact.ChooseContactDialogView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/17/16.
 */
@Singleton
@Component(modules = {AppModule.class, ChooseContactDialogModule.class})
public interface ChooseContactDialogComponent {
    void inject(ChooseContactDialogView view);
}
