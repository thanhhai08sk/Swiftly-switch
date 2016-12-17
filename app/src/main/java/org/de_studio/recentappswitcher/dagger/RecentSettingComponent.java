package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.recentSetting.RecentSettingView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/17/16.
 */
@Singleton
@Component(modules = {AppModule.class, RecentSettingModule.class})
public interface RecentSettingComponent {
    void inject(RecentSettingView view);
}
