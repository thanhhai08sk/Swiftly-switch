package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.main.edgeSetting.EdgeSettingView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 1/13/17.
 */
@Singleton
@Component(modules = {AppModule.class, EdgeSettingModule.class})
public interface EdgeSettingComponent {
    void inject(EdgeSettingView view);
}
