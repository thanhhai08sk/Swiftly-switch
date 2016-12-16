package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;
import org.de_studio.recentappswitcher.quickActionSetting.QuickActionSettingModel;
import org.de_studio.recentappswitcher.quickActionSetting.QuickActionSettingPresenter;
import org.de_studio.recentappswitcher.quickActionSetting.QuickActionSettingView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 12/16/16.
 */
@Module
public class QuickActionsSettingModule {
    QuickActionSettingView view;
    String collectionId;

    public QuickActionsSettingModule(QuickActionSettingView view, String collectionId) {
        this.view = view;
        this.collectionId = collectionId;
    }
    
    @Provides
    @Singleton
    BaseCollectionSettingPresenter presenter(QuickActionSettingModel model){
        return new QuickActionSettingPresenter(model);
    }
    
    @Provides
    @Singleton
    QuickActionSettingModel model(){
        return new QuickActionSettingModel(view.getString(R.string.main_outer_ring_setting), collectionId);
    }

    @Provides
    @Singleton
    SlotsAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new SlotsAdapter(view, null, false, iconPack, Cons.ITEM_TYPE_ICON_LABEL);
    }



}
