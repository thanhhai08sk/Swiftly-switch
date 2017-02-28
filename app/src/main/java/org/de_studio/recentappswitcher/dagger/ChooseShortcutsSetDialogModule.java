package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseChooseItemPresenter;
import org.de_studio.recentappswitcher.base.adapter.ItemsListAdapter;
import org.de_studio.recentappswitcher.setItems.chooseShortcutsSet.ChooseShortcutsSetDialogView;
import org.de_studio.recentappswitcher.setItems.chooseShortcutsSet.ChooseShortcutsSetPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 12/17/16.
 */

@Module
public class ChooseShortcutsSetDialogModule {
    ChooseShortcutsSetDialogView view;
    String collectionType;
    public ChooseShortcutsSetDialogModule(ChooseShortcutsSetDialogView view, String collectionType) {
        this.view = view;
        this.collectionType = collectionType;
    }

    @Provides
    @Singleton
    BaseChooseItemPresenter presenter() {
        return new ChooseShortcutsSetPresenter(null, collectionType);
    }


    @Provides
    @Singleton
    ItemsListAdapter adapter(@Nullable IconPackManager.IconPack iconPack){
        return new ItemsListAdapter(view.getActivity(), null, view.getActivity().getPackageManager(), iconPack, R.layout.item_items_list);
    }
}
