package org.de_studio.recentappswitcher.recentSetting;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerRecentSettingComponent;
import org.de_studio.recentappswitcher.dagger.RecentSettingModule;
import org.de_studio.recentappswitcher.model.Collection;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class RecentSettingView extends BaseCollectionSettingView<Void, RecentSettingPresenter> implements RecentSettingPresenter.View {
    private static final String TAG = RecentSettingView.class.getSimpleName();
    @BindView(R.id.size_text)
    TextView sizeText;
    @BindView(R.id.circle_size_description)
    TextView circleSizeDescription;

    @Override
    protected void inject() {
        DaggerRecentSettingComponent.builder()
                .appModule(new AppModule(this))
                .recentSettingModule(new RecentSettingModule(this, collectionId))
                .build().inject(this);
    }

    @Override
    public void updateCollectionInfo(Collection collection) {
        super.updateCollectionInfo(collection);
        sizeText.setText(String.valueOf(collection.slots.size()));
        circleSizeDescription.setText(String.valueOf(collection.radius) + " dp");

    }

    @Override
    public boolean isHoverOnDeleteButton(float x, float y) {
        return  y > deleteButton.getY() - deleteButton.getHeight()*2;
    }



    @OnClick(R.id.circle_size)
    void onCircleSizeModeClick(){
        presenter.onCircleSize();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.circle_favorite_setting;
    }

    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, RecentSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }
}
