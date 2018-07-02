package org.de_studio.recentappswitcher;

import android.graphics.Color;

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

    public static final int OLD_REALM_SCHEMA_VERSION = 11;
    public static final int REALM_SCHEMA_VERSION = 9;

    public static final String PRO_VERSION_PACKAGE_NAME = "org.de_studio.recentappswitcher.pro";
    public static final String JOURNAL_IT_PACKAGE_NAME = "org.de_studio.diary";
    public static final String FREE_VERSION_PACKAGE_NAME = "org.de_studio.recentappswitcher.trial";
    public static final int NOTIFICATION_ID = 2323;

    public static final String SHARED_PREFERENCE_NAME = "org.de_studio.recentappswitcher.shared";
    public static final String EDGE_1_SHARED_NAME = "org.de_studio.recentappswitcher_edge_1_shared_preference";
    public static final String EDGE_2_SHARED_NAME = "org.de_studio.recentappswitcher_edge_2_shared_preference";
    public static final String EDGE_3_SHARED_NAME = "org.de_studio.recentappswitcher_edge_3_shared_preference";
    public static final String OLD_DEFAULT_SHARED_NAME = "org.de_studio.recentappswitcher_sharedpreferences";
    public static final String EXCLUDE_SHARED_NAME = "org.de_studio.recentappswitcher_exclude_shared_preferences";
    public static final String DEFAULT_REALM_NAME = "swiftly_switch_ver2.realm";

    public static final int REQUEST_CODE_CONTACT_PERMISSION = 121;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 122;


    public static final int RAD_ICON_DEFAULT_DP = 24;
    public static final String ACTION_UPDATE_TOGGLE_WIDGET = "org.de_studio.recentappswitcher.toggle_widget";
    public static final String ACTION_TOGGLE_EDGES = "org.de_studio.recentappswitcher.action.toggle_edges";
    public static final String ACTION_SCREENSHOT_OK = "org.de_studio.recentappswitcher.action.screenshot_ok";
    public static final String ACTION_REFRESH_FAVORITE = "org.de_studio.recentappswitcher.action.refresh_favorite";
    public static final String ACTION_BACK = "org.de_studio.recentappswitcher.action.back";
    public static final String ACTION_RECENT = "org.de_studio.recentappswitcher.action.recent";
    public static final String ACTION_HOME = "org.de_studio.recentappswitcher.action.home";
    public static final String ACTION_NOTI = "org.de_studio.recentappswitcher.action.noti";
    public static final String ACTION_POWER_MENU = "org.de_studio.recentappswitcher.action.power_menu";
    public static final int POSITION_RIGHT_TOP = 10;
    public static final int POSITION_RIGHT_CENTRE = 11;
    public static final int POSITION_RIGHT_BOTTOM = 12;
    public static final int POSITION_LEFT_TOP = 20;
    public static final int POSITION_LEFT_CENTRE = 21;
    public static final int POSITION_LEFT_BOTTOM = 22;
    public static final int POSITION_BOTTOM_CENTRE = 31;
    public static final int POSITION_RIGHT = 1;
    public static final int POSITION_LEFT = 2;
    public static final int POSITION_BOTTOM = 3;
    public static final int CIRCLE_ICON_NUMBER_DEFAULT = 6;
    public static final int RINGER_MODE_NORMAL = 0;
    public static final int RINGER_MODE_VIBRATE = 1;
    public static final int RINGER_MODE_SILENT = 2;

    public static final int QUICK_ACTION_GAP_DP = 35;
    public static final int ICON_SIZE_DEFAULT = 48;
    public static final int DEFAULT_ICON_GAP_IN_GRID = 10;
    public static final int DEFAULT_FAVORITE_GRID_PADDING_HORIZONTAL = 40; //no use
    public static final int DEFAULT_FAVORITE_GRID_PADDING_VERTICAL = 40;  //no use
    public static final int DEFAULT_FAVORITE_GRID_COLUMN_COUNT = 4;
    public static final int DEFAULT_FAVORITE_GRID_ROW_COUNT = 6;
    public static final int DEFAULT_FAVORITE_GRID_VERTICAL_MARGIN = 40;
    public static final int DEFAULT_FAVORITE_GRID_HORIZONTAL_MARGIN = 40;
    public static final int DEFAULT_FAVORITE_GRID_SPACE = 8;
    public static final int FAVORITE_GRID_MIN_VERTICAL_MARGIN = 0;
    public static final int FAVORITE_GRID_MAX_VERTICAL_MARGIN = 120;
    public static final int FAVORITE_GRID_MIN_HORIZONTAL_MARGIN = 0;
    public static final int FAVORITE_GRID_MAX_HORIZONTAL_MARGIN = 120;
    public static final int FAVORITE_GRID_MIN_SHORTCUTS_SPACE = 5;
    public static final int FAVORITE_GRID_MAX_SHORTCUTS_SPACE = 40;
    public static final int INIT_OFFSET = 10;
    public static final int CIRCLE_RADIUS_DEFAULT = 95;
    public static final int CIRCLE_COUNT_DEFAULT = 6;
    public static final int QUICK_ACTION_RADIUS_DEFAULT = 70;

    public static final int CIRCLE_AND_QUICK_ACTION_GAP = 35;
    public static final int OVAL_RADIUS_PLUS = 17;
    public static final int OVAL_OFFSET = 70;
    public static final int GRID_GAP_DEFAULT = 5;
    public static final int GUIDE_COLOR_DEFAULT = Color.argb(255, 23, 103, 137);
    public static final int BACKGROUND_COLOR_DEFAULT = Color.argb(170, 0, 0, 0);
    public static final int FOLDER_BACKGROUND_COLOR_DEFAULT = Color.argb(255, 224, 224, 224);
    public static final int LONG_PRESS_DELAY_DEFAULT = 800;
    public static final int DEFAULT_VIBRATE_DURATION = 15;
    public static final int ANIMATION_TIME_DEFAULT = 250;
    public static final int ANIMATION_TIME_MIN = 0;
    public static final int ANIMATION_TIME_MAX = 1500;
    public static final long TIME_INTERVAL_SHORT = 1000000;
    public static final long TIME_INTERVAL_LONG = 604800000;
    public static final long TRIAL_TIME = 1000 * 60 * 60 * 24 * 14;
    public static final long WAIT_FOR_SHOWING_JOURNAL_IT_TIME = 1000 * 60 * 60 * 24 * 7;
    public static final long WAIT_FOR_REVIEW_REQUEST_TIME = 1000 * 60 * 60 * 24 * 7;
    public static final long REVIEW_REQUEST_INTEVAL_TIME = 1000 * 60 * 60 * 24 * 24;


    public static final int EDGE_1_ID_INT = 11;
    public static final int EDGE_2_ID_INT = 22;
    public static final int EDGE_3_ID_INT = 33;
    public static final int BACKGROUND_ID_INT = 33;
    public static final int QUICK_ACTION_ID_INSTANT_GRID = 1;
    public static final int QUICK_ACTION_ID_NORMAL = 2;


    public static final int DEFAULT_EDGE_SENSITIVE = 12;
    public static final int DEFAULT_EDGE_LENGTH = 150;
    public static final float ICON_SCALE_DEFAULT = 1f;
    public static final int DEFAULT_EDGE_OFFSET = 0;
    public static final boolean DEFAULT_USE_ANIMATION = true;

    public static final String OPEN_FOLDER_DELAY_NAME = "open_folder_delay";
    public static final String LAUNCHER_PACKAGENAME_NAME = "launcher_packagename";
    public static final String M_SCALE_NAME = "m_scale";
    public static final String ICON_SCALE_NAME = "icon_scale";
    public static final String COLLECTION_WINDOW_PARAMS_NAME = "grid_parent_view_para";
    public static final String GRID_WINDOW_PARAMS_NAME = "grid_params";
    public static final String GUIDE_COLOR_NAME = "guide_color";
    public static final String BACKGROUND_COLOR_NAME = "background_color";
    public static final String EDGE_1_VIEW_NAME = "edge_1_view";
    public static final String EDGE_2_VIEW_NAME = "edge_2_view";
    public static final String EDGE_3_VIEW_NAME = "edge_3_view";
    public static final String EDGE_1_PARA_NAME = "edge1Para";
    public static final String EDGE_2_PARA_NAME = "edge2Para";
    public static final String EDGE_3_PARA_NAME = "edge3Para";
    public static final String HOLD_TIME_NAME = "holdTime";
    public static final String ANIMATION_TIME_NAME = "animationTime";
    public static final String USE_ANIMATION_NAME = "useAnimation";
    public static final String USE_TRANSITION_NAME = "useTransition";
    public static final String CLOCK_PARENTS_VIEW_NAME = "clockParentsView";
    public static final String EXCLUDE_SET_NAME = "excludeSet";
    public static final String EDGE_1_NAME = "edge1";





    public static final String EDGE_2_NAME = "edge2";
    public static final String EDGE_3_NAME = "edge3";
    public static final String PIN_REALM_NAME = "pinApp.realm";
    public static final String FAVORITE_GRID_REALM_NAME = "default.realm";
    public static final String FAVORITE_CIRCLE_REALM_NAME = "circleFavo.realm";



    public static final String PRO_PURCHASED_KEY = "realmsm";
    public static final String BEGIN_DAY_KEY = "begin_trial_time";
    public static final String OPEN_FOLDER_DELAY_KEY = "open_folder_delay";
    public static final String DISABLE_IN_FULLSCREEN_KEY = "disable_in_fullscreen";
    public static final String AUTO_SHOW_WHAT_NEW_KEY = "auto_show_what_new";
    public static final String AVOID_KEYBOARD_KEY = "avoid_keyboard";
    public static final String EDGE_1_ON_KEY = "edge_1_on";
    public static final String EDGE_2_ON_KEY = "edge_2_on";
    public static final String EDGE_3_ON_KEY = "edge_3_on";
    public static final String TRIAL_START_TIME_KEY = "begin_trial_time";
    public static final String DISABLE_HAPTIC_FEEDBACK_KEY = "disable_haptic";
    public static final String HAPTIC_ON_ICON_KEY = "haptic_on_icon";
    public static final String DISABLE_CLOCK_KEY = "disable_clock";
    public static final String DISABLE_INDICATOR_KEY = "disable_indicator";
    public static final String USE_ANIMATION_KEY = "disable_background_animation";
    public static final String USE_TRANSITION_KEY = "useTransition";
    public static final String IS_DISABLE_IN_LANDSCAPE_KEY = "disable_in_lanscape";
    public static final String ICON_PACK_PACKAGE_NAME_KEY = "icon_pack_packa";
    public static final String ICON_PACK_NONE = "none";
    public static final String IS_ACTIONS_STAY_PERMANENT = "is_permanent";
    public static final String VIBRATION_DURATION_KEY = "vibration_duration";
    public static final String LONG_PRESS_DELAY_KEY = "hold_time";
    public static final String ANIMATION_TIME_KEY = "animation_time";
    public static final String IS_PIN_TO_TOP_KEY = "is_pin_to_top";
    public static final String BACKGROUND_COLOR_KEY = "background_color";
    public static final String GUIDE_COLOR_KEY = "guide_color";
    public static final String USE_GUIDE_KEY = "edge_guide";
    public static final String HAS_TELL_ABOUT_TRIAL_LIMIT = "has_tell_about_trial_limit";
    public static final String CONTACT_ACTION_KEY = "contact_action";
    public static final String RINGER_MODE_ACTION_KEY = "ringer_mode_action";
    public static final String CIRCLE_FAVORITE_MODE = "circle_fovorite_mode";
    public static final String SERVICE_ID = "service_id";
    public static final String ICON_SCALE_KEY = "icon_scale";
    public static final String FIRST_START_KEY = "firstStart";
    public static final String DATE_START_KEY = "dateStart";
    public static final String SAW_JOURNAL_IT_KEY = "sawJournalIt";
    public static final String SAW_JOURNAL_IT_DATE_KEY = "sawJournalItDate";
    public static final String DONE_WITH_REVIEW_REQUEST = "doneWithReviewRequest";
    public static final String LAST_REVIEW_REQUEST = "lastReviewRequest";





    public static final int CONTACT_ACTION_CHOOSE = 0;
    public static final int CONTACT_ACTION_CALL = 1;
    public static final int CONTACT_ACTION_SMS = 2;
    public static final int DEFAULT_CONTACT_ACTION = CONTACT_ACTION_CHOOSE;
    public static final int RINGER_MODE_ACTION_SOUND_AND_VIBRATE = 0;
    public static final int RINGER_MODE_ACTION_SOUND_AND_SILENT = 1;
    public static final int RINGER_MODE_ACTION_DEFAULT = RINGER_MODE_ACTION_SOUND_AND_VIBRATE;


    public static final int HOLD_TIME_MIN = 75;
    public static final int HOLD_TIME_MAX = 2000;
    public static final int VIBRATION_TIME_MIN = 5;
    public static final int VIBRATION_TIME_MAX = 150;
    public static final int VIBRATION_TIME_DEFAULT = 15;
    public static final int CIRCLE_SIZE_MAX = 150;
    public static final int CIRCLE_SIZE_MIN = 60;
    public static final int EDGE_SENSITIVE_MIN = 5;
    public static final int EDGE_LENGTH_MIN = 55;
    public static final int EDGE_LENGTH_MAX = 500;
    public static final int EDGE_OFFSET_MIN = -300;
    public static final double CIRCLE_INIT_ANGLE_FOR_3 = 0.15 * Math.PI;
    public static final double CIRCLE_INIT_ANGLE_LESS_THAN_6_ITEMS = 0.111 * Math.PI;
    public static final double CIRCLE_INIT_ANGLE_GREATER_OR_EQUAL_6_ITEMS = 0.0566 * Math.PI;



    //Model
    public static final String TYPE = "type";
    public static final String ITEM_ID = "itemId";
    public static final String LABEL = "label";
    public static final String ACTION = "action";
    public static final String PACKAGENAME = "packageName";
    public static final String CONTACT_ID = "contactId";
    public static final String COLLECTION_ID = "collectionId";
    public static final String SLOT_ID = "slotId";
    public static final String EDGE_ID = "edgeId";

    public static final int LAYOUT_TYPE_LINEAR = 0;
    public static final int LAYOUT_TYPE_GRID = 1;

    public static final int ITEM_TYPE_ICON_LABEL = 0;
    public static final int ITEM_TYPE_ICON_ONLY = 1;
    public static final int ITEM_TYPE_ICON_LABEL_INSTANT = 2;


    public static final String SHARED_PREFERENCE_FOLDER_NAME = "shared_prefs";
    public static final String BACKUP_FILE_NAME = "backup.swiftly_switch";
    public static final String AUTHORITY = "org.de_studio.recentappswitcher.provider";



    public static final String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgHZzxPr4voivbRVH3i1ikAKE75u89En2Dy7sokKutIxsbhp5r/MCZ/d5vyNOwglmEdO+7B555jIN8HgAOC2q5Eu6xFjFXRbiC/cu6S++0A2P10i/mmswLE0cwVQPpNNU/n61CotWp1yeXAXThhfSzxNEYyHBs97EOtDe2BVXHn5DXOsEvyf5dK0NSmFqyPOBLFOG+dZ9irRwB5bKqkYr0T2N4JX4Vk1exG/rXajmxjBdkJaPKYNwWPGf7mFJXYbFpTmLj5JWQDXs/b2JQs1fcyiUd13Q48KjUq9l4/Byz+oIJC1J4UNHiiXAM1qLPnEwHT/bwhJgYBb61tLOH6u8zwIDAQAB";
    public static final String SKU_PRO = "sku_pro";

    public static final String FOLDER_BACKGROUND_COLOR_KEY = "folder_background_color";
}
