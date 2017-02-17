package org.de_studio.recentappswitcher.circleFavoriteSetting;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCircleCollectionSettingView;
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

public class CircleFavoriteSettingView extends BaseCircleCollectionSettingView<Void, CircleFavoriteSettingPresenter> implements CircleFavoriteSettingPresenter.View {
    private static final String TAG = CircleFavoriteSettingView.class.getSimpleName();
    @BindView(R.id.size_text)
    TextView sizeText;
    @BindView(R.id.circle_size_description)
    TextView circleSizeDescription;


    @Override
    public void updateCollectionInfo(Collection collection) {
        super.updateCollectionInfo(collection);
        sizeText.setText(String.valueOf(collection.slots.size()));
        circleSizeDescription.setText(String.valueOf(collection.radius) + " dp");
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




    @OnClick(R.id.circle_size)
    void onCircleSizeModeClick(){
        presenter.onCircleSize();
    }


    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, CircleFavoriteSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }

}
