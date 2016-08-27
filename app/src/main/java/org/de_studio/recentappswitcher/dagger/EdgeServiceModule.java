package org.de_studio.recentappswitcher.dagger;

import android.content.Context;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.edgeService.EdgeServiceModel;
import org.de_studio.recentappswitcher.edgeService.EdgeServicePresenter;
import org.de_studio.recentappswitcher.edgeService.EdgeServiceView;
import org.de_studio.recentappswitcher.service.FavoriteShortcutAdapter;

import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

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
            , @Named(Cons.LAUNCHER_PACKAGENAME_NAME) String laucherPackageName
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

}
