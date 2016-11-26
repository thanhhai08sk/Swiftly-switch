package org.de_studio.recentappswitcher.gridFavoriteSetting;

import android.content.Context;
import android.content.Intent;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerGridFavoriteSettingComponent;
import org.de_studio.recentappswitcher.dagger.GridFavoriteSettingModule;

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
        return R.layout.circle_favorite_setting;
    }
    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, GridFavoriteSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }
}
