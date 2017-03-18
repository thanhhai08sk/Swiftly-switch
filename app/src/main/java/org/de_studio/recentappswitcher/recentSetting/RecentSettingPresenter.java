package org.de_studio.recentappswitcher.recentSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCircleCollectionSettingPresenter;
import org.de_studio.recentappswitcher.model.Slot;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class RecentSettingPresenter extends BaseCircleCollectionSettingPresenter<RecentSettingPresenter.View, RecentSettingModel> {

    public RecentSettingPresenter(RecentSettingModel model) {
        super(model);
    }

    @Override
    public void onSlotClick(int slotIndex) {
        Slot slot = model.getCurrentCollection().slots.get(slotIndex);
        view.chooseToSetRecentOrShortcutToSlot(slotIndex, slot.type.equals(Slot.TYPE_ITEM));
    }

    public void setThisSlotAsRecent(int slotIndex) {
        model.setSlotAsRecent(slotIndex);
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_LINEAR, -1),null);
    }



    public interface View extends BaseCircleCollectionSettingPresenter.View {
        void chooseToSetRecentOrShortcutToSlot(int slotIndex, boolean isItem);
    }

}
