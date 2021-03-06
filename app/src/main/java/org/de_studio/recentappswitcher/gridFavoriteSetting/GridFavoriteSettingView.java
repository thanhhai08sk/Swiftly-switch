package org.de_studio.recentappswitcher.gridFavoriteSetting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
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

public class GridFavoriteSettingView extends BaseCollectionSettingView<Void, GridFavoriteSettingPresenter> implements GridFavoriteSettingPresenter.View {
    private static final String TAG = GridFavoriteSettingView.class.getSimpleName();
    @BindView(R.id.margin)
    LinearLayout marginLayout;
    @BindView(R.id.columns_count)
    View columnsCount;
    @BindView(R.id.columns_count_title)
    TextView columnsCountTitle;
    @BindView(R.id.rows_count_title)
    TextView rowsCountTitle;
    @BindView(R.id.columns_count_value)
    TextView columnsCountValue;
    @BindView(R.id.rows_count)
    View rowsCount;
    @BindView(R.id.rows_count_value)
    TextView rowsCountValue;
    @BindView(R.id.shortcuts_space_value)
    TextView space;
    @BindView(R.id.position_value)
    TextView position;
    @BindView(R.id.horizontal_margin_value)
    TextView horizontalMargin;
    @BindView(R.id.vertical_margin_value)
    TextView verticalMargin;
    @BindView(R.id.stay_on_screen_switch)
    Switch stayOnScreenSwitch;
    @BindView(R.id.stay_on_screen_description)
    TextView stayOnScreenDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.textColorDisabledIfFree(rowsCountTitle);
        Utility.textColorDisabledIfFree(columnsCountTitle);
    }

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
                .setItems(new CharSequence[]{"1","2","3","4", "5", "6", "7", "8", "9", "10"}, new DialogInterface.OnClickListener() {
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
                .setItems(new CharSequence[]{"1","2","3","4", "5", "6", "7", "8", "9", "10"}, new DialogInterface.OnClickListener() {
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

    @Override
    public void updateCollectionInfo(Collection collection) {
        super.updateCollectionInfo(collection);

        columnsCountValue.setText(String.valueOf(collection.columnCount));
        rowsCountValue.setText(String.valueOf(collection.rowsCount));
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
        boolean stayOnScreen = collection.stayOnScreen == null ? true : collection.stayOnScreen;
        stayOnScreenSwitch.setChecked(stayOnScreen);
        if (stayOnScreen) {
            stayOnScreenDescription.setText(R.string.stay_on_screen_enable_description);
        } else stayOnScreenDescription.setText(R.string.stay_on_screen_disable_description);

    }



    @OnClick(R.id.rows_count)
    void onSetRowsClick(){
        if (Utility.isFree(this)) {
            Utility.showProOnlyDialog(this);
        } else {
            presenter.onSetRowsCountClick();
        }
    }
    @OnClick(R.id.columns_count)
    void onSetColumnsCountClick(){
        if (Utility.isFree(this)) {
            Utility.showProOnlyDialog(this);
        } else {
            presenter.onSetColumnsCountClick();
        }
    }
    @OnClick(R.id.shortcuts_space)
    void onSetShortcutsSpaceClick(){
        presenter.onSetShortcutsSpaceClick();
    }

    @OnClick(R.id.horizontal_margin)
    void onMarginHorizontalClick(){
        presenter.onSetMarginHorizontalClick();
    }

    @OnClick(R.id.vertical_margin)
    void onMarginVerticalClick(){
        presenter.onSetMarginVerticalClick();
    }

    @OnClick(R.id.position)
    void onPositionClick(){
        presenter.onSetPositionClick();
    }
    @OnClick(R.id.stay_on_screen)
    void onStayOnScreenClick(){
        presenter.onSetStayOnScreen();
    }
}
