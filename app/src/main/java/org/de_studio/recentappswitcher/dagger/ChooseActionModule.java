package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.adapter.ItemsListAdapter;
import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionPresenter;
import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 11/25/16.
 */

@Singleton
@Module
public class ChooseActionModule {
    ChooseActionView view;

    public ChooseActionModule(ChooseActionView view) {
        this.view = view;
    }

    @Provides
    @Singleton
    ChooseActionPresenter presenter() {
        return new ChooseActionPresenter(view);
    }


    @Provides
    @Singleton
    ItemsListAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new ItemsListAdapter(view.getActivity(), null, view.getActivity().getPackageManager(), iconPack, R.layout.item_items_list_radio_button);
    }

}
