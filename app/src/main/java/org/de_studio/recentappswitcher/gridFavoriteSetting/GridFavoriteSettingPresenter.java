package org.de_studio.recentappswitcher.gridFavoriteSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingModel;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public class GridFavoriteSettingPresenter extends BaseCollectionSettingPresenter {
    private static final String TAG = GridFavoriteSettingPresenter.class.getSimpleName();
    private PublishSubject<Integer> setMarginHorizontalSubject = PublishSubject.create();
    private PublishSubject<Integer> setMarginVerticalSubject = PublishSubject.create();
    private PublishSubject<Integer> setShortcutsSpaceSubject = PublishSubject.create();


    public GridFavoriteSettingPresenter(BaseCollectionSettingModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(BaseCollectionSettingView view) {
        super.onViewAttach(view);
        addSubscription(
                setMarginHorizontalSubject.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        getGridModel().setHorizontalMargin(integer);
                    }
                })
        );

        addSubscription(
                setMarginVerticalSubject.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        getGridModel().setVerticalMargin(integer);
                    }
                })
        );

        addSubscription(
                setShortcutsSpaceSubject.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        getGridModel().setShortcutsSpace(integer);
                        getGridView().setShorcutsSpace(integer);
                    }
                })
        );
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_GRID, Cons.DEFAULT_FAVORITE_GRID_COLUMN_COUNT)
                , new GridSpacingItemDecoration(Utility.dpToPixel(view, model.getCurrentCollection().space)));
        getGridView().setChoosingMargins(model.getCurrentCollection().position == Collection.POSITION_TRIGGER);
    }

    public void onSetColumnsCount(int columnsCount) {
        getGridModel().setColumnsCount(columnsCount);
        getGridView().setGridColumn(columnsCount);

    }

    public void onSetRowsCount(int rowsCount) {
        getGridModel().setRowsCount(rowsCount);
    }





    public void onSetPosition(int position) {
        getGridModel().setPosition(position);
        switch (position) {
            case Collection.POSITION_TRIGGER:
                getGridView().setChoosingMargins(true);
                break;
            case Collection.POSITION_CENTER:
                getGridView().setChoosingMargins(false);
                break;
        }
    }

    public void onSetShortcutsSpaceClick() {
        getGridView().showChooseShortcutSpace(Cons.FAVORITE_GRID_MIN_SHORTCUTS_SPACE
                , Cons.FAVORITE_GRID_MAX_SHORTCUTS_SPACE
                , model.getCurrentCollection().space
                , setShortcutsSpaceSubject);
    }

    public void onSetMarginHorizontalClick() {
        getGridView().showChooseMarginHorizontal(Cons.FAVORITE_GRID_MIN_HORIZONTAL_MARGIN
                , Cons.FAVORITE_GRID_MAX_HORIZONTAL_MARGIN
                , model.getCurrentCollection().marginHorizontal, setMarginHorizontalSubject);
    }

    public void onSetMarginVerticalClick() {
        getGridView().showChooseMarginVertical(Cons.FAVORITE_GRID_MIN_VERTICAL_MARGIN
                , Cons.FAVORITE_GRID_MAX_VERTICAL_MARGIN
                , model.getCurrentCollection().marginVertical, setMarginVerticalSubject);
    }

    public void onSetRowsCountClick() {
        getGridView().showChooseRowsCount();
    }

    public void onSetColumnsCountClick() {
        getGridView().showChooseColumnsCount();
    }

    public void onSetPositionClick() {
        getGridView().showChoosePositionDialog();
    }

    public GridFavoriteSettingModel getGridModel() {
        return ((GridFavoriteSettingModel) model);
    }

    public GridFavoriteSettingView getGridView() {
        return ((GridFavoriteSettingView) view);
    }

}
