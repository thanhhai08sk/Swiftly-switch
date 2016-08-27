package org.de_studio.recentappswitcher;

import java.util.Comparator;

/**
 * Created by HaiNguyen on 8/5/16.
 */
public class Cons {

    public static final Comparator<Long> DATE_DECENDING_COMPARATOR = new Comparator<Long>() {
        @Override
        public int compare(Long lhs, Long rhs) {
            if (rhs > lhs) {
                return 1;
            } else if (rhs == lhs) {
                return 0;
            } else return -1;
        }
    };

    public static final String ACTION_TOGGLE_EDGES = "org.de_studio.recentappswitcher.action.toggle_edges";
    public static final int POSITION_RIGHT_TOP = 10;
    public static final int POSITION_RIGHT_CENTRE = 11;
    public static final int POSITION_RIGHT_BOTTOM = 12;
    public static final int POSITION_LEFT_TOP = 20;
    public static final int POSITION_LEFT_CENTRE = 21;
    public static final int POSITION_LEFT_BOTTOM = 22;
    public static final int POSITION_BTTOM_CENTRE = 31;
    public static final int POSITION_RIGHT = 1;
    public static final int POSITION_LEFT = 2;
    public static final int POSITION_BOTTOM = 3;

    public static final int QUICK_ACTION_GAP_DP = 35;
    public static final int DEFAULT_ICON_SIZE = 48;
    public static final int DEFAULT_ICON_GAP_IN_GRID = 10;
    public static final int INIT_OFFSET = 10;

    public static final String TAG_EDGE_1 = "edge1";
    public static final String TAG_EDGE_2 = "edge2";


    public static final int EDGE_SENSITIVE_DEFAULT = 12;
    public static final int EDGE_LENGTH_DEFAULT = 150;
    public static final float ICON_SCALE_DEFAULT = 1f;
    public static final int EDGE_OFFSET_DEFAULT = 0;



//    edge setting
    public static final String AVOID_KEYBOARD_KEY = "avoid_keyboard";
    public static final String AVOID_KEYBOARD_OPTION_KEY = "avoid_keyboard_option";
    public static final int OPTION_STEP_ASIDE = 1;
    public static final int OPTION_PLACE_UNDER = 0;
    public static final String EDGE_POSITION_KEY = "position";
    public static final String EDGE_SENSIIVE_KEY = "sensitive";
    public static final String EDGE_LENGTH_KEY = "length";
    public static final String EDGE_OFFSET_KEY = "off_set";
    public static final String EDGE_NUMBER_KEY = "number_of_edge";
    public static final String EDGE_ON_KEY = "is_on";
    public static final String FAVORITE_KEY = "favorite";
    public static final String EXCLUDE_KEY = "exclude";
    public static final String TRIAL_TIME_PASS_KEY = "trial_time_pass";
    public static final String BEGIN_DAY_KEY = "begin_trial_time";
    public static final String DISABLE_HAPTIC_FEEDBACK_KEY = "disable_haptic";
    public static final String HAPTIC_ON_ICON_KEY = "haptic_on_icon";
    public static final String DISABLE_CLOCK_KEY = "disable_clock";
    public static final String DISABLE_INDICATOR = "disable_indicator";
    public static final String ANIMATION_KEY = "disable_background_animation";
    public static final String HOLD_TIME_ENABLE_KEY = "hold_time_enable";
    public static final String APP_OPEN_TIME_KEY = "app_open_time";
    public static final String HAS_REACT_FOR_VOTE_KEY = "has_react_for_vote";
    public static final String ICON_DISTANCE_KEY = "icon_distance";
    public static final String NUM_OF_RECENT_KEY = "num_of_recent";
    public static final String NUM_OF_GRID_ROW_KEY = "grid_row";
    public static final String NUM_OF_GRID_COLUMN_KEY = "grid_column";
    public static final String IS_CENTRE_FAVORITE = "is_centre";
    public static final String IS_DISABLE_IN_LANSCAPE = "disable_in_lanscape";
    public static final String GAP_OF_SHORTCUT_KEY = "shortcut_gap";
    public static final String GRID_DISTANCE_FROM_EDGE_KEY = "grid_distance_from_edge";
    public static final String GRID_DISTANCE_VERTICAL_FROM_EDGE_KEY = "grid_distance_vertical_from_edge";
    public static final String IS_ONLY_FAVORITE_KEY = "is_only_favorite";
    public static final String ICON_PACK_PACKAGE_NAME_KEY = "icon_pack_packa";
    public static final String ACTION_1_KEY = "action_1";
    public static final String ACTION_2_KEY = "action_2";
    public static final String ACTION_3_KEY = "action_3";
    public static final String ACTION_4_KEY = "action_4";
    public static final String IS_ACTIONS_STAY_PERMANENT = "is_permanent";
    public static final String VIBRATION_DURATION_KEY = "vibration_duration";
    public static final String HOLD_TIME_KEY  = "hold_time";
    public static final String ANI_TIME_KEY = "animation_time";
    public static final String IS_PIN_TO_TOP_KEY = "is_pin_to_top";
    public static final String BACKGROUND_COLOR_KEY = "background_color";
    public static final String GUIDE_COLOR_KEY = "guide_color";
    public static final String USE_GUIDE_KEY = "edge_guide";
    public static final String HAS_TELL_ABOUT_TRIAL_LIMIT = "has_tell_about_trial_limit";
    public static final String CONTACT_ACTION = "contact_action";
    public static final String CIRCLE_FAVORITE_MODE = "circle_fovorite_mode";
    public static final String SERVICE_ID = "service_id";
    public static final String ICON_SCALE = "icon_scale";
    public static final int ACTION_CHOOSE = 0;
    public static final int ACTION_CALL = 1;
    public static final int ACTION_SMS = 2;




}
