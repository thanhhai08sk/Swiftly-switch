package org.de_studio.recentappswitcher.setItems.chooseAction;

import android.content.Context;

import org.de_studio.recentappswitcher.base.BaseChooseItemDialogView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseActionDialogModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseActionDialogComponent;

import java.lang.ref.WeakReference;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class ChooseActionDialogView extends BaseChooseItemDialogView {


    @Override
    public void loadItems() {
        ChooseActionFragmentView.LoadActionsTask task = new ChooseActionFragmentView.LoadActionsTask(new WeakReference<Context>(getActivity()), null);
        task.execute();
    }

    @Override
    protected void inject() {
        DaggerChooseActionDialogComponent.builder()
                .appModule(new AppModule(getActivity()))
                .chooseActionDialogModule(new ChooseActionDialogModule(this))
                .build().inject(this);
    }
}
