package org.de_studio.recentappswitcher.quickActionSetting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerQuickActionsSettingComponent;
import org.de_studio.recentappswitcher.dagger.QuickActionsSettingModule;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionView;

import java.lang.ref.WeakReference;

import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/10/16.
 */

public class QuickActionSettingView extends BaseCollectionSettingView implements QuickActionSettingPresenter.View{

    PublishSubject<Void> loadItemsOkSubject = PublishSubject.create();
    PublishSubject<QuickActionSettingPresenter.SlotInfo> setSlotSubject = PublishSubject.create();


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

    @Override
    public PublishSubject<QuickActionSettingPresenter.SlotInfo> onSetSlot() {
        return setSlotSubject;
    }

    @Override
    public void chooseItemTypeToAdd(final String slotId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_shortcuts_type);
        builder.setItems(new CharSequence[]{getString(R.string.apps)
                        , getString(R.string.actions), getString(R.string.contacts), getString(R.string.device_shortcuts), getString(R.string.shortcuts_sets)}
                , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        setSlotSubject.onNext(new QuickActionSettingPresenter.SlotInfo(slotId, Item.TYPE_APP));
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void setAppToSlot(String slotId) {

    }

    @Override
    public void setActionToSlot(String slotId) {

    }

    @Override
    public void setContactToSlot(String slotId) {

    }

    @Override
    public void setDeviceShortcutToSlot(String slotId) {

    }

    @Override
    public void setShortcutsSetToSlot(String slotId) {

    }

    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, QuickActionSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }


}
