package org.de_studio.recentappswitcher.recentSetting;

import android.content.Context;
import android.content.Intent;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.collectionSetting.BaseCircleCollectionSettingView;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerRecentSettingComponent;
import org.de_studio.recentappswitcher.dagger.RecentSettingModule;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class RecentSettingView extends BaseCircleCollectionSettingView<Void, RecentSettingPresenter> implements RecentSettingPresenter.View {
    private static final String TAG = RecentSettingView.class.getSimpleName();

    @Override
    protected void inject() {
        DaggerRecentSettingComponent.builder()
                .appModule(new AppModule(this))
                .recentSettingModule(new RecentSettingModule(this, collectionId))
                .build().inject(this);
    }







    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, RecentSettingView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }
}
