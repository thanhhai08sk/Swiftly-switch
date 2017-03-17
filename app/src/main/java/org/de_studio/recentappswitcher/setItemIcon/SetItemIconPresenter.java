package org.de_studio.recentappswitcher.setItemIcon;

import android.graphics.Bitmap;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;

import java.util.SortedMap;

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
                view.onSearch().subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        view.updateAdapter(model.getDrawables(s));
                    }
                })
        );

        addSubscription(
                view.onDrawableClick().subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        model.setItemBitmap(view.getBitmap(s));
                    }
                })
        );

        view.updateAdapter(model.getDrawables(null));

    }

    public interface View extends PresenterView, SearchView.OnQueryTextListener,MenuItem.OnActionExpandListener {

        PublishSubject<String> onSearch();

        PublishSubject<String> onDrawableClick();

        Bitmap getBitmap(String drawable);

        void updateAdapter(SortedMap<String, String> sortedDrawableMap);

    }
}
