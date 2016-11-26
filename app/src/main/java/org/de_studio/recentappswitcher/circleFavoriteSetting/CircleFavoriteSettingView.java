package org.de_studio.recentappswitcher.circleFavoriteSetting;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerCircleFavoriteSettingComponent;
import org.de_studio.recentappswitcher.dagger.CircleFavoriteSettingModule;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class CircleFavoriteSettingView extends BaseCollectionSettingView {
    private static final String TAG = CircleFavoriteSettingView.class.getSimpleName();
    @BindView(R.id.size_text)
    TextView sizeText;
    @BindView(R.id.long_click_mode_text)
    TextView longClickModeText;




    @Override
    protected int getLayoutId() {
        return R.layout.circle_favorite_setting;
    }

    @Override
    protected void inject() {
        DaggerCircleFavoriteSettingComponent.builder()
                .appModule(new AppModule(this.getApplicationContext()))
                .circleFavoriteSettingModule(new CircleFavoriteSettingModule(this, collectionId))
                .build().inject(this);
    }



    @OnClick(R.id.long_click_mode)
    void onLongClickModeClick(){
        presenter.onLongClickModeClick();
    }


    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, CircleFavoriteSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }


    @Override
    public void getDataFromRetainFragment() {

    }

    @Override
    public void onDestroyBySystem() {

    }
}
