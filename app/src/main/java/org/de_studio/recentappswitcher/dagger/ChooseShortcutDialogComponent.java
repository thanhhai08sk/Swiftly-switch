package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseShortcut.ChooseShortcutDialogView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/17/16.
 */
@Singleton
@Component(modules = {AppModule.class, ChooseShortcutDialogModule.class})
public interface ChooseShortcutDialogComponent {
    void inject(ChooseShortcutDialogView view);
}
