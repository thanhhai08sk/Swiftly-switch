package org.de_studio.recentappswitcher.setItemIcon;

import android.graphics.Bitmap;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 3/17/17.
 */

public class SetItemIconPresenter extends BasePresenter<SetItemIconPresenter.View,SetItemIconModel> {


    public SetItemIconPresenter(SetItemIconModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);

        addSubscription(
                view.onItemClick().subscribe(new Action1<SetItemIconView.BitmapInfo>() {
                    @Override
                    public void call(SetItemIconView.BitmapInfo bitmapInfo) {
                        model.setItemBitmap(view.getBitmap(bitmapInfo));
                        view.finish();
                    }
                })
        );

        view.showAllItems();

    }

    public interface View extends PresenterView, SearchView.OnQueryTextListener,MenuItem.OnActionExpandListener {


        PublishSubject<SetItemIconView.BitmapInfo> onItemClick();

        Bitmap getBitmap(SetItemIconView.BitmapInfo item);

        void showAllItems();

        void finish();

    }
}
