package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.recentSetting.RecentSettingModel;
import org.de_studio.recentappswitcher.recentSetting.RecentSettingPresenter;
import org.de_studio.recentappswitcher.recentSetting.RecentSettingView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 12/17/16.
 */
@Module
public class RecentSettingModule {
    RecentSettingView view;
    String collectionId;

    public RecentSettingModule(RecentSettingView view, String collectionId) {
        this.view = view;
        this.collectionId = collectionId;
    }

    @Provides
    @Singleton
    RecentSettingPresenter presenter(RecentSettingModel model) {
        return new RecentSettingPresenter(model);
    }

    @Provides
    @Singleton
    RecentSettingModel model() {
        return new RecentSettingModel(view.getString(R.string.recent_apps), collectionId);
    }


    @Provides
    @Singleton
    SlotsAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new SlotsAdapter(view, null, false, iconPack, Cons.ITEM_TYPE_ICON_LABEL);
    }
}
