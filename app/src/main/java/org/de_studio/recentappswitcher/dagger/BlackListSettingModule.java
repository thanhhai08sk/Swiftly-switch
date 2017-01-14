package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.base.adapter.ItemsListWithCheckBoxAdapter;
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
    BlackListSettingPresenter presenter() {
        return new BlackListSettingPresenter(null);
    }

    @Provides
    @Singleton
    ItemsListWithCheckBoxAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new ItemsListWithCheckBoxAdapter(view.getActivity(), null, view.getActivity().getPackageManager(), iconPack, null);
    }



}
