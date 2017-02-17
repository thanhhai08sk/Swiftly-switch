package org.de_studio.recentappswitcher.base.collectionSetting;

/**
 * Created by HaiNguyen on 2/17/17.
 */

public abstract class BaseCircleCollectionSettingView<T, P extends BaseCircleCollectionSettingPresenter> extends BaseCollectionSettingView<T,P> implements BaseCircleCollectionSettingPresenter.View {


    @Override
    public boolean isHoverOnDeleteButton(float x, float y) {
        return  y > deleteButton.getY() - deleteButton.getHeight()*2;
    }

}
