package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionFragmentView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 11/25/16.
 */
@Singleton
@Component(modules = {AppModule.class, ChooseActionModule.class})
public interface ChooseActionComponent {
    void inject(ChooseActionFragmentView view);
}
