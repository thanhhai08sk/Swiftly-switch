package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseShortcutsSet.ChooseShortcutsSetDialogView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/17/16.
 */

@Singleton
@Component(modules = {AppModule.class, ChooseShortcutsSetDialogModule.class})
public interface ChooseShortcutsSetDialogComponent {
    void inject(ChooseShortcutsSetDialogView view);
}
