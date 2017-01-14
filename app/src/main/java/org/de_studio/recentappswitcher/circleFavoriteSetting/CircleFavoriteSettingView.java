package org.de_studio.recentappswitcher.circleFavoriteSetting;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.CircleFavoriteSettingModule;
import org.de_studio.recentappswitcher.dagger.DaggerCircleFavoriteSettingComponent;
import org.de_studio.recentappswitcher.model.Collection;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class CircleFavoriteSettingView extends BaseCollectionSettingView<Void, CircleFavoriteSettingPresenter> implements CircleFavoriteSettingPresenter.View {
    private static final String TAG = CircleFavoriteSettingView.class.getSimpleName();
    @BindView(R.id.size_text)
    TextView sizeText;
    @BindView(R.id.long_click_mode_text)
    TextView longClickModeText;


    @Override
    public void updateCollectionInfo(Collection collection) {
        super.updateCollectionInfo(collection);
        sizeText.setText(String.valueOf(collection.slots.size()));
    }

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

    @Override
    public boolean isHoverOnDeleteButton(float x, float y) {
        return  y > deleteButton.getY() - deleteButton.getHeight()*2;
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

}
