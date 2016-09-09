package org.de_studio.recentappswitcher.dagger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Vibrator;
import android.util.Log;
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
import org.de_studio.recentappswitcher.edgeService.EdgesServiceModel;
import org.de_studio.recentappswitcher.edgeService.EdgeServicePresenter;
import org.de_studio.recentappswitcher.edgeService.EdgeServiceView;
import org.de_studio.recentappswitcher.favoriteShortcut.CircleFavoriteAdapter;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.ExpandStatusBarView;
import org.de_studio.recentappswitcher.service.FavoriteShortcutAdapter;
import org.de_studio.recentappswitcher.service.MyImageView;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

import static org.de_studio.recentappswitcher.Cons.ANIMATION_TIME_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.ANIMATION_TIME_NAME;
import static org.de_studio.recentappswitcher.Cons.ANI_TIME_KEY;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_COLOR_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_COLOR_NAME;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_FRAME_NAME;
import static org.de_studio.recentappswitcher.Cons.BACKGROUND_FRAME_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_AND_QUICK_ACTION_GAP;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_PARENTS_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SHORTCUT_VIEW_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_KEY;
import static org.de_studio.recentappswitcher.Cons.CIRCLE_SIZE_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.CLOCK_PARENTS_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.CLOCK_PARENTS_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_FAVORITE_GRID_PADDING_HORIZONTAL;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_FAVORITE_GRID_PADDING_VERTICAL;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_ICON_GAP_IN_GRID;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_ICON_SIZE;
import static org.de_studio.recentappswitcher.Cons.DEFAULT_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_HEIGHT_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_MODE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_OFFSET_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_POSITION_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_QUICK_ACTION_VIEWS_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_SENSITIVE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_1_WIDTH_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_HEIGHT_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_ID;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_MODE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_OFFSET_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_POSITION_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_QUICK_ACTION_VIEWS_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_SENSITIVE_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_2_WIDTH_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.EDGE_POSITIONS_ARRAY_NAME;
import static org.de_studio.recentappswitcher.Cons.EXCLUDE_SET_NAME;
import static org.de_studio.recentappswitcher.Cons.EXCLUDE_SHARED_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_ADAPTER_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_PADDING_HORIZONTAL_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_PADDING_VERTICAL_NAME;
import static org.de_studio.recentappswitcher.Cons.FAVORITE_GRID_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.FOLDER_GRID_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_GAP_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.GRID_GAP_KEY;
import static org.de_studio.recentappswitcher.Cons.GRID_GAP_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_HEIGHT_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_NUMBER_COLUMNS_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_NUMBER_ROWS_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_PARENTS_VIEW_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_PARENT_VIEW_PARA_NAME;
import static org.de_studio.recentappswitcher.Cons.GRID_WIDTH_NAME;
import static org.de_studio.recentappswitcher.Cons.GUIDE_COLOR_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.GUIDE_COLOR_NAME;
import static org.de_studio.recentappswitcher.Cons.HALF_ICON_WIDTH_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.HOLD_TIME_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.HOLD_TIME_ENABLE_NAME;
import static org.de_studio.recentappswitcher.Cons.HOLD_TIME_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE;
import static org.de_studio.recentappswitcher.Cons.ICON_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.ICON_SIZE_PXL_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_EDGE_1_ON_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_EDGE_2_ON_NAME;
import static org.de_studio.recentappswitcher.Cons.IS_FREE_AND_OUT_OF_TRIAL_NAME;
import static org.de_studio.recentappswitcher.Cons.LAUNCHER_PACKAGENAME_NAME;
import static org.de_studio.recentappswitcher.Cons.MODE_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.MODE_ONLY_FAVORITE;
import static org.de_studio.recentappswitcher.Cons.M_SCALE_NAME;
import static org.de_studio.recentappswitcher.Cons.OVAL_OFFSET;
import static org.de_studio.recentappswitcher.Cons.OVAL_RADIUS_PLUS;
import static org.de_studio.recentappswitcher.Cons.QUICK_ACTION_VIEW_RADIUS_NAME;
import static org.de_studio.recentappswitcher.Cons.QUICK_ACTION_WITH_INSTANT_FAVORITE_NAME;
import static org.de_studio.recentappswitcher.Cons.RAD_ICON_DEFAULT_DP;
import static org.de_studio.recentappswitcher.Cons.USE_ACTION_DOW_VIBRATE_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_ACTION_MOVE_VIBRATE_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_ANIMATION_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.USE_ANIMATION_KEY;
import static org.de_studio.recentappswitcher.Cons.USE_ANIMATION_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_CLOCK_NAME;
import static org.de_studio.recentappswitcher.Cons.USE_INSTANT_FAVORITE_NAME;
import static org.de_studio.recentappswitcher.Cons.VIBRATE_DURATION_NAME;
import static org.de_studio.recentappswitcher.Cons.VIBRATION_DURATION_DEFAULT;
import static org.de_studio.recentappswitcher.Cons.VIBRATION_DURATION_KEY;

/**
 * Created by HaiNguyen on 8/27/16.
 */
@Module
@Singleton
public class EdgeServiceModule {
    private static final String TAG = EdgeServiceModule.class.getSimpleName();
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
    @Named(FAVORITE_GRID_ADAPTER_NAME)
    @Singleton
    FavoriteShortcutAdapter gridAdapter() {
        return new FavoriteShortcutAdapter(context);
    }

    @Provides
    @Singleton
    EdgeServicePresenter presenter(EdgesServiceModel model) {
        return new EdgeServicePresenter(model, view);
    }

    @Provides
    @Singleton
    EdgesServiceModel model(@Named(EXCLUDE_SET_NAME)Set<String> excludeSet , @Named(Cons.PIN_REALM_NAME) Realm pinRealm
            , @Named(LAUNCHER_PACKAGENAME_NAME) String laucherPackageName
            , @Named(Cons.IS_FREE_AND_OUT_OF_TRIAL_NAME) boolean isFreeAndOutOfTrial
            , @Named(Cons.M_SCALE_NAME) float mScale
            , @Named(Cons.HALF_ICON_WIDTH_PXL_NAME) float halfIconWidthPxl
            , @Named(Cons.CIRCLE_SIZE_PXL_NAME) float circleSizePxl
            , @Named(Cons.ICON_SCALE_NAME) float iconScale
            , @Named(Cons.GRID_GAP_NAME) int gridGap) {

        return new EdgesServiceModel(excludeSet, pinRealm, laucherPackageName
                , isFreeAndOutOfTrial, mScale, halfIconWidthPxl
                , circleSizePxl, iconScale, gridGap);
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
    @Named(CIRCLE_SIZE_PXL_NAME)
    float circleSizeDp(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(M_SCALE_NAME) float mScale) {

        return defaultShared.getInt(CIRCLE_SIZE_KEY, CIRCLE_SIZE_DEFAULT) * mScale;
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
    @Named(GRID_HEIGHT_NAME)
    float gridHeight(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(M_SCALE_NAME) float mScale
            , @Named(ICON_SCALE_NAME) float iconScale) {
        int gridRow = defaultShared.getInt(EdgeSetting.NUM_OF_GRID_ROW_KEY, 5);
        int gridGap = defaultShared.getInt(EdgeSetting.GAP_OF_SHORTCUT_KEY, 5);
        return mScale *  (((DEFAULT_ICON_SIZE * iconScale) + DEFAULT_ICON_GAP_IN_GRID) * gridRow + gridGap * (gridRow - 1));
    }

    @Provides
    @Singleton
    @Named(GRID_WIDTH_NAME)
    float gridWidth(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(M_SCALE_NAME) float mScale
            , @Named(ICON_SCALE_NAME) float iconScale) {
        int gridColumn = defaultShared.getInt(Cons.NUM_OF_GRID_COLUMN_KEY, 4);
        int gridGap = defaultShared.getInt(EdgeSetting.GAP_OF_SHORTCUT_KEY, 5);
        return  mScale * (((DEFAULT_ICON_SIZE * iconScale) + DEFAULT_ICON_GAP_IN_GRID) * gridColumn + gridGap * (gridColumn - 1));

    }

    @Provides
    @Singleton
    @Named(GRID_NUMBER_COLUMNS_NAME)
    int gridColumns(@Named(DEFAULT_SHARED_NAME) SharedPreferences shared) {
        return shared.getInt(Cons.NUM_OF_GRID_COLUMN_KEY, 4);
    }

    @Provides
    @Singleton
    @Named(GRID_NUMBER_ROWS_NAME)
    int gridRows(@Named(DEFAULT_SHARED_NAME) SharedPreferences shared) {
        return shared.getInt(Cons.NUM_OF_GRID_ROW_KEY, 5);
    }


    @Provides
    @Singleton
    @Named(FOLDER_GRID_VIEW_NAME)
    GridView folderGridView(@Named(GRID_PARENTS_VIEW_NAME) FrameLayout parent){
        return (GridView) parent.findViewById(R.id.folder_grid);
    }

    @Provides
    @Singleton
    @Named(ICON_SIZE_PXL_NAME)
    float iconSizePxl(@Named(M_SCALE_NAME) float mScale
            , @Named(ICON_SCALE_NAME) float iconScale) {
        return DEFAULT_ICON_SIZE * mScale * iconScale;
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
        edge1View.setId(Cons.EDGE_1_ID);
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
            Log.e(TAG, "edge1View: height = " + edge1HeightPxl + "\nwidth = " + edge1WidthPxl);
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
        edge2View.setId(EDGE_2_ID);
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
            , @Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared
            , @Named(EDGE_2_SHARED_NAME) SharedPreferences edge2Shared
            , @Named(EDGE_2_WIDTH_PXL_NAME) int edgeWidth
            , @Named(EDGE_2_HEIGHT_PXL_NAME) int edgeHeight) {

        return Utility.getEdgeLayoutPara(defaultShared, edge2Shared, mScale, edge2Position, edgeWidth, edgeHeight);
    }

    @Provides
    @Singleton
    @Named(EDGE_1_WIDTH_PXL_NAME)
    int edge1Width(@Named(EDGE_1_SHARED_NAME) SharedPreferences shared
            , @Named(EDGE_1_POSITION_NAME) int  edgePosition
            ,@Named(M_SCALE_NAME) float mScale) {
        int edgeSensivite = shared.getInt(Cons.EDGE_SENSIIVE_KEY,Cons.EDGE_SENSITIVE_DEFAULT);
        int edgeLength = shared.getInt(Cons.EDGE_LENGTH_KEY,Cons.EDGE_LENGTH_DEFAULT);
        int edgeWidthPxl;


        if (Utility.rightLeftOrBottom(edgePosition) == Cons.POSITION_BOTTOM) {
            edgeWidthPxl = (int) (edgeLength * mScale);
        } else {
            edgeWidthPxl = (int) (edgeSensivite * mScale);
        }
        return edgeWidthPxl;
    }

    @Provides
    @Singleton
    @Named(EDGE_2_WIDTH_PXL_NAME)
    int edge2Width(@Named(EDGE_2_SHARED_NAME) SharedPreferences shared
            , @Named(EDGE_2_POSITION_NAME) int edgePosition
            , @Named(M_SCALE_NAME) float mScale) {
        int edgeSensivite = shared.getInt(Cons.EDGE_SENSIIVE_KEY,Cons.EDGE_SENSITIVE_DEFAULT);
        int edgeLength = shared.getInt(Cons.EDGE_LENGTH_KEY,Cons.EDGE_LENGTH_DEFAULT);
        int edgeWidthPxl;


        if (Utility.rightLeftOrBottom(edgePosition) == Cons.POSITION_BOTTOM) {
            edgeWidthPxl = (int) (edgeLength * mScale);
        } else {
            edgeWidthPxl = (int) (edgeSensivite * mScale);
        }
        return edgeWidthPxl;
    }

    @Provides
    @Singleton
    @Named(EDGE_1_HEIGHT_PXL_NAME)
    int edge1Height(@Named(EDGE_1_SHARED_NAME) SharedPreferences shared
            , @Named(EDGE_1_POSITION_NAME) int  edgePosition
            ,@Named(M_SCALE_NAME) float mScale){
        int edgeSensivite = shared.getInt(Cons.EDGE_SENSIIVE_KEY,Cons.EDGE_SENSITIVE_DEFAULT);
        int edgeLength = shared.getInt(Cons.EDGE_LENGTH_KEY,Cons.EDGE_LENGTH_DEFAULT);
        int edgeHeightPxl;


        if (Utility.rightLeftOrBottom(edgePosition) == Cons.POSITION_BOTTOM) {
            edgeHeightPxl = (int) (edgeSensivite * mScale);
        } else {
            edgeHeightPxl = (int) (edgeLength * mScale);
        }
        return edgeHeightPxl;
    }

    @Provides
    @Singleton
    @Named(EDGE_2_HEIGHT_PXL_NAME)
    int edge2Height(@Named(EDGE_2_SHARED_NAME) SharedPreferences shared
            , @Named(EDGE_2_POSITION_NAME) int edgePosition
            , @Named(M_SCALE_NAME) float mScale){
        int edgeSensivite = shared.getInt(Cons.EDGE_SENSIIVE_KEY,Cons.EDGE_SENSITIVE_DEFAULT);
        int edgeLength = shared.getInt(Cons.EDGE_LENGTH_KEY,Cons.EDGE_LENGTH_DEFAULT);
        int edgeHeightPxl;


        if (Utility.rightLeftOrBottom(edgePosition) == Cons.POSITION_BOTTOM) {
            edgeHeightPxl = (int) (edgeSensivite * mScale);
        } else {
            edgeHeightPxl = (int) (edgeLength * mScale);
        }
        return edgeHeightPxl;
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
    int quickActionViewRadius(@Named(CIRCLE_SIZE_PXL_NAME) float circleSize
            , @Named(M_SCALE_NAME) float mScale) {
        return (int) ((CIRCLE_AND_QUICK_ACTION_GAP + OVAL_RADIUS_PLUS) * mScale + circleSize);
    }


    @Provides
    @Singleton
    Vibrator vibrator(){
        return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Provides
    @Singleton
    @Named(IS_EDGE_1_ON_NAME)
    boolean isEdge1On(@Named(EDGE_1_SHARED_NAME) SharedPreferences edge1Shared){
        return edge1Shared.getBoolean(Cons.EDGE_ON_KEY, true);
    }

    @Provides
    @Singleton
    @Named(IS_EDGE_2_ON_NAME)
    boolean isEdge2On(@Named(EDGE_2_SHARED_NAME) SharedPreferences edge2shared) {
        return edge2shared.getBoolean(Cons.EDGE_ON_KEY, false);
    }

    @Provides
    @Singleton
    @Named(HOLD_TIME_NAME)
    int holdTime(@Named(DEFAULT_SHARED_NAME) SharedPreferences shared){
        return shared.getInt(Cons.HOLD_TIME_KEY, HOLD_TIME_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(HOLD_TIME_ENABLE_NAME)
    boolean holdTimeEnable(@Named(DEFAULT_SHARED_NAME) SharedPreferences shared){
        return shared.getBoolean(Cons.HOLD_TIME_ENABLE_KEY, true);
    }

    @Provides
    @Singleton
    @Named(VIBRATE_DURATION_NAME)
    int vibrationDuration(@Named(DEFAULT_SHARED_NAME) SharedPreferences shared) {
        return shared.getInt(VIBRATION_DURATION_KEY, VIBRATION_DURATION_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(ANIMATION_TIME_NAME)
    int animationTime(@Named(DEFAULT_SHARED_NAME) SharedPreferences shared) {
        return shared.getInt(ANI_TIME_KEY, ANIMATION_TIME_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(USE_ANIMATION_NAME)
    boolean useAnimation(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared) {
        return defaultShared.getBoolean(USE_ANIMATION_KEY, USE_ANIMATION_DEFAULT);
    }

    @Provides
    @Singleton
    @Named(EDGE_1_MODE_NAME)
    int edge1Mode(@Named(EDGE_1_SHARED_NAME) SharedPreferences shared) {
        int edge1mode;
        edge1mode = shared.getInt(EdgeSetting.CIRCLE_FAVORITE_MODE, 0);
        if (edge1mode == 0) {
            if (shared.getBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false)) {
                edge1mode = MODE_ONLY_FAVORITE;
            } else {
                edge1mode = MODE_DEFAULT;
            }
        }
        return edge1mode;
    }

    @Provides
    @Singleton
    @Named(EDGE_2_MODE_NAME)
    int edge2Mode(@Named(EDGE_2_SHARED_NAME) SharedPreferences shared) {
        int edge2mode = shared.getInt(EdgeSetting.CIRCLE_FAVORITE_MODE, 0);
        if (edge2mode == 0) {
            if (shared.getBoolean(EdgeSetting.IS_ONLY_FAVORITE_KEY, false)) {
                edge2mode = MODE_ONLY_FAVORITE;
            } else {
                edge2mode = MODE_DEFAULT;
            }
        }
        return edge2mode;
    }

    @Provides
    @Singleton
    @Named(QUICK_ACTION_WITH_INSTANT_FAVORITE_NAME)
    int[] quickActionWithInstant(@Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared) {
        int[] returnArray = new int[4];
        if (defaultShared.getString(EdgeSetting.ACTION_1_KEY, MainActivity.ACTION_INSTANT_FAVO).equalsIgnoreCase(MainActivity.ACTION_INSTANT_FAVO)) {
            returnArray[0] = 1;
        }else returnArray[0] = -1;
        if (defaultShared.getString(EdgeSetting.ACTION_2_KEY, MainActivity.ACTION_HOME).equalsIgnoreCase(MainActivity.ACTION_INSTANT_FAVO)) {
            returnArray[1] = 1;
        }else returnArray[1] = -1;
        if (defaultShared.getString(EdgeSetting.ACTION_3_KEY, MainActivity.ACTION_BACK).equalsIgnoreCase(MainActivity.ACTION_INSTANT_FAVO)) {
            returnArray[2] = 1;
        }else returnArray[2] = -1;
        if (defaultShared.getString(EdgeSetting.ACTION_4_KEY, MainActivity.ACTION_NOTI).equalsIgnoreCase(MainActivity.ACTION_INSTANT_FAVO)) {
            returnArray[3] = 1;
        }else returnArray[3] = -1;
        return returnArray;
    }

    @Provides
    @Singleton
    @Named(USE_INSTANT_FAVORITE_NAME)
    boolean useInstantFavorite(@Named(QUICK_ACTION_WITH_INSTANT_FAVORITE_NAME) int[] array) {
        for (int i : array) {
            if (i == 1) {
                return true;
            }
        }
        return false;
    }


    @Provides
    @Singleton
    @Named(CLOCK_PARENTS_VIEW_NAME)
    View clockParentsView(WindowManager windowManager
            , @Named(DEFAULT_SHARED_NAME) SharedPreferences defaultShared) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater.inflate(R.layout.clock, null);
    }

    @Provides
    @Singleton
    @Named(CLOCK_PARENTS_PARA_NAME)
    WindowManager.LayoutParams clockParentsPara(){
        WindowManager.LayoutParams para = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
        para.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
        return para;
    }

    @Provides
    @Singleton
    @Named(EXCLUDE_SET_NAME)
    Set<String> excludeSet(@Named(EXCLUDE_SHARED_NAME) SharedPreferences shared) {
        return shared.getStringSet(Cons.EXCLUDE_KEY, new HashSet<String>());
    }

    @Provides
    @Singleton
    @Named(USE_ACTION_DOW_VIBRATE_NAME)
    boolean useActionDownVibrate(@Named(DEFAULT_SHARED_NAME) SharedPreferences shared) {
        return !shared.getBoolean(EdgeSetting.DISABLE_HAPTIC_FEEDBACK_KEY, true);
    }

    @Provides
    @Singleton
    @Named(USE_ACTION_MOVE_VIBRATE_NAME)
    boolean useActionMoveVibrate(@Named(DEFAULT_SHARED_NAME) SharedPreferences shared) {
        return shared.getBoolean(Cons.HAPTIC_ON_ICON_KEY, false);
    }

    @Provides
    @Singleton
    @Named(USE_CLOCK_NAME)
    boolean useClock(@Named(DEFAULT_SHARED_NAME) SharedPreferences shared) {
        return !shared.getBoolean(Cons.DISABLE_CLOCK_KEY, false);
    }













}
