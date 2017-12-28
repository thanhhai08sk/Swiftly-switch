package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseContact.ChooseContactFragmentView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/25/16.
 */
@Singleton
@Component(modules = {AppModule.class,ChooseContactModule.class})
public interface ChooseContactComponent {
    void inject(ChooseContactFragmentView view);
}
