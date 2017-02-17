package org.de_studio.recentappswitcher.base.collectionSetting;

import android.widget.TextView;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.model.Collection;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by HaiNguyen on 2/17/17.
 */

public abstract class BaseCircleCollectionSettingView<T, P extends BaseCircleCollectionSettingPresenter> extends BaseCollectionSettingView<T,P> implements BaseCircleCollectionSettingPresenter.View {
    @BindView(R.id.size_text)
    TextView sizeDescription;
    @BindView(R.id.circle_size_description)
    TextView circleSizeDescription;
    @BindView(R.id.long_press_description)
    TextView longPressDescription;



    @Override
    public boolean isHoverOnDeleteButton(float x, float y) {
        return  y > deleteButton.getY() - deleteButton.getHeight()*2;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.circle_favorite_setting;
    }

    @Override
    public void updateCollectionInfo(Collection collection) {
        super.updateCollectionInfo(collection);
        sizeDescription.setText(String.valueOf(collection.slots.size()));
        circleSizeDescription.setText(String.valueOf(collection.radius) + " dp");


    }

    @OnClick(R.id.circle_size)
    void onCircleSizeModeClick(){
        presenter.onCircleSize();
    }

}
