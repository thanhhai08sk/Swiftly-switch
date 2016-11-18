package org.de_studio.recentappswitcher.dagger;

import org.de_studio.recentappswitcher.setItems.SetItemsView;

import javax.inject.Singleton;

import dagger.Module;

/**
 * Created by HaiNguyen on 11/18/16.
 */
@Singleton
@Module
public class SetItemsModule {
    SetItemsView view;
    int itemsType;
    int itemIndex;
    String collectionId;
    String itemId;

    public SetItemsModule(SetItemsView view, int itemsType, int itemIndex, String collectionId, String itemId) {
        this.view = view;
        this.itemsType = itemsType;
        this.itemIndex = itemIndex;
        this.collectionId = collectionId;
        this.itemId = itemId;
    }


}
