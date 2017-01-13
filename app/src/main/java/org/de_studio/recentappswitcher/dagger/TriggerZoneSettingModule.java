package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.main.triggerZoneSetting.TriggerZoneSettingPresenter;
import org.de_studio.recentappswitcher.main.triggerZoneSetting.TriggerZoneSettingView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 1/13/17.
 */
@Module
public class TriggerZoneSettingModule {
    TriggerZoneSettingView view;
    String edgeId;

    public TriggerZoneSettingModule(TriggerZoneSettingView view, String edgeId) {
        this.view = view;
        this.edgeId = edgeId;
    }

    @Provides
    @Singleton
    TriggerZoneSettingPresenter presenter(){
        return new TriggerZoneSettingPresenter(null, edgeId);
    }
}
