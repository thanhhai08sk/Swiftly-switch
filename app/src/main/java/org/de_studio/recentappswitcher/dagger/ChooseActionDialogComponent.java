package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionDialogView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/17/16.
 */
@Singleton
@Component(modules = {AppModule.class, ChooseActionDialogModule.class})
public interface ChooseActionDialogComponent {
    void inject(ChooseActionDialogView view);
}
