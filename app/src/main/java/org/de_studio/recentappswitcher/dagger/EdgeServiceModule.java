package org.de_studio.recentappswitcher.dagger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.edgeService.EdgeServiceModel;
import org.de_studio.recentappswitcher.edgeService.EdgeServicePresenter;
import org.de_studio.recentappswitcher.edgeService.EdgeServiceView;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.FavoriteShortcutAdapter;

import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_DP_NAME;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_KEY;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_GAP_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.GRID_GAP_KEY;
import static org.de_studio.recentappswitcher.Cons.GRID_GAP_NAME;
import static org.de_studio.recentappswitcher.Cons.HALF_ICON_WIDTH_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_FREE_AND_OUT_OF_TRIAL_NAME;
import static org.de_studio.recentappswitcher.Cons.LAUNCHER_PACKAGENAME_NAME;
import static org.de_studio.recentappswitcher.Cons.M_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.RAD_ICON_DEFAULT_DP;

/**
 * Created by HaiNguyen on 8/27/16.
 */
@Module
@Singleton
public class EdgeServiceModule {
    EdgeServiceView view;
    Context context;

    public EdgeServiceModule(EdgeServiceView view) {
        this.view = view;
        context = view.getApplicationContext();
    }

    @Provides
    @Singleton
    EdgeServiceView view() {
        return view;
    }

    @Provides
    @Singleton
    FavoriteShortcutAdapter gridAdapter() {
        return new FavoriteShortcutAdapter(context);
    }

    @Provides
    @Singleton
    EdgeServicePresenter presenter(EdgeServiceModel model) {
        return new EdgeServicePresenter(model, view);
    }

    @Provides
    @Singleton
    EdgeServiceModel model(Set<String> blackListSet, @Named(Cons.PIN_REALM_NAME) Realm pinRealm
            , @Named(LAUNCHER_PACKAGENAME_NAME) String laucherPackageName
            , @Named(Cons.IS_FREE_AND_OUT_OF_TRIAL_NAME) boolean isFreeAndOutOfTrial
            , @Named(Cons.M_SCALE_NAME) float mScale
            , @Named(Cons.HALF_ICON_WIDTH_PXL_NAME) float halfIconWidthPxl
            , @Named(Cons.CIRCLE_SIZE_DP_NAME) int circleSizeDp
            , @Named(Cons.ICON_SCALE_NAME) float iconScale
            , @Named(Cons.GRID_GAP_NAME) int gridGap) {

        return new EdgeServiceModel(blackListSet, pinRealm, laucherPackageName
                , isFreeAndOutOfTrial, mScale, halfIconWidthPxl
                , circleSizeDp, iconScale, gridGap);
    }
    @Provides
    @Singleton
    @Named(LAUNCHER_PACKAGENAME_NAME)
    String launcherPackageName(){
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = context.getPackageManager().resolveActivity(launcherIntent, 0);
        if (res.activityInfo != null) {
            return res.activityInfo.packageName;
        } else return  "";
    }

    @Provides
    @Singleton
    @Named(IS_FREE_AND_OUT_OF_TRIAL_NAME)
    boolean isFreeAndOutOfTrial(@Named(Cons.DEFAULT_SHARED_NAME) SharedPreferences defaultShared) {

        return context.getPackageName().equals(Cons.FREE_VERSION_PACKAGE_NAME)
                && System.currentTimeMillis() - defaultShared.getLong(EdgeSetting.BEGIN_DAY_KEY, System.currentTimeMillis()) > MainActivity.trialTime;
    }

    @Provides
    @Singleton
    @Named(M_SCALE_NAME)
    float mScale(){
        return context.getResources().getDisplayMetrics().density;
    }

    @Provides
    @Singleton
    @Named(HALF_ICON_WIDTH_PXL_NAME)
    float iconWidth(@Named(ICON_SCALE_NAME) float iconScale
            , @Named(M_SCALE_NAME) float mScale) {

        return iconScale * RAD_ICON_DEFAULT_DP * mScale;
    }


    @Provides
    @Singleton
    @Named(CIRCLE_SIZE_DP_NAME)
    int circleSizeDp(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared) {
        return defaultShared.getInt(CIRCLE_SIZE_KEY, CIRCLE_SIZE_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(ICON_SCALE_NAME)
    float iconScale(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared){
            return defaultShared.getFloat(ICON_SCALE,1f);
    }

    @Provides
    @Singleton
    @Named(GRID_GAP_NAME)
    int gridGap(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared) {
        return defaultShared.getInt(GRID_GAP_KEY, GRID_GAP_DEFAULT);
    }





    

}
