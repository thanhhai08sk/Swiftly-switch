package org.de_studio.recentappswitcher.gridFavoriteSetting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerGridFavoriteSettingComponent;
import org.de_studio.recentappswitcher.dagger.GridFavoriteSettingModule;
import org.de_studio.recentappswitcher.model.Collection;

import butterknife.OnClick;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public class GridFavoriteSettingView extends BaseCollectionSettingView {
    private static final String TAG = GridFavoriteSettingView.class.getSimpleName();


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
