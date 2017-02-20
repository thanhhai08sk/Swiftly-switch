package org.de_studio.recentappswitcher.quickActionSetting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerQuickActionsSettingComponent;
import org.de_studio.recentappswitcher.dagger.QuickActionsSettingModule;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionDialogView;
import org.de_studio.recentappswitcher.setItems.chooseAction.ChooseActionFragmentView;
import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppDialogView;
import org.de_studio.recentappswitcher.setItems.chooseContact.ChooseContactDialogView;
import org.de_studio.recentappswitcher.setItems.chooseShortcut.ChooseShortcutDialogView;
import org.de_studio.recentappswitcher.setItems.chooseShortcutsSet.ChooseShortcutsSetDialogView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/10/16.
 */

public class QuickActionSettingView extends BaseCollectionSettingView<Void, QuickActionSettingPresenter> implements QuickActionSettingPresenter.View{


    @BindView(R.id.size_text)
    TextView sizeText;
    @BindView(R.id.visibility_option_description)
    TextView visibilityOptionDescription;

    PublishSubject<Void> loadItemsOkSubject = PublishSubject.create();
    PublishSubject<QuickActionSettingPresenter.SlotInfo> setSlotSubject = PublishSubject.create();
    PublishSubject<Item> setItemToSlotSubject = PublishSubject.create();


    @Override
    public void updateCollectionInfo(Collection collection) {
        super.updateCollectionInfo(collection);
        sizeText.setText(String.valueOf(collection.slots.size()));
        switch (collection.visibilityOption) {
            case Collection.VISIBILITY_OPTION_ONLY_TRIGGERED_ONE_VISIBLE:
                visibilityOptionDescription.setText(R.string.only_triggered_one_visible);
                break;
            case Collection.VISIBILITY_OPTION_TRIGGER_ONE_MAKE_ALL_VISIBLE:
                visibilityOptionDescription.setText(R.string.trigger_one_make_all_visible);
                break;
            case Collection.VISIBILITY_OPTION_ALWAYS_VISIBLE:
                visibilityOptionDescription.setText(R.string.always_visible);
                break;
        }
    }

    @Override
    protected void inject() {
        DaggerQuickActionsSettingComponent.builder()
                .appModule(new AppModule(this))
                .quickActionsSettingModule(new QuickActionsSettingModule(this, collectionId))
                .build().inject(this);
    }

    @Override
    public void showChooseSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.set_size)
                .setItems(new CharSequence[]{"4", "5", "6", "7", "8"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onChooseCollectionSize(which + 4);
                    }
                });
        builder.create().show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.quick_action_setting;
    }

    @Override
    public void loadItems() {
        ChooseActionFragmentView.LoadActionsTask task = new ChooseActionFragmentView.LoadActionsTask(new WeakReference<Context>(this), loadItemsOkSubject);
        task.execute();
    }

    @Override
    public boolean isHoverOnDeleteButton(float x, float y) {
        return  y > deleteButton.getY() - deleteButton.getHeight()*2;
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
    public PublishSubject<Item> onSetItemToSlot() {
        return setItemToSlotSubject;
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
                        break;
                    case 1:
                        setSlotSubject.onNext(new QuickActionSettingPresenter.SlotInfo(slotId, Item.TYPE_ACTION));
                        break;
                    case 2:
                        setSlotSubject.onNext(new QuickActionSettingPresenter.SlotInfo(slotId, Item.TYPE_CONTACT));
                        break;
                    case 3:
                        setSlotSubject.onNext(new QuickActionSettingPresenter.SlotInfo(slotId, Item.TYPE_DEVICE_SHORTCUT));
                        break;
                    case 4:
                        setSlotSubject.onNext(new QuickActionSettingPresenter.SlotInfo(slotId, Item.TYPE_SHORTCUTS_SET));
                        break;

                }
            }
        });
        builder.create().show();
    }

    @Override
    public void chooseVisibilityOption() {
        Utility.showDialogWithOptionToChoose(this, R.string.visibility_option,
                new int[]{R.string.only_triggered_one_visible, R.string.trigger_one_make_all_visible, R.string.always_visible},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.setVisibilityOption(which);
                    }
                });
    }

    @Override
    public void setAppToSlot(String slotId) {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        ChooseAppDialogView chooseAppDialogView = new ChooseAppDialogView();
        chooseAppDialogView.setSubjects(null, setItemToSlotSubject);
        chooseAppDialogView.show(fragmentManager1, "chooseAppDialog");
    }

    @Override
    public void setActionToSlot(String slotId) {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        ChooseActionDialogView dialogView = new ChooseActionDialogView();
        dialogView.setSubjects(null, setItemToSlotSubject);
        dialogView.show(fragmentManager1, "chooseActionDialog");
    }

    @Override
    public void setContactToSlot(String slotId) {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        ChooseContactDialogView dialogView = new ChooseContactDialogView();
        dialogView.setSubjects(null, setItemToSlotSubject);
        dialogView.show(fragmentManager1, "chooseContactDialog");
    }

    @Override
    public void setDeviceShortcutToSlot(String slotId) {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        ChooseShortcutDialogView dialogView = new ChooseShortcutDialogView();
        dialogView.setSubjects(null, setItemToSlotSubject);
        dialogView.show(fragmentManager1, "chooseShortcutDialog");
    }

    @Override
    public void setShortcutsSetToSlot(String slotId) {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        ChooseShortcutsSetDialogView dialogView = new ChooseShortcutsSetDialogView();
        dialogView.setSubjects(null, setItemToSlotSubject);
        dialogView.show(fragmentManager1, "chooseShortcutsSetDialog");
    }

    @OnClick(R.id.visibility_option)
    void onVisibilityOptionClick(){
        presenter.onSetVisibilityOption();
    }

    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, QuickActionSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }



}
