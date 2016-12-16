package org.de_studio.recentappswitcher.quickActionSetting;

import android.content.Context;
import android.content.Intent;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerQuickActionsSettingComponent;
import org.de_studio.recentappswitcher.dagger.QuickActionsSettingModule;
import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionView;

import java.lang.ref.WeakReference;

import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/10/16.
 */

public class QuickActionSettingView extends BaseCollectionSettingView implements QuickActionSettingPresenter.View{

    PublishSubject<Void> loadItemsOkSubject = PublishSubject.create();

    @Override
    protected void inject() {
        DaggerQuickActionsSettingComponent.builder()
                .appModule(new AppModule(this))
                .quickActionsSettingModule(new QuickActionsSettingModule(this, collectionId))
                .build().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.quick_action_setting;
    }

    @Override
    public void loadItems() {
        ChooseActionView.LoadActionsTask task = new ChooseActionView.LoadActionsTask(new WeakReference<Context>(this), loadItemsOkSubject);
        task.execute();
    }

    @Override
    public PublishSubject<Void> onLoadItemsOk() {
        return loadItemsOkSubject;
    }

    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, QuickActionSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }


}
