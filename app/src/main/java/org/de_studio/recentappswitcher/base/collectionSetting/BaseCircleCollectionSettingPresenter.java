package org.de_studio.recentappswitcher.base.collectionSetting;

/**
 * Created by HaiNguyen on 2/17/17.
 */

public abstract class BaseCircleCollectionSettingPresenter<V extends BaseCircleCollectionSettingPresenter.View, M extends BaseCircleCollectionSettingModel> extends BaseCollectionSettingPresenter<V,M> {


    public BaseCircleCollectionSettingPresenter(M model) {
        super(model);
    }

    public interface View extends BaseCollectionSettingPresenter.View {

    }
}
