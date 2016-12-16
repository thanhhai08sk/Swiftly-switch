package org.de_studio.recentappswitcher.gridFavoriteSetting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerGridFavoriteSettingComponent;
import org.de_studio.recentappswitcher.dagger.GridFavoriteSettingModule;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public class GridFavoriteSettingView extends BaseCollectionSettingView implements GridFavoriteSettingPresenter.View {
    private static final String TAG = GridFavoriteSettingView.class.getSimpleName();
    @BindView(R.id.margin)
    LinearLayout marginLayout;
    @BindView(R.id.columns_count_value)
    TextView columnsCount;
    @BindView(R.id.rows_count_value)
    TextView rowsCount;
    @BindView(R.id.shortcuts_space_value)
    TextView space;
    @BindView(R.id.position_value)
    TextView position;
    @BindView(R.id.horizontal_margin_value)
    TextView horizontalMargin;
    @BindView(R.id.vertical_margin_value)
    TextView verticalMargin;
    
    @Override
    protected void inject() {
        DaggerGridFavoriteSettingComponent.builder()
                .appModule(new AppModule(this))
                .gridFavoriteSettingModule(new GridFavoriteSettingModule(this, collectionId))
                .build()
                .inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.grid_favorite_setting;
    }
    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, GridFavoriteSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }

    private GridFavoriteSettingPresenter getGridPresenter() {
        return ((GridFavoriteSettingPresenter) presenter);
    }

    public void showChooseColumnsCountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.columns_count)
                .setItems(new CharSequence[]{"1","2","3","4","5", "6", "7"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getGridPresenter().onSetColumnsCount(which + 1);
                    }
                });
        builder.create().show();
    }

    public void showChooseRowsCountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.rows_count)
                .setItems(new CharSequence[]{"1","2","3","4","5", "6", "7"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getGridPresenter().onSetRowsCount(which + 1);
                    }
                });
        builder.create().show();
    }

    public void showChoosePositionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.edge_dialog_set_position_text)
                .setItems(R.array.gird_favorite_position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                getGridPresenter().onSetPosition(Collection.POSITION_TRIGGER);
                                break;
                            case 1:
                                getGridPresenter().onSetPosition(Collection.POSITION_CENTER);
                                break;
                        }
                    }
                });
        builder.create().show();
    }




    public void showChooseMarginHorizontal(int min, int max, int current, PublishSubject<Integer> subject) {
        Utility.showDialogWithSeekBar(min,max,current,"dp",getString(R.string.horizontal_margin),subject,this);
    }

    public void showChooseMarginVertical(int min, int max, int current, PublishSubject<Integer> subject) {
        Utility.showDialogWithSeekBar(min,max,current,"dp",getString(R.string.vertical_margin),subject,this);
    }

    public void showChooseShortcutSpace(int min, int max, int current, PublishSubject<Integer> subject) {
        Utility.showDialogWithSeekBar(min, max, current, "dp", getString(R.string.set_favorite_shortcut_grid_gap_title_text_view), subject, this);
    }

    public void showChooseRowsCount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.rows_count)
                .setItems(new CharSequence[]{"1","2","3","4", "5", "6", "7", "8"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getGridPresenter().onSetRowsCount(which + 1);
                    }
                });
        builder.create().show();
    }

    public void showChooseColumnsCount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.columns_count)
                .setItems(new CharSequence[]{"1","2","3","4", "5", "6", "7", "8"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getGridPresenter().onSetColumnsCount(which + 1);
                    }
                });
        builder.create().show();
    }

    public void setShorcutsSpace(int space) {
        recyclerView.removeItemDecoration(decoration);
        decoration = new GridSpacingItemDecoration(Utility.dpToPixel(this, space));
        recyclerView.addItemDecoration(decoration);
    }

    public void setGridColumn(int column) {
        manager.setSpanCount(column);
    }

    public void setChoosingMargins(boolean enable) {
        marginLayout.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);

    }

    public void updateValueText(Collection collection) {
        columnsCount.setText(String.valueOf(collection.columnCount));
        rowsCount.setText(String.valueOf(collection.rowsCount));
        space.setText(String.valueOf(collection.space));
        String positionValue = "";
        switch (collection.position) {
            case Collection.POSITION_TRIGGER:
                positionValue = getString(R.string.grid_position_trigger_position);
                break;
            case Collection.POSITION_CENTER:
                positionValue = getString(R.string.grid_position_center);
                break;
        }
        position.setText(positionValue);
        verticalMargin.setText(String.valueOf(collection.marginVertical));
        horizontalMargin.setText(String.valueOf(collection.marginHorizontal));
    }


    @OnClick(R.id.rows_count)
    void onSetRowsClick(){
        getGridPresenter().onSetRowsCountClick();
    }
    @OnClick(R.id.columns_count)
    void onSetColumnsCountClick(){
        getGridPresenter().onSetColumnsCountClick();
    }
    @OnClick(R.id.shortcuts_space)
    void onSetShortcutsSpaceClick(){
        getGridPresenter().onSetShortcutsSpaceClick();
    }

    @OnClick(R.id.horizontal_margin)
    void onMarginHorizontalClick(){
        getGridPresenter().onSetMarginHorizontalClick();
    }

    @OnClick(R.id.vertical_margin)
    void onMarginVerticalClick(){
        getGridPresenter().onSetMarginVerticalClick();
    }

    @OnClick(R.id.position)
    void onPositionClick(){
        getGridPresenter().onSetPositionClick();
    }
}
