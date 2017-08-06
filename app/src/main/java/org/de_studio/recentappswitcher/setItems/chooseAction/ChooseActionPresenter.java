package org.de_studio.recentappswitcher.setItems.chooseAction;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseChooseItemPresenter;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Item;

import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 11/19/16.
 */

public class ChooseActionPresenter extends BaseChooseItemPresenter {



    public ChooseActionPresenter(BaseModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
//        addSubscription(
//                view.onItemClick().subscribe(new Action1<Item>() {
//                    @Override
//                    public void call(Item item) {
//                        if (item.action == Item.ACTION_SCREEN_LOCK) {
//                            view.noticeUserAboutScreenLock();
//                        }
//                    }
//                })
//        );
    }

    @Override
    protected RealmResults<Item> getItemRealmResult() {
        return realm.where(Item.class).equalTo(Cons.TYPE, Item.TYPE_ACTION).findAllAsync();
    }
}
