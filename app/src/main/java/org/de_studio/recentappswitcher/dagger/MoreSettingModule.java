package org.de_studio.recentappswitcher.dagger;

import android.content.SharedPreferences;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.main.moreSetting.MoreSettingPresenter;
import org.de_studio.recentappswitcher.main.moreSetting.MoreSettingView;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 2/3/17.
 */
@Module
public class MoreSettingModule {
    MoreSettingView view;

    public MoreSettingModule(MoreSettingView view) {
        this.view = view;
    }

    @Provides
    @Singleton
    MoreSettingPresenter presenter(@Named(Cons.SHARED_PREFERENCE_NAME) SharedPreferences shared) {
        return new MoreSettingPresenter(null, shared);
    }

}
