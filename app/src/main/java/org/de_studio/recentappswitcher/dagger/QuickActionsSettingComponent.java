package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.quickActionSetting.QuickActionSettingView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 12/16/16.
 */
@Singleton
@Component(modules = {AppModule.class, QuickActionsSettingModule.class})
public interface QuickActionsSettingComponent {
    void inject(QuickActionSettingView view);
}
