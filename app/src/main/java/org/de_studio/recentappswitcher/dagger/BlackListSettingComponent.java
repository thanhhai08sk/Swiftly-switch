package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.blackListSetting.BlackListSettingView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/17/16.
 */
@Singleton
@Component(modules = {AppModule.class, BlackListSettingModule.class})
public interface BlackListSettingComponent {
    void inject(BlackListSettingView view);
}
