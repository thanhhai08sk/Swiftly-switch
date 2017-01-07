package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.blackListSetting.BlackListSettingModel;
import org.de_studio.recentappswitcher.blackListSetting.BlackListSettingPresenter;
import org.de_studio.recentappswitcher.blackListSetting.BlackListSettingView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 12/17/16.
 */
@Module
public class BlackListSettingModule {
    BlackListSettingView view;

    public BlackListSettingModule(BlackListSettingView view) {
        this.view = view;
    }

    @Provides
    @Singleton
    BlackListSettingPresenter presenter(BlackListSettingModel model) {
        return new BlackListSettingPresenter(model);
    }

    @Provides
    @Singleton
    BlackListSettingModel model() {
        return new BlackListSettingModel(view.getString(R.string.circle_favorites), null);
    }


    @Provides
    @Singleton
    SlotsAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new SlotsAdapter(view, null, false, iconPack, Cons.ITEM_TYPE_ICON_LABEL);
    }
}
