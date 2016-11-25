package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseShortcut.ChooseShortcutView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/25/16.
 */
@Singleton
@Component(modules = {AppModule.class, ChooseShortcutModule.class})
public interface ChooseShortcutComponent {
    void inject(ChooseShortcutView view);
}
