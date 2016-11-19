package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.SetItemsView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/19/16.
 */
@Singleton
@Component(modules = {AppModule.class, SetItemsModule.class})
public interface SetItemsComponent {
    void inject(SetItemsView view);
}
