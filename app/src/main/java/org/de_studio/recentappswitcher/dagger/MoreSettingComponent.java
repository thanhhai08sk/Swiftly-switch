package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.main.moreSetting.MoreSettingView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 2/3/17.
 */
@Singleton
@Component(
        modules = {AppModule.class, MoreSettingModule.class}
)
public interface MoreSettingComponent {
    void inject(MoreSettingView view);
}
