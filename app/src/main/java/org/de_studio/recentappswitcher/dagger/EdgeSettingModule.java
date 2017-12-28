package org.de_studio.recentappswitcher.dagger;

import android.content.SharedPreferences;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.main.edgeSetting.EdgeSettingModel;
import org.de_studio.recentappswitcher.main.edgeSetting.EdgeSettingPresenter;
import org.de_studio.recentappswitcher.main.edgeSetting.EdgeSettingView;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 1/13/17.
 */
@Module
public class EdgeSettingModule {
    EdgeSettingView view;
    String edgeId;

    public EdgeSettingModule(EdgeSettingView view, String edgeId) {
        this.view = view;
        this.edgeId = edgeId;
    }

    @Provides
    @Singleton
    EdgeSettingPresenter presenter(EdgeSettingModel model){
        return new EdgeSettingPresenter(model);
    }

    @Provides
    @Singleton
    EdgeSettingModel model(@Named(Cons.SHARED_PREFERENCE_NAME) SharedPreferences preference){
        return new EdgeSettingModel(edgeId, preference);
    }

}
