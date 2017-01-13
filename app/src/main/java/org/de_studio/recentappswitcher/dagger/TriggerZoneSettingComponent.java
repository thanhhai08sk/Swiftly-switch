package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.main.triggerZoneSetting.TriggerZoneSettingView;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by HaiNguyen on 1/13/17.
 */
@Singleton
@Component(modules = {TriggerZoneSettingModule.class})
public interface TriggerZoneSettingComponent {
    void inject(TriggerZoneSettingView view);
}
