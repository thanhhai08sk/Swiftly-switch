package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.adapter.ItemsListAdapter;
import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppFragmentView;
import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 11/18/16.
 */
@Module
public class ChooseAppFragmentModule {
    ChooseAppFragmentView view;

    public ChooseAppFragmentModule(ChooseAppFragmentView view) {
        this.view = view;
    }

    @Provides
    @Singleton
    ChooseAppPresenter presenter() {
        return new ChooseAppPresenter(null);
    }


    @Provides
    @Singleton
    ItemsListAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new ItemsListAdapter(view.getActivity(), null, view.getActivity().getPackageManager(), iconPack, R.layout.item_items_list_radio_button);
    }



}
