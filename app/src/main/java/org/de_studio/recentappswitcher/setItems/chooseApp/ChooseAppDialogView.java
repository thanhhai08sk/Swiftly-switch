package org.de_studio.recentappswitcher.setItems.chooseApp;

import android.content.pm.PackageManager;
import android.util.Log;

import org.de_studio.recentappswitcher.base.BaseChooseItemDialogView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseAppDialogModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseAppDialogComponent;

import java.lang.ref.WeakReference;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class ChooseAppDialogView extends BaseChooseItemDialogView {

    @Override
    public void loadItems() {
        Log.e("ChooseAppDialogView", "loadItems: ");
        ChooseAppFragmentView.LoadAppsTask task = new ChooseAppFragmentView.LoadAppsTask(new WeakReference<PackageManager>(getActivity().getPackageManager()));
        task.execute();
    }

    @Override
    protected void inject() {
        DaggerChooseAppDialogComponent.builder()
                .appModule(new AppModule(getActivity()))
                .chooseAppDialogModule(new ChooseAppDialogModule(this))
                .build().inject(this);
    }
}
