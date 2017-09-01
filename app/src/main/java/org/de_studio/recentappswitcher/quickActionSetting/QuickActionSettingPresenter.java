package org.de_studio.recentappswitcher.quickActionSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;
import org.de_studio.recentappswitcher.model.Item;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/10/16.
 */

public class QuickActionSettingPresenter extends BaseCollectionSettingPresenter<QuickActionSettingPresenter.View, QuickActionSettingModel> {

    String slotOnsettingId;
    public QuickActionSettingPresenter(QuickActionSettingModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        addSubscription(
                view.onLoadItemsOk().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        model.loadItemsOk();
                    }
                })
        );


        view.loadItems();
        model.setup();

        addSubscription(
                view.onSetSlot().subscribe(new Action1<SlotInfo>() {
                    @Override
                    public void call(SlotInfo slotInfo) {
                        switch (slotInfo.itemTypeToAdd) {
                            case Item.TYPE_APP:
                                view.setAppToSlot(slotInfo.slotId);
                                break;
                            case Item.TYPE_ACTION:
                                view.setActionToSlot(slotInfo.slotId);
                                break;
                            case Item.TYPE_CONTACT:
                                view.setContactToSlot(slotInfo.slotId);
                                break;
                            case Item.TYPE_DEVICE_SHORTCUT:
                                view.setDeviceShortcutToSlot(slotInfo.slotId);
                                break;
                            case Item.TYPE_SHORTCUTS_SET:
                                view.setShortcutsSetToSlot(slotInfo.slotId);
                                break;
                        }
//                        view.restartService();
                    }
                })
        );

        addSubscription(
                view.onSetItemToSlot()
                        .subscribe(new Action1<Item>() {
                    @Override
                    public void call(Item item) {
                        model.setItemToSlotStage1(item, slotOnsettingId);
                        view.restartService();
                    }
                })
        );

        addSubscription(
                view.onInstantClick().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        model.setSlotInstant(integer);
                        view.showInstantChangedToast(model.getCurrentCollection().slots.get(integer).instant);
                    }
                })
        );

    }

    @Override
    public void onSlotClick(int slotIndex) {
        slotOnsettingId = model.getSlotId(slotIndex);
        view.chooseItemTypeToAdd(slotOnsettingId, slotIndex);
    }

    public void onSetVisibilityOption() {
        view.chooseVisibilityOption();
    }

    public void setVisibilityOption(int option) {
        model.setVisibilityOption(option);
        view.restartService();
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_LINEAR, -1),null);
    }

    public interface View extends BaseCollectionSettingPresenter.View {

        PublishSubject<Void> onLoadItemsOk();

        PublishSubject<SlotInfo> onSetSlot();

        PublishSubject<Item> onSetItemToSlot();

        Observable<Integer> onInstantClick();

        void loadItems();

        void chooseItemTypeToAdd(String slotId, int slotIndex);

        void chooseVisibilityOption();

        void setAppToSlot(String slotId);

        void setActionToSlot(String slotId);

        void setContactToSlot(String slotId);

        void setDeviceShortcutToSlot(String slotId);

        void setShortcutsSetToSlot(String slotId);

        void showInstantChangedToast(boolean enable);
    }

    public static class SlotInfo {
        public String slotId;
        public String itemTypeToAdd;

        public SlotInfo(String slotId, String itemTypeToAdd) {
            this.slotId = slotId;
            this.itemTypeToAdd = itemTypeToAdd;
        }
    }


}
