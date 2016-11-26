package org.de_studio.recentappswitcher.gridFavoriteSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.model.Collection;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public class GridFavoriteSettingPresenter extends BaseCollectionSettingPresenter {
    private static final String TAG = GridFavoriteSettingPresenter.class.getSimpleName();
    private PublishSubject<Integer> setMarginHorizontalSubject = PublishSubject.create();
    private PublishSubject<Integer> setMarginVerticalSubject = PublishSubject.create();


    public GridFavoriteSettingPresenter(BaseCollectionSettingView view, BaseCollectionSettingModel model) {
        super(view, model);
    }


    @Override
    public void onViewAttach() {
        super.onViewAttach();
        addSubscription(
                setMarginHorizontalSubject.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        model.setHorizontalMargin();
                    }
                })
        );
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_GRID, Cons.DEFAULT_FAVORITE_GRID_COLUMN_COUNT));
    }

    public void onSetColumnsCount(int columnsCount) {

    }

    public void onSetRowsCount(int rowsCount) {

    }

    public void onSetShortcutsSpace(int shortcutsSpace) {

    }

    public void onSetPosition(int position) {
        switch (position) {
            case Collection.POSITION_TRIGGER:
                break;
            case Collection.POSITION_CENTER:
                break;
        }
    }

    public void onSetShortcutsSpaceClick() {

    }

    public void onSetMarginHorizontalClick() {

    }

    public void onSetMarginVerticalClick() {

    }

    public void onSetPositionClick() {

    }

}
