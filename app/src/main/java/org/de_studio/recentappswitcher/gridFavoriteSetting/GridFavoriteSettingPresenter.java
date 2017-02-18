package org.de_studio.recentappswitcher.gridFavoriteSetting;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingPresenter;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public class GridFavoriteSettingPresenter extends BaseCollectionSettingPresenter<GridFavoriteSettingPresenter.View,GridFavoriteSettingModel> {
    private static final String TAG = GridFavoriteSettingPresenter.class.getSimpleName();
    private PublishSubject<Integer> setMarginHorizontalSubject = PublishSubject.create();
    private PublishSubject<Integer> setMarginVerticalSubject = PublishSubject.create();
    private PublishSubject<Integer> setShortcutsSpaceSubject = PublishSubject.create();


    public GridFavoriteSettingPresenter(GridFavoriteSettingModel model) {
        super(model);
    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        addSubscription(
                setMarginHorizontalSubject.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        model.setHorizontalMargin(integer);
                    }
                })
        );

        addSubscription(
                setMarginVerticalSubject.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        model.setVerticalMargin(integer);
                    }
                })
        );

        addSubscription(
                setShortcutsSpaceSubject.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        model.setShortcutsSpace(integer);
                        view.setShorcutsSpace(integer);
                    }
                })
        );
    }

    @Override
    public void setRecyclerView() {
        view.setRecyclerView(model.getSlots(), view.getLayoutManager(Cons.LAYOUT_TYPE_GRID, model.getCurrentCollection().columnCount)
                , new GridSpacingItemDecoration(view.dpToPixel(model.getCurrentCollection().space)));
        view.setChoosingMargins(model.getCurrentCollection().position == Collection.POSITION_TRIGGER);
    }

    @Override
    public void onSlotClick(int slotIndex) {
        Slot slot = model.getSlots().get(slotIndex);
        if (slot.type.equals(Slot.TYPE_FOLDER)) {
            view.openSetFolder(slot.slotId);
        } else {
            view.showChooseBetweenSetFolderAndSetItems(slotIndex);
        }
    }

    public void onSetColumnsCount(int columnsCount) {
        model.setColumnsCount(columnsCount);
        view.setGridColumn(columnsCount);
        view.restartService();
    }

    public void onSetRowsCount(int rowsCount) {
        model.setRowsCount(rowsCount);
        view.restartService();
    }


    public void onSetPosition(int position) {
        model.setPosition(position);
        switch (position) {
            case Collection.POSITION_TRIGGER:
                view.setChoosingMargins(true);
                break;
            case Collection.POSITION_CENTER:
                view.setChoosingMargins(false);
                break;
        }
    }

    public void onSetShortcutsSpaceClick() {
        view.showChooseShortcutSpace(Cons.FAVORITE_GRID_MIN_SHORTCUTS_SPACE
                , Cons.FAVORITE_GRID_MAX_SHORTCUTS_SPACE
                , model.getCurrentCollection().space
                , setShortcutsSpaceSubject);
    }

    public void onSetMarginHorizontalClick() {
        view.showChooseMarginHorizontal(Cons.FAVORITE_GRID_MIN_HORIZONTAL_MARGIN
                , Cons.FAVORITE_GRID_MAX_HORIZONTAL_MARGIN
                , model.getCurrentCollection().marginHorizontal, setMarginHorizontalSubject);
    }

    public void onSetMarginVerticalClick() {
        view.showChooseMarginVertical(Cons.FAVORITE_GRID_MIN_VERTICAL_MARGIN
                , Cons.FAVORITE_GRID_MAX_VERTICAL_MARGIN
                , model.getCurrentCollection().marginVertical, setMarginVerticalSubject);
    }

    public void onSetRowsCountClick() {
        view.showChooseRowsCount();
    }

    public void onSetColumnsCountClick() {
        view.showChooseColumnsCount();
    }

    public void onSetPositionClick() {
        view.showChoosePositionDialog();
    }

    public void onSetStayOnScreen() {
        model.setStayOnScreen();
    }


    public interface View extends BaseCollectionSettingPresenter.View {
        void showChooseColumnsCountDialog();


        void setChoosingMargins(boolean enable);

        void setGridColumn(int column);

        void setShorcutsSpace(int space);

        void showChooseColumnsCount();

        void showChooseRowsCount();

        void showChooseShortcutSpace(int min, int max, int current, PublishSubject<Integer> subject);

        void showChooseMarginVertical(int min, int max, int current, PublishSubject<Integer> subject);

        void showChooseMarginHorizontal(int min, int max, int current, PublishSubject<Integer> subject);

        void showChoosePositionDialog();

        void showChooseRowsCountDialog();



    }


}
