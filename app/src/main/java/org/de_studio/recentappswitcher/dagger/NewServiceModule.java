package org.de_studio.recentappswitcher.dagger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.edgeService.NewServiceModel;
import org.de_studio.recentappswitcher.edgeService.NewServicePresenter;
import org.de_studio.recentappswitcher.edgeService.NewServiceView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.service.EdgeSetting;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmList;

import static org.de_studio.recentappswitcher.Cons.CLOCK_PARENTS_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_HEIGHT_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_POSITION_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_WIDTH_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_HEIGHT_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_ID;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_POSITION_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_WIDTH_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.EXCLUDE_SET_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_PARENT_VIEW_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.GUIDE_COLOR_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.GUIDE_COLOR_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.LAUNCHER_PACKAGENAME_NAME;
import static org.de_studio.recentappswitcher.Cons.M_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.OLD_DEFAULT_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.SHARED_PREFERENCE_NAME;

/**
 * Created by HaiNguyen on 12/24/16.
 */
@Module
public class NewServiceModule {
    private static final String TAG = NewServiceModule.class.getSimpleName();
    NewServiceView view;
    Context context;

    public NewServiceModule(NewServiceView view, Context context) {
        this.view = view;
        this.context = context;
    }


    @Provides
    @Singleton
    NewServicePresenter presenter(NewServiceModel model
            , Realm realm
            , @Named(SHARED_PREFERENCE_NAME) SharedPreferences shared
            , @Named(EDGE_1_NAME) Edge edge1
            , @Named(EDGE_2_NAME) Edge edge2) {

        return new NewServicePresenter(model, edge1, edge2, shared.getInt(Cons.HOLD_TIME_KEY, Cons.DEFAULT_HOLD_TIME));
    }

    @Provides
    @Singleton
    @Named(EDGE_1_NAME)
    Edge edge1(Realm realm) {
        return realm.where(Edge.class).equalTo(Cons.EDGE_ID, Cons.EDGE_1_ID).findFirst();
    }

    @Provides
    @Singleton
    @Named(EDGE_2_NAME)
    Edge edge2(Realm realm) {
        return realm.where(Edge.class).equalTo(Cons.EDGE_ID, Cons.EDGE_2_ID).findFirst();
    }

    @Provides
    @Singleton
    NewServiceModel model(@Named(M_SCALE_NAME) float mScale
            , @Named(ICON_SCALE_NAME) float iconScale
            , @Named(LAUNCHER_PACKAGENAME_NAME) String launcher
            , Realm realm) {

        return new NewServiceModel(mScale, iconScale, launcher, realm);
    }


    @Provides
    @Singleton
    @Named(GRID_PARENT_VIEW_PARA_NAME)
    WindowManager.LayoutParams gridParentViewPara(){
        return new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
    }

    @Provides
    @Singleton
    @Named(CLOCK_PARENTS_VIEW_NAME)
    FrameLayout clockParentsView() {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (FrameLayout) layoutInflater.inflate(R.layout.clock, null);
    }

    @Provides
    @Singleton
    @Named(EDGE_1_VIEW_NAME)
    View edge1View(@Named(EDGE_1_NAME) Edge edge1
            , @Named(M_SCALE_NAME) float mScale
            , @Named(GUIDE_COLOR_NAME) int guideColor) {

        View edge1View = new View(context);
        edge1View.setId(Cons.EDGE_1_ID);
        if (edge1.useGuide) {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(0);
            shape.setStroke((int) (2 * mScale), guideColor);
            LayerDrawable drawable = new LayerDrawable(new Drawable[]{shape});
            switch (edge1.position / 10) {
                case 1:
                    drawable.setLayerInset(0, (int) (-5 * mScale), (int) (-5 * mScale), 0, (int) (-5 * mScale));
                    break;
                case 2:
                    drawable.setLayerInset(0, 0, (int) (-5 * mScale), (int) (-5 * mScale), (int) (-5 * mScale));
                    break;
                case 3:
                    drawable.setLayerInset(0, (int) (-5 * mScale), (int) (-5 * mScale), (int) (-5 * mScale), 0);
                    break;
            }
            edge1View.setBackground(drawable);


            int edge1Sensivite = edge1.sensitive;
            int edge1Length = edge1.length;
            int edge1HeightPxl;
            int edge1WidthPxl;


            if (Utility.rightLeftOrBottom(edge1.position) == Cons.POSITION_BOTTOM) {
                edge1HeightPxl = (int) (edge1Sensivite * mScale);
                edge1WidthPxl = (int) (edge1Length * mScale);
            } else {
                edge1HeightPxl = (int) (edge1Length * mScale);
                edge1WidthPxl = (int) (edge1Sensivite * mScale);
            }
            RelativeLayout.LayoutParams edge1ImageLayoutParams = new RelativeLayout.LayoutParams(edge1WidthPxl, edge1HeightPxl);
            edge1ImageLayoutParams.height = edge1HeightPxl;
            edge1ImageLayoutParams.width = edge1WidthPxl;
            Log.e(TAG, "edge1View: height = " + edge1HeightPxl + "\nwidth = " + edge1WidthPxl);
            edge1View.setLayoutParams(edge1ImageLayoutParams);
        }
        return edge1View;
    }
    @Provides
    @Singleton
    @Named(GUIDE_COLOR_NAME)
    int guideColor(@Named(OLD_DEFAULT_SHARED_NAME) SharedPreferences defaultShared){
        return defaultShared.getInt(EdgeSetting.GUIDE_COLOR_KEY, GUIDE_COLOR_DEFAULT);
    }


    @Provides
    @Singleton
    @Named(EDGE_2_VIEW_NAME)
    View edge2View(@Named(EDGE_2_NAME) Edge edge2
            , @Named(M_SCALE_NAME) float mScale
            , @Named(GUIDE_COLOR_NAME) int guideColor) {


        View edge2View = new View(context);
        edge2View.setId(EDGE_2_ID);
        if (edge2.useGuide) {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(0);
            shape.setStroke((int) (2 * mScale), guideColor);
            LayerDrawable drawable = new LayerDrawable(new Drawable[]{shape});
            switch (edge2.position / 10) {
                case 1:
                    drawable.setLayerInset(0, (int) (-5 * mScale), (int) (-5 * mScale), 0, (int) (-5 * mScale));
                    break;
                case 2:
                    drawable.setLayerInset(0, 0, (int) (-5 * mScale), (int) (-5 * mScale), (int) (-5 * mScale));
                    break;
                case 3:
                    drawable.setLayerInset(0, (int) (-5 * mScale), (int) (-5 * mScale), (int) (-5 * mScale), 0);
                    break;
            }
            edge2View.setBackground(drawable);

            int edge2Sensivite = edge2.sensitive;
            int edge2Length = edge2.length;
            int edge2HeightPxl;
            int edge2WidthPxl;


            if (Utility.rightLeftOrBottom(edge2.position) == Cons.POSITION_BOTTOM) {
                edge2HeightPxl = (int) (edge2Sensivite * mScale);
                edge2WidthPxl = (int) (edge2Length * mScale);
            } else {
                edge2HeightPxl = (int) (edge2Length * mScale);
                edge2WidthPxl = (int) (edge2Sensivite * mScale);
            }
            RelativeLayout.LayoutParams edge2ImageLayoutParams = new RelativeLayout.LayoutParams(edge2WidthPxl, edge2HeightPxl);
            edge2ImageLayoutParams.height = edge2HeightPxl;
            edge2ImageLayoutParams.width = edge2WidthPxl;
            edge2View.setLayoutParams(edge2ImageLayoutParams);
        }
        return edge2View;
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

    @Nullable
    @Provides
    @Singleton
    @Named(EXCLUDE_SET_NAME)
    RealmList<Item> excludeSet(Realm realm) {
        Collection blackList = realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_BLACK_LIST).findFirst();
        if (blackList != null) {
            return blackList.items;
        }
        return null;
    }

    @Provides
    @Singleton
    @Named(EDGE_1_PARA_NAME)
    WindowManager.LayoutParams edge1Para(@Named(EDGE_1_POSITION_NAME) int edge1Position
            , @Named(M_SCALE_NAME) float mScale
            , @Named(OLD_DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(EDGE_1_SHARED_NAME) SharedPreferences edge1Shared
            , @Named(EDGE_1_WIDTH_PXL_NAME) int edgeWidth
            , @Named(EDGE_1_HEIGHT_PXL_NAME) int edgeHeight) {

        return Utility.getEdgeLayoutPara(defaultShared, edge1Shared, mScale, edge1Position, edgeWidth, edgeHeight);

    }

    @Provides
    @Singleton
    @Named(EDGE_2_PARA_NAME)
    WindowManager.LayoutParams edge2Para(@Named(EDGE_2_POSITION_NAME) int edge2Position
            , @Named(M_SCALE_NAME) float mScale
            , @Named(OLD_DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(EDGE_2_SHARED_NAME) SharedPreferences edge2Shared
            , @Named(EDGE_2_WIDTH_PXL_NAME) int edgeWidth
            , @Named(EDGE_2_HEIGHT_PXL_NAME) int edgeHeight) {

        return Utility.getEdgeLayoutPara(defaultShared, edge2Shared, mScale, edge2Position, edgeWidth, edgeHeight);
    }

    @Provides
    @Singleton
    WindowManager windowManager(){
        return (WindowManager) view.getSystemService(Context.WINDOW_SERVICE);
    }

    @Provides
    @Singleton
    @Named(M_SCALE_NAME)
    float mScale(){
        return context.getResources().getDisplayMetrics().density;
    }

    @Provides
    @Singleton
    @Named(ICON_SCALE_NAME)
    float iconScale(@Named(OLD_DEFAULT_SHARED_NAME) SharedPreferences defaultShared){
        return defaultShared.getFloat(ICON_SCALE,1f);
    }




}
