package org.de_studio.recentappswitcher.blackListSetting;

import android.content.Context;
import android.content.Intent;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.BlackListSettingModule;
import org.de_studio.recentappswitcher.dagger.DaggerBlackListSettingComponent;
import org.de_studio.recentappswitcher.model.Collection;

import io.realm.RealmResults;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class BlackListSettingView extends BaseCollectionSettingView<Void, BlackListSettingPresenter> implements BlackListSettingPresenter.View{

    @Override
    protected void inject() {
        DaggerBlackListSettingComponent.builder()
                .appModule(new AppModule(this))
                .blackListSettingModule(new BlackListSettingModule(this))
                .build().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.folder_setting_view;
    }

    @Override
    public void setSpinner(RealmResults<Collection> collections, Collection currentCollection) {
        //do nothing
    }

    @Override
    public boolean isHoverOnDeleteButton(float x, float y) {
        return  y > deleteButton.getY() - deleteButton.getHeight()*2;
    }
    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, BlackListSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }

    @Override
    public PublishSubject<Void> onAddApps() {
        return null;
    }

    @Override
    public void addApps() {

    }
}
