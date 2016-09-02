package org.de_studio.recentappswitcher.dagger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.edgeService.EdgeServiceModel;
import org.de_studio.recentappswitcher.edgeService.EdgeServicePresenter;
import org.de_studio.recentappswitcher.edgeService.EdgeServiceView;
import org.de_studio.recentappswitcher.favoriteShortcut.CircleFavoriteAdapter;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.ExpandStatusBarView;
import org.de_studio.recentappswitcher.service.FavoriteShortcutAdapter;
import org.de_studio.recentappswitcher.service.MyImageView;

import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

import static org.de_studio.recentappswitcher.Cons.BACKGROUND_COLOR_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_COLOR_NAME;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_FRAME_NAME;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_FRAME_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_AND_QUICK_ACTION_GAP;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_PARENTS_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SHORTCUT_VIEW_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_DP_NAME;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_KEY;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_FAVORITE_GRID_PADDING_HORIZONTAL;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_FAVORITE_GRID_PADDING_VERTICAL;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_ICON_GAP_IN_GRID;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_ICON_SIZE;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_OFFSET_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_POSITION_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_QUICK_ACTION_VIEWS_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_SENSITIVE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_OFFSET_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_POSITION_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_QUICK_ACTION_VIEWS_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_SENSITIVE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_POSITIONS_ARRAY_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_ADAPTER_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_PADDING_HORIZONTAL_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_PADDING_VERTICAL_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.FOLDER_GRID_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_GAP_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.GRID_GAP_KEY;
import static org.de_studio.recentappswitcher.Cons.GRID_GAP_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_PARENTS_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_PARENT_VIEW_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.GUIDE_COLOR_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.GUIDE_COLOR_NAME;
import static org.de_studio.recentappswitcher.Cons.HALF_ICON_WIDTH_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_FREE_AND_OUT_OF_TRIAL_NAME;
import static org.de_studio.recentappswitcher.Cons.LAUNCHER_PACKAGENAME_NAME;
import static org.de_studio.recentappswitcher.Cons.M_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.OVAL_OFFSET;
import static org.de_studio.recentappswitcher.Cons.OVAL_RADIUS_PLUS;
import static org.de_studio.recentappswitcher.Cons.QUICK_ACTION_VIEW_RADIUS_NAME;
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
    @Named(CIRCLE_SHORTCUT_VIEW_PARA_NAME)
    WindowManager.LayoutParams circlePara(){

        WindowManager.LayoutParams circleShortcutsViewPara = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
        circleShortcutsViewPara.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
        return circleShortcutsViewPara;
    }

    @Provides
    @Singleton
    @Named(GUIDE_COLOR_NAME)
    int guideColor(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared){
        return defaultShared.getInt(EdgeSetting.GUIDE_COLOR_KEY, GUIDE_COLOR_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(CIRCLE_PARENTS_VIEW_NAME)
    FrameLayout circleParentView(){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (FrameLayout) layoutInflater.inflate(R.layout.items, null);
    }

    @Provides
    @Singleton
    @Named(GRID_PARENTS_VIEW_NAME)
    FrameLayout gridParentsView(){
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return  (FrameLayout) layoutInflater.inflate(R.layout.grid_shortcut, null);
    }

    @Provides
    @Singleton
    @Named(FAVORITE_GRID_VIEW_NAME)
    GridView favoriteGridView(@Named(GRID_PARENTS_VIEW_NAME) FrameLayout parent
            , @Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(M_SCALE_NAME) float mScale
            , @Named(FAVORITE_GRID_ADAPTER_NAME) FavoriteShortcutAdapter adapter
            , @Named(ICON_SCALE_NAME) float mIconScale) {

        GridView gridView = (GridView) parent.findViewById(R.id.edge_shortcut_grid_view);
        ViewGroup.LayoutParams gridParams = gridView.getLayoutParams();
        int gridRow = defaultShared.getInt(EdgeSetting.NUM_OF_GRID_ROW_KEY, 5);
        int gridColumn = defaultShared.getInt(EdgeSetting.NUM_OF_GRID_COLUMN_KEY, 4);
        int gridGap = defaultShared.getInt(EdgeSetting.GAP_OF_SHORTCUT_KEY, 5);
        gridView.setVerticalSpacing((int) (gridGap * mScale));
        gridView.setNumColumns(gridColumn);
        gridView.setGravity(Gravity.CENTER);

        float gridWide = (int) (mScale *  (((DEFAULT_ICON_SIZE * mIconScale) + DEFAULT_ICON_GAP_IN_GRID) * gridColumn + gridGap * (gridColumn - 1)));
        float gridTall = (int) (mScale *  (((DEFAULT_ICON_SIZE * mIconScale) + DEFAULT_ICON_GAP_IN_GRID) * gridRow + gridGap * (gridRow - 1)));
        gridParams.height = (int) gridTall;
        gridParams.width = (int) gridWide;
        gridView.setLayoutParams(gridParams);
        gridView.setAdapter(adapter);
        return gridView;
    }

    @Provides
    @Singleton
    @Named(FOLDER_GRID_VIEW_NAME)
    GridView folderGridView(@Named(GRID_PARENTS_VIEW_NAME) FrameLayout parent){
        return (GridView) parent.findViewById(R.id.folder_grid);
    }



    @Provides
    @Singleton
    MyImageView[] circleIcons(@Named(CIRCLE_PARENTS_VIEW_NAME) FrameLayout parents
            , @Named(ICON_SCALE_NAME) float iconScale
            , @Named(M_SCALE_NAME) float mScale) {

        MyImageView[] circleIcons = new MyImageView[6];
        circleIcons[0] = (MyImageView) parents.findViewById(R.id.item_0);
        circleIcons[1] = (MyImageView) parents.findViewById(R.id.item_1);
        circleIcons[2] = (MyImageView) parents.findViewById(R.id.item_2);
        circleIcons[3] = (MyImageView) parents.findViewById(R.id.item_3);
        circleIcons[4] = (MyImageView) parents.findViewById(R.id.item_4);
        circleIcons[5] = (MyImageView) parents.findViewById(R.id.item_5);

        FrameLayout.LayoutParams sampleParas1 = new FrameLayout.LayoutParams(circleIcons[0].getLayoutParams());
        for (MyImageView image : circleIcons) {
            sampleParas1.height = (int) (48 * iconScale * mScale);
            sampleParas1.width = (int) (48 * iconScale * mScale);
            image.setLayoutParams(sampleParas1);
        }
        return circleIcons;
    }


    @Provides
    @Singleton
    @Named(EDGE_1_POSITION_NAME)
    int edge1Position(@Named(EDGE_1_SHARED_NAME) SharedPreferences shared
            , @Named(EDGE_POSITIONS_ARRAY_NAME) String[] edgePositionsArray) {
        return Utility.getPositionIntFromString(shared.getString(EdgeSetting.EDGE_POSITION_KEY, edgePositionsArray[1]), context); // default =1
    }

    @Provides
    @Singleton
    @Named(EDGE_2_POSITION_NAME)
    int edge2Position(@Named(EDGE_2_SHARED_NAME) SharedPreferences shared
            , @Named(EDGE_POSITIONS_ARRAY_NAME) String[] edgePositionsArray) {
        return Utility.getPositionIntFromString(shared.getString(EdgeSetting.EDGE_POSITION_KEY, edgePositionsArray[5]), context);
    }


    @Provides
    @Singleton
    @Named(EDGE_POSITIONS_ARRAY_NAME)
    String[] edgePositionsArray(){
            return  context.getResources().getStringArray(R.array.edge_positions_array);
    }

    @Provides
    @Singleton
    @Named(EDGE_1_VIEW_NAME)
    View edge1View(@Named(EDGE_1_POSITION_NAME) int edge1Position
            , @Named(M_SCALE_NAME) float mScale
            , @Named(EDGE_1_SHARED_NAME) SharedPreferences edge1Shared
            , @Named(GUIDE_COLOR_NAME) int guideColor) {

        View edge1View = new View(context);
        edge1View.setTag(Cons.TAG_EDGE_1);
        if (edge1Shared.getBoolean(EdgeSetting.USE_GUIDE_KEY, true)) {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(0);
            shape.setStroke((int) (2 * mScale), guideColor);
            LayerDrawable drawable = new LayerDrawable(new Drawable[]{shape});
            switch (edge1Position / 10) {
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


            int edge1Sensivite = edge1Shared.getInt(Cons.EDGE_SENSIIVE_KEY, Cons.EDGE_SENSITIVE_DEFAULT);
            int edge1Length = edge1Shared.getInt(Cons.EDGE_LENGTH_KEY, Cons.EDGE_LENGTH_DEFAULT);
            int edge1HeightPxl;
            int edge1WidthPxl;


            if (Utility.rightLeftOrBottom(edge1Position) == Cons.POSITION_BOTTOM) {
                edge1HeightPxl = (int) (edge1Sensivite * mScale);
                edge1WidthPxl = (int) (edge1Length * mScale);
            } else {
                edge1HeightPxl = (int) (edge1Length * mScale);
                edge1WidthPxl = (int) (edge1Sensivite * mScale);
            }
            RelativeLayout.LayoutParams edge1ImageLayoutParams = new RelativeLayout.LayoutParams(edge1WidthPxl, edge1HeightPxl);
            edge1ImageLayoutParams.height = edge1HeightPxl;
            edge1ImageLayoutParams.width = edge1WidthPxl;
            edge1View.setLayoutParams(edge1ImageLayoutParams);
        }
        return edge1View;
    }


    @Provides
    @Singleton
    @Named(EDGE_2_VIEW_NAME)
    View edge2View(@Named(EDGE_2_POSITION_NAME) int edge2Position
            , @Named(M_SCALE_NAME) float mScale
            , @Named(EDGE_2_SHARED_NAME) SharedPreferences edge2Shared
            , @Named(GUIDE_COLOR_NAME) int guideColor) {


        View edge2View = new View(context);
        edge2View.setTag(Cons.TAG_EDGE_2);
        if (edge2Shared.getBoolean(EdgeSetting.USE_GUIDE_KEY, true)) {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(0);
            shape.setStroke((int) (2 * mScale), guideColor);
            LayerDrawable drawable = new LayerDrawable(new Drawable[]{shape});
            switch (edge2Position / 10) {
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

            int edge2Sensivite = edge2Shared.getInt(Cons.EDGE_SENSIIVE_KEY,Cons.EDGE_SENSITIVE_DEFAULT);
            int edge2Length = edge2Shared.getInt(Cons.EDGE_LENGTH_KEY,Cons.EDGE_LENGTH_DEFAULT);
            int edge2HeightPxl;
            int edge2WidthPxl;


            if (Utility.rightLeftOrBottom(edge2Position) == Cons.POSITION_BOTTOM) {
                edge2HeightPxl = (int) (edge2Sensivite * mScale);
                edge2WidthPxl = (int) (edge2Length * mScale);
            } else {
                edge2HeightPxl = (int) (edge2Length * mScale);
                edge2WidthPxl = (int) (edge2Sensivite * mScale);
            }
            RelativeLayout.LayoutParams edge2ImageLayoutParams = new RelativeLayout.LayoutParams(edge2WidthPxl,edge2HeightPxl);
            edge2ImageLayoutParams.height = edge2HeightPxl;
            edge2ImageLayoutParams.width = edge2WidthPxl;
            edge2View.setLayoutParams(edge2ImageLayoutParams);
        }
        return edge2View;
    }


    @Provides
    @Singleton
    @Named(BACKGROUND_COLOR_NAME)
    int backgroundColor(@Named(DEFAULT_SHARED_NAME)SharedPreferences shared){
        return shared.getInt(Cons.BACKGROUND_COLOR_KEY, BACKGROUND_COLOR_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(BACKGROUND_FRAME_NAME)
    FrameLayout backgroundFrame(@Named(BACKGROUND_COLOR_NAME) int backgroundColor) {

        FrameLayout backgroundFrame;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        backgroundFrame = (FrameLayout) layoutInflater.inflate(R.layout.background, null);
        backgroundFrame.setBackgroundColor(backgroundColor);
        return backgroundFrame;

    }

    @Provides
    @Singleton
    @Named(BACKGROUND_FRAME_PARA_NAME)
    WindowManager.LayoutParams backgroundFramePara(){
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
    @Named(EDGE_1_PARA_NAME)
    WindowManager.LayoutParams edge1Para(@Named(EDGE_1_POSITION_NAME) int edge1Position
            , @Named(M_SCALE_NAME) float mScale
            , @Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(EDGE_1_SHARED_NAME) SharedPreferences edge1Shared) {
        return Utility.getEdgeLayoutPara(defaultShared, edge1Shared, mScale, edge1Position);

    }

    @Provides
    @Singleton
    @Named(EDGE_2_PARA_NAME)
    WindowManager.LayoutParams edge2Para(@Named(EDGE_2_POSITION_NAME) int edge2Position
            , @Named(M_SCALE_NAME) float mScale
            , @Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(EDGE_2_SHARED_NAME) SharedPreferences edge2Shared) {
        return Utility.getEdgeLayoutPara(defaultShared, edge2Shared, mScale, edge2Position);
    }


    @Provides
    @Singleton
    WindowManager windowManager(){
        return (WindowManager) view.getSystemService(Context.WINDOW_SERVICE);
    }

    @Provides
    @Singleton
    LayoutInflater layoutInflater(){
        return (LayoutInflater) view.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Provides
    @Singleton
    @Named(EDGE_1_SENSITIVE_NAME)
    int edge1Sensitive(@Named(EDGE_1_SHARED_NAME) SharedPreferences edge1Shared){
        return edge1Shared.getInt(Cons.EDGE_SENSIIVE_KEY, Cons.EDGE_SENSITIVE_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(EDGE_2_SENSITIVE_NAME)
    int edge2Sensitive(@Named(EDGE_2_SHARED_NAME) SharedPreferences edge2Shared){
        return edge2Shared.getInt(Cons.EDGE_SENSIIVE_KEY, Cons.EDGE_SENSITIVE_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(EDGE_1_OFFSET_NAME)
    int edge1offset(@Named(EDGE_1_SHARED_NAME) SharedPreferences edge1Shared){
        return edge1Shared.getInt(Cons.EDGE_OFFSET_KEY, Cons.EDGE_OFFSET_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(EDGE_2_OFFSET_NAME)
    int edge2Offset(@Named(EDGE_2_SHARED_NAME) SharedPreferences edge2Shared){
        return edge2Shared.getInt(Cons.EDGE_OFFSET_KEY, Cons.EDGE_OFFSET_DEFAULT);
    }

    @Provides
    @Singleton
    FavoriteShortcutAdapter GridFavoriteAdapter(){
        return new FavoriteShortcutAdapter(context);
    }

    @Provides
    @Singleton
    CircleFavoriteAdapter circleFavoriteAdapter(){
        return new CircleFavoriteAdapter(context);
    }

    @Provides
    @Singleton
    @Named(FAVORITE_GRID_PADDING_HORIZONTAL_NAME)
    int favoritePaddingHorizontal(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared){
        return defaultShared.getInt(Cons.FAVORITE_GRID_PADDING_HORIZONTAL_KEY, DEFAULT_FAVORITE_GRID_PADDING_HORIZONTAL);
    }

    @Provides
    @Singleton
    @Named(FAVORITE_GRID_PADDING_VERTICAL_NAME)
    int favoritePaddingVertical(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared){
        return defaultShared.getInt(Cons.FAVORITE_GRID_PADDING_VERTICAL_NAME, DEFAULT_FAVORITE_GRID_PADDING_VERTICAL);
    }

    @Provides
    @Singleton
    @Named(EDGE_1_QUICK_ACTION_VIEWS_NAME)
    ExpandStatusBarView[] edge1QuickActionViews(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(QUICK_ACTION_VIEW_RADIUS_NAME) int quickActionViewRadius
            , @Named(M_SCALE_NAME) float mScale
            , @Named(EDGE_1_POSITION_NAME) int edge1Position) {

        ExpandStatusBarView[] quickActionViews = new ExpandStatusBarView[4];
        for (int i = 0; i < quickActionViews.length; i++) {
            quickActionViews[i] = new ExpandStatusBarView(context, quickActionViewRadius, (int) (OVAL_OFFSET * mScale), edge1Position, i + 1);
        }
        return quickActionViews;
    }

    @Provides
    @Singleton
    @Named(EDGE_2_QUICK_ACTION_VIEWS_NAME)
    ExpandStatusBarView[] edge2QuickActionViews(@Named(QUICK_ACTION_VIEW_RADIUS_NAME) int quickActionViewRadius
            , @Named(M_SCALE_NAME) float mScale
            , @Named(EDGE_2_POSITION_NAME) int edge2Position) {
        ExpandStatusBarView[] quickActionViews = new ExpandStatusBarView[4];
        for (int i = 0; i < quickActionViews.length; i++) {
            quickActionViews[i] = new ExpandStatusBarView(context, quickActionViewRadius, (int) (OVAL_OFFSET * mScale), edge2Position, i + 1);
        }
        return quickActionViews;
    }

    @Provides
    @Singleton
    @Named(QUICK_ACTION_VIEW_RADIUS_NAME)
    int quickActionViewRadius(@Named(CIRCLE_SIZE_DP_NAME) int circleSize
            , @Named(M_SCALE_NAME) float mScale) {
        return (int) ((circleSize + CIRCLE_AND_QUICK_ACTION_GAP + OVAL_RADIUS_PLUS) * mScale);
    }

    @Provides
    @Singleton
    @Named(CIRCLE_SIZE_PXL_NAME)
    float circleSizePxl(@Named(CIRCLE_SIZE_DP_NAME) int circleSize
            , @Named(M_SCALE_NAME) float mScale) {
        return mScale * circleSize;
    }













    

}
