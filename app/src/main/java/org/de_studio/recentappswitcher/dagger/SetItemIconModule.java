package org.de_studio.recentappswitcher.dagger;

import android.content.pm.PackageManager;

import org.de_studio.recentappswitcher.setItemIcon.DrawableAdapter;
import org.de_studio.recentappswitcher.setItemIcon.SetItemIconModel;
import org.de_studio.recentappswitcher.setItemIcon.SetItemIconPresenter;
import org.de_studio.recentappswitcher.setItemIcon.SetItemIconView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by HaiNguyen on 3/18/17.
 */

@Module
public class SetItemIconModule {
    String itemId;
    SetItemIconView view;

    public SetItemIconModule(String itemId, SetItemIconView view) {
        this.itemId = itemId;
        this.view = view;
    }

    @Provides
    @Singleton
    SetItemIconPresenter presenter(SetItemIconModel model) {
        return new SetItemIconPresenter(model);
    }

    @Provides
    @Singleton
    SetItemIconModel model() {
        return new SetItemIconModel(itemId);
    }

    @Provides
    @Singleton
    DrawableAdapter adapter() {
        return new DrawableAdapter(view, null);
    }

    @Provides
    @Singleton
    PackageManager manager(){
        return view.getPackageManager();
    }




}
