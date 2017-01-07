package org.de_studio.recentappswitcher.dagger;

import android.support.annotation.Nullable;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.adapter.ItemsListAdapter;
import org.de_studio.recentappswitcher.base.adapter.ShortcutListAdapter;
import org.de_studio.recentappswitcher.setItems.chooseShortcut.ChooseShortcutFragmentView;
import org.de_studio.recentappswitcher.setItems.chooseShortcut.ChooseShortcutPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 11/25/16.
 */
@Singleton
@Module
public class ChooseShortcutModule {
    ChooseShortcutFragmentView view;

    public ChooseShortcutModule(ChooseShortcutFragmentView view) {
        this.view = view;
    }

    @Provides
    @Singleton
    ChooseShortcutPresenter presenter() {
        return new ChooseShortcutPresenter(null);
    }

    @Provides
    @Singleton
    ShortcutListAdapter adapter() {
        return new ShortcutListAdapter(view.getActivity(), null);
    }

    @Provides
    @Singleton
    ItemsListAdapter fakeAdapter(@Nullable IconPackManager.IconPack iconPack){
        return new ItemsListAdapter(view.getActivity(), null, view.getActivity().getPackageManager(), iconPack, R.layout.item_items_list_radio_button);
    }
}
