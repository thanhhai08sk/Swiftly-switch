package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.SetItemsModel;
import org.de_studio.recentappswitcher.setItems.SetItemsPagerAdapter;
import org.de_studio.recentappswitcher.setItems.SetItemsPresenter;
import org.de_studio.recentappswitcher.setItems.SetItemsView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 11/18/16.
 */
@Singleton
@Module
public class SetItemsModule {
    SetItemsView view;
    int itemsType;
    String collectionId;
    String slotId;

    public SetItemsModule(SetItemsView view, int itemsType, int itemIndex, String collectionId, String slotId) {
        this.view = view;
        this.itemsType = itemsType;
        this.collectionId = collectionId;
        this.slotId = slotId;
    }

    @Provides
    @Singleton
    SetItemsPresenter presenter(SetItemsModel model){
        return new SetItemsPresenter(view, model);
    }

    @Provides
    @Singleton
    SetItemsModel model() {
        return new SetItemsModel(itemsType, collectionId, slotId);
    }

    @Provides
    @Singleton
    SetItemsPagerAdapter adapter(){
        return new SetItemsPagerAdapter(view, view.getSupportFragmentManager(), view.onCurrentItemChange(), view.onSetItem());
    }

}
