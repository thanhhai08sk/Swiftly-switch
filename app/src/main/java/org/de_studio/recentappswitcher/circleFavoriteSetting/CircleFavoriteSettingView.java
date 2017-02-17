package org.de_studio.recentappswitcher.circleFavoriteSetting;

import android.content.Context;
import android.content.Intent;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCircleCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.CircleFavoriteSettingModule;
import org.de_studio.recentappswitcher.dagger.DaggerCircleFavoriteSettingComponent;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class CircleFavoriteSettingView extends BaseCircleCollectionSettingView<Void, CircleFavoriteSettingPresenter> implements CircleFavoriteSettingPresenter.View {
    private static final String TAG = CircleFavoriteSettingView.class.getSimpleName();






    @Override
    protected void inject() {
        DaggerCircleFavoriteSettingComponent.builder()
                .appModule(new AppModule(this.getApplicationContext()))
                .circleFavoriteSettingModule(new CircleFavoriteSettingModule(this, collectionId))
                .build().inject(this);
    }



    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, CircleFavoriteSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }

}
