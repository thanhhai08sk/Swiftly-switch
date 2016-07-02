package org.de_studio.recentappswitcher;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.animation.Animator;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.AppCompatImageView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.dialogActivity.AudioDialogActivity;
import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.ChooseActionDialogActivity;
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSetting;
import org.de_studio.recentappswitcher.service.FolderAdapter;
import org.de_studio.recentappswitcher.service.MyImageView;
import org.de_studio.recentappswitcher.service.ScreenBrightnessDialogActivity;
import org.de_studio.recentappswitcher.service.VolumeDialogActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.realm.Realm;

/**
 * Created by hai on 12/19/2015.
 */
public  class Utility {
    private static final String TAG = Utility.class.getSimpleName();
//    public static int dpiToPixels (int dp, WindowManager windowManager){
//        DisplayMetrics metrics =new DisplayMetrics();
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//        float logicalDensity = metrics.density;
//        return (int) Math.ceil(dp*logicalDensity);
//    }

    public static int findIconToSwitch (int[] x, int[] y,int x_cord, int y_cord, int numOfIcon, int radOfIcon, float mScale) {
        int radOfIconPxl = (int) (radOfIcon * mScale);
        if (numOfIcon >= 1) {
            if (x_cord >= x[0] & x_cord <= (x[0] + radOfIconPxl * 2) & y_cord >= y[0] & y_cord <= (y[0] + radOfIconPxl * 2)) {
                return 0;
            }
        }
        if (numOfIcon >= 2) {
            if (x_cord >= x[1] & x_cord <= (x[1] + radOfIconPxl * 2) & y_cord >= y[1] & y_cord <= (y[1] + radOfIconPxl * 2)) {
                return 1;
            }
        }
        if (numOfIcon >= 3) {
            if (x_cord >= x[2] & x_cord <= (x[2] + radOfIconPxl * 2) & y_cord >= y[2] & y_cord <= (y[2] + radOfIconPxl * 2)) {
                return 2;
            }
        }
        if (numOfIcon >= 4) {
            if (x_cord >= x[3] & x_cord <= (x[3] + radOfIconPxl * 2) & y_cord >= y[3] & y_cord <= (y[3] + radOfIconPxl * 2)) {
                return 3;
            }
        }
        if (numOfIcon >= 5) {
            if (x_cord >= x[4] & x_cord <= (x[4] + radOfIconPxl * 2) & y_cord >= y[4] & y_cord <= (y[4] + radOfIconPxl * 2)) {
                return 4;
            }
        }
        if (numOfIcon >= 6) {
            if (x_cord >= x[5] & x_cord <= (x[5] + radOfIconPxl * 2) & y_cord >= y[5] & y_cord <= (y[5] + radOfIconPxl * 2)) {
                return 5;
            }
        }
        return -1;
    }

    public static int findIconToSwitchNew(int[] x, int[] y, int x_cord, int y_cord, float radOfIconPxl, float mScale) {
        double distance;
        double distanceNeeded = 35*mScale;
        if (x != null && y != null) {
            for (int i = 0; i < x.length; i++) {
                distance = Math.sqrt(Math.pow((double)x_cord - (double)(x[i] + radOfIconPxl),2) + Math.pow((double)y_cord - (double)(y[i]+radOfIconPxl), 2));
                if (distance <= distanceNeeded) {
                    return i;
                }
            }
        }

        return -1;
    }


    public static int findShortcutToSwitch(int x_cord, int y_cord, int x_grid, int y_grid, int sizeOfIcon, float mScale,int gird_row, int grid_column, int grid_gap, boolean folderMode) {
        int item_x,item_y;
        double distance;
        double smallestDistance = 1000*mScale;
        for (int i = 0; i < grid_column; i++) {
            for (int j = 0; j < gird_row; j++) {
                item_x = (int)(x_grid + (sizeOfIcon/2)*mScale +i*(sizeOfIcon + grid_gap)*mScale);
                item_y = (int) (y_grid + (sizeOfIcon/2) * mScale + j * (sizeOfIcon + grid_gap) * mScale);
                distance = Math.sqrt(Math.pow((double)x_cord - (double)item_x,2) + Math.pow((double)y_cord - (double) item_y, 2));
                if (distance <= 35 * mScale) {
                    return j * grid_column + i;
                } else {
                    if (smallestDistance > distance) {
                        smallestDistance = distance;
                    }
                }
            }
        }
        if (folderMode) {
            if (smallestDistance > 105 * mScale) {
                return -2;
            }
        }
        return -1;
    }

    public static void setIconPositionNew(final MyImageView[] icon, float r, float icon_24_dp_pxl, int position, int x_i, int y_i, int n, boolean isAnimation, int animationTime) {
        Log.e(TAG, "setIconPositionNew");
        double alpha, beta;
        if (animationTime <= 50) {
            isAnimation = false;
        }
        double[] alphaN = new double[n];
        switch (n) {
            case 4:
//                alpha = 0.1389*Math.PI; // 25 degree
                alpha = 0.111 * Math.PI; // 20 degree
                break;
            case 5:
                alpha = 0.111 * Math.PI; // 20 degree
                break;
            case 6:
                alpha = 0.0556 * Math.PI; // 10 degree
                break;
            default:
                alpha = 0.0556;
        }
        beta = Math.PI - 2 * alpha;
        float x;
        float y;
        if (!isAnimation) {
            for (int i = 0; i < n; i++) {
                alphaN[i] = alpha + i * (beta / (n - 1));
                switch (position / 10) {
                    case 1:
                        icon[i].setX(x_i - r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl);
                        icon[i].setY(y_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl);
                        break;
                    case 2:
                        icon[i].setX(x_i + r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl);
                        icon[i].setY(y_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl);
                        break;
                    case 3:
                        icon[i].setX(x_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl);
                        icon[i].setY(y_i - r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl);
                        break;
                }
            }
        } else {
            for (int i = 0; i < n; i++) {
                alphaN[i] = alpha + i * (beta / (n - 1));
                switch (position / 10) {
                    case 1:
                        icon[i].setX(x_i);
                        icon[i].setY(y_i);
                        x = x_i - r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl;
                        y = y_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl;
                        icon[i].setRotation(0f);
                        if (i == 0) {
                            icon[0].setOnAnimation(true);
                            icon[i].animate().setDuration(animationTime).x(x).y(y).rotation(720f).setInterpolator(new FastOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    icon[0].setOnAnimation(true);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    icon[0].setOnAnimation(false);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    icon[0].setOnAnimation(false);
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else {
                            icon[i].animate().setDuration(animationTime).x(x).y(y).rotation(720f).setInterpolator(new FastOutSlowInInterpolator());
                        }

//                    icon[i].setX(x);
//                    icon[i].setY(y);
                        break;
                    case 2:
                        x = x_i + r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl;
                        y = y_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl;
                        icon[i].setX(x_i);
                        icon[i].setY(y_i);
                        icon[i].setRotation(0f);
                        if (i == 0) {
                            icon[0].setOnAnimation(true);
                            icon[i].animate().setDuration(animationTime).x(x).y(y).rotation(720f).setInterpolator(new FastOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    icon[0].setOnAnimation(true);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    icon[0].setOnAnimation(false);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    icon[0].setOnAnimation(false);
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else {
                            icon[i].animate().setDuration(animationTime).x(x).y(y).rotation(720f).setInterpolator(new FastOutSlowInInterpolator());
                        }
                        break;
                    case 3:
                        x = x_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl;
                        y = y_i - r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl;
                        icon[i].setX(x_i);
                        icon[i].setY(y_i);
                        icon[i].setRotation(0f);
                        if (i == 0) {
                            icon[0].setOnAnimation(true);
                            icon[i].animate().setDuration(animationTime).x(x).y(y).rotation(720f).setInterpolator(new FastOutSlowInInterpolator()).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    icon[0].setOnAnimation(true);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    icon[0].setOnAnimation(false);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    icon[0].setOnAnimation(false);
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        } else {
                            icon[i].animate().setDuration(animationTime).x(x).y(y).rotation(720f).setInterpolator(new FastOutSlowInInterpolator());
                        }
                        break;
                }
            }
        }

        if (n < icon.length) {
            for (int j = n; j < icon.length; j++) {
                icon[j].setVisibility(View.GONE);
            }
        }

    }

    public static void setIconsPosition(AppCompatImageView[] icon, int x_init_cord, int y_init_cord, float icon_distance_pxl, float icon_24_dp_pxl, int edgePosition){
        double sin10 = 0.1736, sin42 = 0.6691, sin74 = 0.9613, cos10 = 0.9848, cos42 = 0.7431, cos74 = 0.2756;
        switch (edgePosition){
            case 10:
                icon[0].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 11:
                icon[0].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                int distance = x_init_cord - (int)icon[0].getX();
                Log.e(TAG,"x_init - x0 = "+distance + "\nx_init = "+ x_init_cord );
                break;
            case 12:
                icon[0].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord - sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord - sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord - sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 20:
                icon[0].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                break;
            case 21:
                icon[0].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                int distance2 = x_init_cord - (int)icon[0].getX();
                Log.e(TAG, "x_init - x0 = " + distance2 + "\nx_init = "+ x_init_cord);
                break;
            case 22:
                icon[0].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + sin74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord + (float) cos74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + sin42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord + (float) cos42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + sin10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord + (float) cos10 * icon_distance_pxl - icon_24_dp_pxl);
                break;

            case 31:
                icon[0].setX((float) (x_init_cord - cos10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[0].setY(y_init_cord - (float) sin10 * icon_distance_pxl - icon_24_dp_pxl);
                icon[1].setX((float) (x_init_cord - cos42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[1].setY(y_init_cord - (float) sin42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[2].setX((float) (x_init_cord - cos74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[2].setY(y_init_cord - (float) sin74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[3].setX((float) (x_init_cord + cos74 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[3].setY(y_init_cord - (float) sin74 * icon_distance_pxl - icon_24_dp_pxl);
                icon[4].setX((float) (x_init_cord + cos42 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[4].setY(y_init_cord - (float) sin42 * icon_distance_pxl - icon_24_dp_pxl);
                icon[5].setX((float) (x_init_cord + cos10 * icon_distance_pxl) - icon_24_dp_pxl);
                icon[5].setY(y_init_cord - (float) sin10 * icon_distance_pxl - icon_24_dp_pxl);
                break;
        }

    }

    public static void setFavoriteShortcutGridViewPosition(GridView gridView,float gridTall, float gridWide, int x_init_cord, int y_init_cord, float mScale, int edgePosition, WindowManager windowManager, SharedPreferences sharedPreferences, int distanceFromEdgeDp, int distanceVertical, int iconToSwitch) {
        float distanceFromEdge = ((float)distanceFromEdgeDp) *mScale;
        float distanceVerticalFromEdge = ((float)distanceVertical)* mScale;
        boolean isCenter = sharedPreferences.getBoolean(EdgeSetting.IS_CENTRE_FAVORITE, false);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        float x = point.x;
        float y = point.y;
        float[] triggerPoint = getTriggerPoint((float) x_init_cord, (float) y_init_cord, sharedPreferences, edgePosition, iconToSwitch, mScale);
        if (!isCenter) {
            switch (edgePosition) {
                case 10:
                    gridView.setX(((float) x_init_cord) - distanceFromEdge - gridWide);
                    if (iconToSwitch != -1) {
                        if (triggerPoint[1] - gridTall / 2 < distanceVerticalFromEdge) {
                            gridView.setY(distanceVerticalFromEdge);
                        } else {
                            gridView.setY(triggerPoint[1] - gridTall/2);
                        }
                    } else {
                        gridView.setY(distanceVerticalFromEdge);
                    }

                    break;
                case 11:
//                    if (x_init_cord - triggerPoint[0] < distanceFromEdge) {
                        gridView.setX(((float) x_init_cord) - distanceFromEdge - gridWide);
//                    } else {
//                        gridView.setX(triggerPoint[0] - gridWide + 24*mScale);
//                    }
//                    if (((float) y_init_cord) - gridTall / (float)2 < 0) {
//                        gridView.setY(0);
//                    } else if (((float) y_init_cord) - gridTall /(float) 2 + gridTall > y) {
//                        gridView.setY(y - gridTall);
//                    } else {
//                        gridView.setY(((float) y_init_cord) - gridTall /(float) 2);
//                    }
                    if (iconToSwitch >1) {
                        gridView.setY(triggerPoint[1] - gridTall / (float) 2);
                    } else {
                        gridView.setY(((float) y_init_cord) - gridTall /(float) 2);
                    }

                    break;
                case 12:
                    gridView.setX(((float) x_init_cord) - distanceFromEdge - gridWide);
                    if (y - triggerPoint[1] - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(y - gridTall - distanceVerticalFromEdge);
                    } else {
                        gridView.setY(triggerPoint[1] - gridTall/2);
                    }

                    break;
                case 20:
                    gridView.setX(((float) x_init_cord) + distanceFromEdge);
                    if (iconToSwitch != -1) {
                        if (triggerPoint[1] - gridTall / 2 < distanceVerticalFromEdge) {
                            gridView.setY(distanceVerticalFromEdge);
                        } else {
                            gridView.setY(triggerPoint[1] - gridTall/2);
                        }
                    } else {
                        gridView.setY(distanceVerticalFromEdge);
                    }
                    break;
                case 21:
                    gridView.setX(((float) x_init_cord) + distanceFromEdge);
//                    if (((float) y_init_cord) - gridTall /(float) 2 < 0) {
//                        gridView.setY(0);
//                    } else if (((float) y_init_cord) - gridTall /(float) 2 + gridTall > y) {
//                        gridView.setY(y - gridTall);
//                    } else {
//                        gridView.setY(((float) y_init_cord) - gridTall /(float) 2);
//                    }
                    if (iconToSwitch >1) {
                        gridView.setY(triggerPoint[1] - gridTall / (float) 2);
                    } else {
                        gridView.setY(((float) y_init_cord) - gridTall /(float) 2);
                    }
                    break;
                case 22:
                    gridView.setX(((float) x_init_cord) + distanceFromEdge);
                    if (y - triggerPoint[1] - gridTall / 2 < distanceVerticalFromEdge) {
                        gridView.setY(y - gridTall - distanceVerticalFromEdge);
                    } else {
                        gridView.setY(triggerPoint[1] - gridTall/2);
                    }
                    break;
                case 31:
                    if (iconToSwitch != -1) {
                        gridView.setX(triggerPoint[0] - gridWide / (float) 2);
                    } else {
                        gridView.setX(((float) x_init_cord) - gridWide /(float) 2);
                    }
                    gridView.setY(((float) y_init_cord) - distanceVerticalFromEdge - gridTall);
                    break;
            }
        } else {
            gridView.setX((x-gridWide)/2);
            gridView.setY((y-gridTall)/2);
        }

    }

    public static float[] getTriggerPoint(float x_init,float y_init,SharedPreferences sharedPreferences, int edgePosition, int iconToSwitch, float mScale) {
        float[] returnValue = new float[2];
        float circleSize = mScale * (float) sharedPreferences.getInt(EdgeSetting.ICON_DISTANCE_KEY, 105);
//        float iconScale = sharedPreferences.getFloat(EdgeSetting.ICON_SCALE, 1f);
//        float iconSize24 = iconScale *mScale * 24;
        double alpha, beta, alphaOfIconToSwitch;
        alpha = 0.0556 * Math.PI; // 10 degree
        beta = Math.PI - 2 * alpha;
        alphaOfIconToSwitch = alpha + iconToSwitch * (beta / 5);
        switch (edgePosition / 10) {
            case 1:
                returnValue[0] = x_init - circleSize * (float) Math.sin(alphaOfIconToSwitch);
                returnValue[1] = y_init - circleSize * (float) Math.cos(alphaOfIconToSwitch);
                break;
            case 2:
                returnValue[0] = x_init + circleSize * (float) Math.sin(alphaOfIconToSwitch);
                returnValue[1] = y_init - circleSize * (float) Math.cos(alphaOfIconToSwitch);
//                        icon[i].setX(x_i + r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl);
//                        icon[i].setY(y_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl);
                break;
            case 3:
                returnValue[0] = x_init - circleSize * (float) Math.cos(alphaOfIconToSwitch);
                returnValue[1] = y_init - circleSize * (float) Math.sin(alphaOfIconToSwitch);
//                        icon[i].setX(x_i - r * (float) Math.cos(alphaN[i]) - icon_24_dp_pxl);
//                        icon[i].setY(y_i - r * (float) Math.sin(alphaN[i]) - icon_24_dp_pxl);
                break;
        }
        return returnValue;
    }


    public static int isHomeOrBackOrNoti(int x_init_int, int y_init_int, int x_int, int y_int, int radius_int, float mScale, int position){
        double x_init = (double)x_init_int;
        double y_init = (double)y_init_int;
        double x = (double) x_int;
        double y = (double) y_int;
        double radius = (double) radius_int;
        double distance = Math.sqrt(Math.pow((double)x - (double)x_init,2) + Math.pow((double)y - (double) y_init, 2));
        double distanceNeeded_pxl = (double) ((35+ radius)* mScale);
        double maxAng = 0.4166*Math.PI;  // 75 degree
        double midAng = 0.3333* Math.PI; //60 degree
        double minAng = 0.0833*Math.PI; //15 degree
        double ang30 = 0.1666*Math.PI;
        double ang70 = 0.3889*Math.PI;
        double ang110 = 0.6111*Math.PI;
        double alpha;
        if (distance < distanceNeeded_pxl) {
            return 0;
        }

        if (position >= 30) {
            alpha = Math.acos((x_init - x) / distance);
        }else {
            alpha = Math.acos((y_init-y)/distance);
        }
        if (alpha < ang30) {
            return 1;
        }else if (alpha < ang70) {
            return 2;
        }else if (alpha < ang110) {
            return 3;
        }else return 4;

    }
//    public static int[] getExpandSpec(int x_init,int y_init,int rad, int distanceFromIcon,WindowManager win){
//        int[] result = new int[4];
//        int rad_pxl = dpiToPixels(rad,win);
//        int distance_pxl = dpiToPixels(distanceFromIcon,win);
//        double radian30 = 0.16667* Math.PI;
//        double sin30 = Math.sin(radian30);
//        double cos30 = Math.cos(radian30);
//        int a = 2* (int)((rad_pxl+ distance_pxl)*sin30);
//        int b = rad_pxl + distance_pxl - (int)((rad_pxl+distance_pxl)*cos30);
//        result[0] = x_init -(int)( (rad_pxl + distance_pxl)*sin30);
//        result[1] = y_init - (int)(rad_pxl*cos30) -(int)( distance_pxl*cos30) - b;
//        result[2] = result[0] + a;
//        result[3] = result[1] + b;
//        Log.e("expand", "left = " + result[0]+ "\ntop =" + result[1] + "\nright = "+ result[2] + "\nbottom = "+ result[3] +"\na = "+ a + "\nb = "+ b +"\nsin30= "
//        + sin30+ "\ncos30 = "+ cos30 + "\ndistance = " +distance_pxl + "\nradpx; = "+rad_pxl);
//        return result;
//    }

    public static int getPositionIntFromString(String position, Context context){
        String[] array = context.getResources().getStringArray(R.array.edge_dialog_spinner_array);
        if (position.equals(array[0])){
            return 10;
        }else if (position.equals(array[1])){
            return 11;
        }else if (position.equals(array[2])){
            return 12;
        }else if (position.equals(array[3])){
            return 20;
        }else if (position.equals(array[4])){
            return 21;
        }else if (position.equals(array[5])){
            return 22;
        }else if (position.equals(array[6])){
            return 31;
        }
        return 11;
    }



    // get installed apps but skip the system apps
    public static Set<PackageInfo> getInstalledApps(Context ctx) {
        final PackageManager packageManager = ctx.getPackageManager();

        final List<PackageInfo> allInstalledPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);
        final Set<PackageInfo> filteredPackages = new HashSet();

        Drawable defaultActivityIcon = packageManager.getDefaultActivityIcon();

        for(PackageInfo each : allInstalledPackages) {
//            if(ctx.getPackageName().equals(each.packageName)) {
//                continue;  // skip own app
//            }

            try {
                // add only apps with application icon
                Intent intentOfStartActivity = packageManager.getLaunchIntentForPackage(each.packageName);
                if(intentOfStartActivity == null)
                    continue;

                Drawable applicationIcon = packageManager.getActivityIcon(intentOfStartActivity);
                if(applicationIcon != null && !defaultActivityIcon.equals(applicationIcon)) {
                    filteredPackages.add(each);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.i("MyTag", "Unknown package name " + each.packageName);
            }
        }

        return filteredPackages;
    }

    public static boolean isAccessibilityEnable(Context context){
        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List <AccessibilityServiceInfo> info = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        if (info == null) {
            return false;
        }
        for (AccessibilityServiceInfo info1 : info){
            String description = info1.loadDescription(context.getPackageManager());
            if (description!= null){
                if (description.equals(context.getString(R.string.accessibility_service_description))){
                    return true;
                }
            }
        }
        return false;
    }

    public static void goHome(final Context context) {
        final Intent intent = new Intent(context, (Class)MainActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putInt("goHome", 1);
        intent.putExtras(bundle);
        intent.setFlags(268435456);
        context.startActivity(intent);
    }



    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void toggleWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean currentState = wifiManager.isWifiEnabled();
        wifiManager.setWifiEnabled(!currentState);
    }
    public static boolean getWifiState(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }
    public static void toggleBluetooth (Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter == null) {
            return;
        }
        boolean bluetoothState = bluetoothAdapter.isEnabled();
        if (bluetoothState){
            bluetoothAdapter.disable();
        }else {
            bluetoothAdapter.enable();
        }

    }
    public static boolean getBluetoothState (Context context){
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter == null) {
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }
    public static boolean checkIsFlashLightAvailable (Context context){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public static boolean getIsRotationAuto(Context context){
        int current = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION,0);
        return current ==1;
    }

    public static void setAutorotation(Context context){
        if(android.provider.Settings.System.getInt(context.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0) == 1)
        {
            android.provider.Settings.System.putInt(context.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0);
        }
        else{
            android.provider.Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
        }
    }

    public static void showAudioDialog(Context context){
        Intent intent = new Intent(context,AudioDialogActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static int getActionFromLabel(Context context, String label) {
        if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_wifi))) {
            return Shortcut.ACTION_WIFI;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_bluetooth))) {
            return Shortcut.ACTION_BLUETOOTH;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_rotation))) {
            return Shortcut.ACTION_ROTATION;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_power_menu))) {
            return Shortcut.ACTION_POWER_MENU;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_home))) {
            return Shortcut.ACTION_HOME;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_back))) {
            return Shortcut.ACTION_BACK;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_noti))) {
            return Shortcut.ACTION_NOTI;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_last_app))) {
            return Shortcut.ACTION_LAST_APP;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_call_log))) {
            return Shortcut.ACTION_CALL_LOGS;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_contact))) {
            return Shortcut.ACTION_CONTACT;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_recent))) {
            return Shortcut.ACTION_RECENT;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_volume))) {
            return Shortcut.ACTION_VOLUME;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_brightness))) {
            return Shortcut.ACTION_BRIGHTNESS;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_ringer_mode))) {
            return Shortcut.ACTION_RINGER_MODE;
        }  else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_dial))) {
            return Shortcut.ACTION_DIAL;
        }else if (label.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_none))) {
            return Shortcut.ACTION_NONE;
        }else return -1;
    }

    public static int getSizeOfFavoriteGrid(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        return sharedPreferences.getInt(EdgeSetting.NUM_OF_GRID_ROW_KEY, 5) * sharedPreferences.getInt(EdgeSetting.NUM_OF_GRID_COLUMN_KEY, 4);
    }

    public static Bitmap getBitmapFromAction(Context context,SharedPreferences sharedPreferences, int actionButton) {
        String action = MainActivity.ACTION_NONE;
        switch (actionButton) {
            case 1:
                action = sharedPreferences.getString(EdgeSetting.ACTION_1_KEY, MainActivity.ACTION_INSTANT_FAVO);
                break;
            case 2:
                action = sharedPreferences.getString(EdgeSetting.ACTION_2_KEY, MainActivity.ACTION_HOME);
                break;
            case 3:
                action = sharedPreferences.getString(EdgeSetting.ACTION_3_KEY, MainActivity.ACTION_BACK);
                break;
            case 4:
                action = sharedPreferences.getString(EdgeSetting.ACTION_4_KEY, MainActivity.ACTION_NOTI);
                break;
        }
        switch (action) {
            case MainActivity.ACTION_HOME:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_home);
            case MainActivity.ACTION_BACK:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_back);
            case MainActivity.ACTION_WIFI:
                if (getWifiState(context)) {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_wifi);
                }else
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_wifi_no_bound_off);

            case MainActivity.ACTION_NOTI:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_expand);
            case MainActivity.ACTION_BLUETOOTH:
                if (getBluetoothState(context)) {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_bluetooth);
                } else {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_bluetooth_no_bound_off);
                }
            case MainActivity.ACTION_ROTATE:
                if (getIsRotationAuto(context)) {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_rotate_no_bound_auto);
                } else {
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_rotate_no_bound_lock);
                }
            case MainActivity.ACTION_POWER_MENU:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_power_menu_no_bound);
            case MainActivity.ACTION_LAST_APP:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_last_app);
            case MainActivity.ACTION_CALL_LOGS:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_call_log);
            case MainActivity.ACTION_CONTACT:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_contact);
            case MainActivity.ACTION_DIAL:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_dial);
            case MainActivity.ACTION_RECENT:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_icon_recent_5122);
            case MainActivity.ACTION_VOLUME:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_volume);
            case MainActivity.ACTION_BRIGHTNESS:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_screen_brightness);
            case MainActivity.ACTION_RINGER_MODE:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_sound_normal);
            case MainActivity.ACTION_INSTANT_FAVO:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_instant_favorite_512);
            case MainActivity.ACTION_NONE:
                return null;

        }
        return null;
    }

    public static Bitmap getBitmapForOuterSetting(Context context,String action) {
        switch (action) {
            case MainActivity.ACTION_HOME:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_home);
            case MainActivity.ACTION_BACK:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_back);
            case MainActivity.ACTION_WIFI:
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_wifi);
            case MainActivity.ACTION_NOTI:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_expand);
            case MainActivity.ACTION_BLUETOOTH:
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_bluetooth);
            case MainActivity.ACTION_ROTATE:
                    return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_rotate_no_bound_auto);
            case MainActivity.ACTION_POWER_MENU:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_power_menu_no_bound);
            case MainActivity.ACTION_LAST_APP:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_last_app);
            case MainActivity.ACTION_CALL_LOGS:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_call_log);
            case MainActivity.ACTION_CONTACT:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_contact);
            case MainActivity.ACTION_DIAL:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_dial);
            case MainActivity.ACTION_RECENT:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_icon_recent_5122);
            case MainActivity.ACTION_VOLUME:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_volume);
            case MainActivity.ACTION_BRIGHTNESS:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_screen_brightness);
            case MainActivity.ACTION_RINGER_MODE:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_sound_normal);
            case MainActivity.ACTION_NONE:
                return null;
            case MainActivity.ACTION_INSTANT_FAVO:
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_instant_favorite_512);
        }
        return null;
    }

    public static String getLabelForOuterSetting(Context context, String action) {
        switch (action) {
            case MainActivity.ACTION_HOME:
                return context.getString(R.string.setting_shortcut_home);
            case MainActivity.ACTION_BACK:
                return context.getString(R.string.setting_shortcut_back);
            case MainActivity.ACTION_WIFI:
                return context.getString(R.string.setting_shortcut_wifi);
            case MainActivity.ACTION_NOTI:
                return context.getString(R.string.setting_shortcut_noti);
            case MainActivity.ACTION_BLUETOOTH:
                return context.getString(R.string.setting_shortcut_bluetooth);
            case MainActivity.ACTION_ROTATE:
                return context.getString(R.string.setting_shortcut_rotation);
            case MainActivity.ACTION_POWER_MENU:
                return context.getString(R.string.setting_shortcut_power_menu);
            case MainActivity.ACTION_LAST_APP:
                return context.getString(R.string.setting_shortcut_last_app);
            case MainActivity.ACTION_CALL_LOGS:
                return context.getString(R.string.setting_shortcut_call_log);
            case MainActivity.ACTION_CONTACT:
                return context.getString(R.string.setting_shortcut_contact);
            case MainActivity.ACTION_DIAL:
                return context.getString(R.string.setting_shortcut_dial);
            case MainActivity.ACTION_RECENT:
                return context.getString(R.string.setting_shortcut_recent);
            case MainActivity.ACTION_VOLUME:
                return context.getString(R.string.setting_shortcut_volume);
            case MainActivity.ACTION_BRIGHTNESS:
                return context.getString(R.string.setting_shortcut_brightness);
            case MainActivity.ACTION_RINGER_MODE:
                return context.getString(R.string.setting_shortcut_ringer_mode);
            case MainActivity.ACTION_NONE:
                return context.getString(R.string.setting_shortcut_none);
            case MainActivity.ACTION_INSTANT_FAVO:
                return context.getString(R.string.setting_shortcut_instant_favorite);
        }
        return context.getString(R.string.setting_shortcut_none);
    }

    public static void homeAction(Context context, View v,String className, String packageName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);


//        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
//        event1.setClassName(className);
//        event1.getText().add("home");
//        event1.setAction(1);
//        event1.setPackageName(packageName);
//        event1.setEnabled(true);
//        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
//        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
//        recordCompat.setSource(v);
//        if (Utility.isAccessibilityEnable(context)) {
//            manager.sendAccessibilityEvent(event1);
//        }else Toast.makeText(context, R.string.ask_user_to_turn_on_accessibility_toast, Toast.LENGTH_LONG).show();
    }



    public static void backAction(Context context, View v, String className, String packageName) {
        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
        event1.setClassName(className);
        event1.getText().add("back");
        event1.setAction(2);
        event1.setPackageName(packageName);
        event1.setEnabled(true);
        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
        recordCompat.setSource(v);
        if (Utility.isAccessibilityEnable(context)) {
            manager.sendAccessibilityEvent(event1);
        }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
    }

    public static void recentAction(Context context, View v, String className, String packageName) {
        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
        event1.setClassName(className);
        event1.getText().add("recent");
        event1.setAction(5);
        event1.setPackageName(packageName);
        event1.setEnabled(true);
        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
        recordCompat.setSource(v);
        if (Utility.isAccessibilityEnable(context)) {
            manager.sendAccessibilityEvent(event1);
        }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
    }

    public static void volumeAction(Context context) {
        Intent intent = new Intent(context, VolumeDialogActivity.class);
//        Intent intent = new Intent(context, ScreenBrightnessDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }

    public static void brightnessAction(Context context) {
        Intent intent = new Intent(context, ScreenBrightnessDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        context.startActivity(intent);
    }

    public static void powerAction(Context context, View v, String className, String packageName) {
        AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
        event1.setClassName(className);
        event1.getText().add("power");
        event1.setAction(3);
        event1.setPackageName(packageName);
        event1.setEnabled(true);
        AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
        recordCompat.setSource(v);
        if (Utility.isAccessibilityEnable(context)) {
            manager.sendAccessibilityEvent(event1);
        }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
    }

    public static void notiAction(Context context, View v, String className, String packageName) {
        Object sbservice =context.getSystemService("statusbar");
        try {
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb;
            if (Build.VERSION.SDK_INT >= 17) {
                showsb = statusbarManager.getMethod("expandNotificationsPanel");
            } else {
                showsb = statusbarManager.getMethod("expand");
            }
            showsb.invoke(sbservice);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "ClassNotFound " + e);
        } catch (NoSuchMethodException e) {
            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
            event1.setClassName(className);
            event1.getText().add("noti");
            event1.setAction(4);
            event1.setPackageName(packageName);
            event1.setEnabled(true);
            AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
            recordCompat.setSource(v);
            if (Utility.isAccessibilityEnable(context)) {
                manager.sendAccessibilityEvent(event1);
            }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
        } catch (IllegalAccessException e) {
            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
            event1.setClassName(className);
            event1.getText().add("noti");
            event1.setAction(4);
            event1.setPackageName(packageName);
            event1.setEnabled(true);
            AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
            recordCompat.setSource(v);
            if (Utility.isAccessibilityEnable(context)) {
                manager.sendAccessibilityEvent(event1);
            }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
            Log.e(TAG, "IllegalAccessException " + e);
        } catch (InvocationTargetException e) {
            AccessibilityEvent event1 = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_TOUCH_INTERACTION_END);
            event1.setClassName(className);
            event1.getText().add("noti");
            event1.setAction(4);
            event1.setPackageName(packageName);
            event1.setEnabled(true);
            AccessibilityManager manager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            AccessibilityRecordCompat recordCompat = AccessibilityEventCompat.asRecord(event1);
            recordCompat.setSource(v);
            if (Utility.isAccessibilityEnable(context)) {
                manager.sendAccessibilityEvent(event1);
            }else Toast.makeText(context,R.string.ask_user_to_turn_on_accessibility_toast,Toast.LENGTH_LONG).show();
            Log.e(TAG, "InvocationTargetException " + e);
        }
    }

    public static void lastAppAction(Context context, String packageName) {
        Intent extApp = null;

        extApp = context.getPackageManager().getLaunchIntentForPackage(packageName);


        if (extApp != null) {
            ComponentName componentName = extApp.getComponent();
//            Intent startApp = new Intent(Intent.ACTION_MAIN, null);
//            startApp.addCategory(Intent.CATEGORY_LAUNCHER);
//            startApp.setComponent(componentName);
//            startApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//            extApp.addFlags(805306368);
//            context.startActivity(extApp);
            Intent startAppIntent = new Intent(Intent.ACTION_MAIN);
            startAppIntent.setComponent(componentName);
            startAppIntent.addFlags(1064960);
            startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startAppIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startAppIntent.setFlags(270532608);
            startAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(startAppIntent);
            Log.e(TAG, "packageToSwitch = " + packageName);
        } else Log.e(TAG, "extApp = null ");
    }

    public static void callLogsAction(Context context) {
        Intent launchCallLog = new Intent(Intent.ACTION_VIEW);
        launchCallLog.setData(CallLog.Calls.CONTENT_URI);
        launchCallLog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(launchCallLog);
    }

    public static void contactAction(Context context) {
        Intent launchContact = new Intent(Intent.ACTION_VIEW);
        launchContact.setData(Uri.parse("content://contacts/people/"));
        launchContact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(launchContact);
    }

    public static int getRingerMode(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (manager.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                return 0;
            case AudioManager.RINGER_MODE_VIBRATE:
                return 1;
            case AudioManager.RINGER_MODE_SILENT:
                return 2;
            default:
                return 0;
        }
    }

    public static void setRinggerMode(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (getRingerMode(context)) {
            case 0:
                manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case 1:
                manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            case 2:
                manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
        }
    }

    public static void dialAction(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    public static void executeAction(Context context, String action, View v, String className, String packageName, String lastAppPackageName) {
        switch (action) {
            case MainActivity.ACTION_HOME:
//                homeAction(context,v,className,packageName);
                homeAction(context, v, className, packageName);
                break;
            case MainActivity.ACTION_BACK:
                backAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_NOTI:
                notiAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_WIFI:
                toggleWifi(context);
                break;
            case MainActivity.ACTION_BLUETOOTH:
                toggleBluetooth(context);
                break;
            case MainActivity.ACTION_ROTATE:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Utility.setAutorotation(context);
                } else {
                    if (Settings.System.canWrite(context)) {
                        Utility.setAutorotation(context);
                    } else {
                        Intent notiIntent = new Intent();
                        notiIntent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        PendingIntent notiPending = PendingIntent.getActivity(context, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        builder.setContentTitle(context.getString(R.string.ask_for_write_setting_notification_title)).setContentText(context.getString(R.string.ask_for_write_setting_notification_text)).setSmallIcon(R.drawable.ic_settings_white_36px)
                                .setContentIntent(notiPending)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND);
                        Notification notification = builder.build();
                        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
                        notificationManager.notify(22, notification);
                    }
                }
                break;
            case MainActivity.ACTION_NONE:
                //nothing
                break;
            case MainActivity.ACTION_POWER_MENU:
                powerAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_LAST_APP:
                lastAppAction(context, lastAppPackageName);
                break;
            case MainActivity.ACTION_CONTACT:
                contactAction(context);
                break;
            case MainActivity.ACTION_CALL_LOGS:
                callLogsAction(context);
                break;
            case MainActivity.ACTION_DIAL:
                dialAction(context);
                break;
            case MainActivity.ACTION_RECENT:
                recentAction(context,v,className,packageName);
                break;
            case MainActivity.ACTION_VOLUME:
                volumeAction(context);
                break;
            case MainActivity.ACTION_BRIGHTNESS:
                brightnessAction(context);
                break;
            case MainActivity.ACTION_RINGER_MODE:
                setRinggerMode(context);
                break;
        }
    }

    public static View disPlayClock(Context context, WindowManager windowManager, boolean isAnimation, int animationTime, boolean disableClock) {
        if (animationTime <= 50) {
            isAnimation = false;
        }
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.clock, null);
        LinearLayout clock = (LinearLayout) view.findViewById(R.id.clock_linear_layout);


        if (!disableClock) {
            Calendar c = Calendar.getInstance();
            int mHour;
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMMM");
            clock.setVisibility(View.VISIBLE);
            TextView hourTextView = (TextView) view.findViewById(R.id.clock_time_in_hour);
            TextView dateTextView = (TextView) view.findViewById(R.id.clock_time_in_date);
            TextView batteryLifeTextView = (TextView) view.findViewById(R.id.clock_battery_life);
            String batteryString = context.getString(R.string.batterylife) + " " + getBatteryLevel(context) + "%";
            if (batteryLifeTextView != null) {
                batteryLifeTextView.setText(batteryString);
            }
            if (dateTextView != null) {
                dateTextView.setText(dateFormat.format(c.getTime()));
            }
            if (!DateFormat.is24HourFormat(context)) {
                SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm");
                if (hourTextView != null) {
                    hourTextView.setText(hourFormat.format(c.getTime()));
                }
            } else {
                SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
                if (hourTextView != null) {
                    hourTextView.setText(hourFormat.format(c.getTime()));
                }
            }
        } else {
            clock.setVisibility(View.GONE);
        }


        WindowManager.LayoutParams paramsEdge1 = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                PixelFormat.TRANSLUCENT);
        paramsEdge1.gravity = Gravity.TOP|Gravity.CENTER_HORIZONTAL;
        windowManager.addView(view, paramsEdge1);
        if (isAnimation) {
            view.setAlpha(0f);
            view.animate().alpha(1f).setDuration(animationTime).setInterpolator(new FastOutSlowInInterpolator());

        }
        return view;

    }
    public static int getBatteryLevel(Context context) {
        Intent batteryIntent =context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level,scale;
        try {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        } catch (NullPointerException e) {
            Log.e(TAG, "Null when get battery life");
            return 50;
        }
        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50;
        }

        return((int) (((float)level / (float)scale) * 100.0f));
    }

    public static String getForegroundApp(Context context) {
        ActivityManager.RunningAppProcessInfo resultInfo=null, info=null;
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List <ActivityManager.RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        Iterator<ActivityManager.RunningAppProcessInfo> i = l.iterator();
        Log.e(TAG, "list size = " + l.size());
        while(i.hasNext()){
            info = i.next();
            if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    ) {
                if (!isRunningService(context, info.processName, mActivityManager)) {
                    resultInfo = info;
                    break;
                } else {
                    Log.e(TAG, "info = " + info.processName);
                }
            }
        }
        if (resultInfo != null && resultInfo.importanceReasonComponent!= null) {
            String packageReturn = resultInfo.importanceReasonComponent.getPackageName();
            if (!packageReturn.isEmpty()) {
                return packageReturn;
            }else return null;
        }else return null;

    }

    public static boolean isRunningService(Context mContext, String processname, ActivityManager mActivityManager){
        if(processname==null || processname.isEmpty())
            return false;

        ActivityManager.RunningServiceInfo service;

        if(mActivityManager==null)
            mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List <ActivityManager.RunningServiceInfo> l = mActivityManager.getRunningServices(9999);
        Iterator <ActivityManager.RunningServiceInfo> i = l.iterator();
        while(i.hasNext()){
            service = i.next();
            if(service.process.equals(processname))
                return true;
        }

        return false;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

//    public static void setShortcutDrawable(Shortcut shortcut, Context mContext, ImageView imageView, IconPackManager.IconPack iconPack, boolean folderMode) {
//        if (shortcut == null) {
//            if (!folderMode) {
//                imageView.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
//            }
////            imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//        } else {
//            if (shortcut.getType() == Shortcut.TYPE_APP) {
//                imageView.setColorFilter(null);
//                try {
//                    Drawable defaultDrawable = mContext.getPackageManager().getApplicationIcon(shortcut.getPackageName());
//                    if (iconPack!=null) {
//                        imageView.setImageDrawable(iconPack.getDrawableIconForPackage(shortcut.getPackageName(), defaultDrawable));
//                    } else {
//                        imageView.setImageDrawable(defaultDrawable);
//                    }
//                } catch (PackageManager.NameNotFoundException e) {
//                    Log.e(TAG, "NameNotFound " + e);
//                }
//            }else if (shortcut.getType() == Shortcut.TYPE_ACTION) {
//                switch (shortcut.getAction()) {
//                    case Shortcut.ACTION_WIFI:
//                        if (getWifiState(mContext)) {
//                            imageView.setImageResource(R.drawable.ic_wifi);
//                        } else {
//                            imageView.setImageResource(R.drawable.ic_wifi_off);
//                        }
//
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color. black));
//                        break;
//                    case Shortcut.ACTION_BLUETOOTH:
//                        if (getBluetoothState(mContext)) {
//                            imageView.setImageResource(R.drawable.ic_bluetooth);
//                        } else {
//                            imageView.setImageResource(R.drawable.ic_bluetooth_off);
//                        }
//
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_ROTATION:
//                        if (getIsRotationAuto(mContext)) {
//                            imageView.setImageResource(R.drawable.ic_rotation);
//                        } else {
//                            imageView.setImageResource(R.drawable.ic_rotation_lock);
//                        }
//
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_POWER_MENU:
//                        imageView.setImageResource(R.drawable.ic_power_menu);
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_HOME:
//                        imageView.setImageResource(R.drawable.ic_home);
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_BACK:
//                        imageView.setImageResource(R.drawable.ic_back);
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_NOTI:
//                        imageView.setImageResource(R.drawable.ic_notification);
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_LAST_APP:
//                        imageView.setImageResource(R.drawable.ic_last_app);
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_CALL_LOGS:
//                        imageView.setImageResource(R.drawable.ic_call_log);
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_DIAL:
//                        imageView.setImageResource(R.drawable.ic_dial);
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_CONTACT:
//                        imageView.setImageResource(R.drawable.ic_contact);
////                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
//                        break;
//                    case Shortcut.ACTION_RECENT:
//                        imageView.setImageResource(R.drawable.ic_recent);
//                        break;
//                    case Shortcut.ACTION_VOLUME:
//                        imageView.setImageResource(R.drawable.ic_volume);
//                        break;
//                    case Shortcut.ACTION_BRIGHTNESS:
//                        imageView.setImageResource(R.drawable.ic_screen_brightness);
//                        break;
//                    case Shortcut.ACTION_RINGER_MODE:
//                        switch (getRingerMode(mContext)) {
//                            case 0:
//                                imageView.setImageResource(R.drawable.ic_sound_normal);
//                                break;
//                            case 1:
//                                imageView.setImageResource(R.drawable.ic_sound_vibrate);
//                                break;
//                            case 2:
//                                imageView.setImageResource(R.drawable.ic_sound_silent);
//                                break;
//                        }
//                        break;
//                    case Shortcut.ACTION_NONE:
//                        imageView.setImageDrawable(null);
//                }
//            }else if (shortcut.getType() == Shortcut.TYPE_FOLDER) {
//                File myDir = mContext.getFilesDir();
//                String fname = "folder-"+ shortcut.getId() +".png";
//                File file = new File (myDir, fname);
//                if (!file.exists()) {
//                    imageView.setImageBitmap(null);
//                } else {
//                    try {
//                        imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
//                    } catch (Exception e) {
//                        Log.e(TAG, "read thumbnail exeption" + e);
//                        e.printStackTrace();
//                    }
//                }
//
//            } else if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
//                String thumbnaiUri = shortcut.getThumbnaiUri();
//                if (thumbnaiUri != null) {
//                    try {
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(thumbnaiUri));
//                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
//                        drawable.setCircular(true);
//                        imageView.setImageDrawable(drawable);
//                        imageView.setColorFilter(null);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        imageView.setImageResource(R.drawable.ic_contact_default);
//                    }
//                } else {
//                    imageView.setImageResource(R.drawable.ic_contact_default);
//
//                }
//            }
//
//        }
//
//    }

    public static void startShortcut(Context context, Shortcut shortcut, View v, String className, String packageName, String lastAppPackageName, int contactAction) {
        if (shortcut.getType() == Shortcut.TYPE_APP) {
            Intent extApp;
            extApp =context.getPackageManager().getLaunchIntentForPackage(shortcut.getPackageName());
            if (extApp != null) {
                if (shortcut.getPackageName().equals("com.devhomc.search")) {
                    extApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(extApp);
                } else {
                    ComponentName componentName = extApp.getComponent();
//                                    Intent startApp = new Intent(Intent.ACTION_MAIN, null);
//                                    startApp.addCategory(Intent.CATEGORY_LAUNCHER);
//                                    startApp.setComponent(componentName);
//                                    startApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                    extApp.addFlags(805306368);
                    Intent startAppIntent = new Intent(Intent.ACTION_MAIN);
                    startAppIntent.setComponent(componentName);
                    startAppIntent.addFlags(1064960);
                    startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startAppIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startAppIntent.setFlags(270532608 | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    context.startActivity(startAppIntent);
//                                    startActivity(extApp);
                }

            } else {
                Log.e(TAG, "extApp of shortcut = null ");
            }
        } else if (shortcut.getType() == Shortcut.TYPE_ACTION) {
            switch (shortcut.getAction()) {
                case Shortcut.ACTION_WIFI:
                    Utility.toggleWifi(context);
                    break;
                case Shortcut.ACTION_BLUETOOTH:
                    Utility.toggleBluetooth(context);
                    break;
                case Shortcut.ACTION_ROTATION:
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        Utility.setAutorotation(context);
                    } else {
                        if (Settings.System.canWrite(context)) {
                            Utility.setAutorotation(context);
                        } else {
                            Intent notiIntent = new Intent();
                            notiIntent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            PendingIntent notiPending = PendingIntent.getActivity(context, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                            builder.setContentTitle(context.getString(R.string.ask_for_write_setting_notification_title)).setContentText(context.getString(R.string.ask_for_write_setting_notification_text)).setSmallIcon(R.drawable.ic_settings_white_36px)
                                    .setContentIntent(notiPending)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setDefaults(NotificationCompat.DEFAULT_SOUND);
                            Notification notification = builder.build();
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                            notificationManager.notify(22, notification);
                        }
                    }

                    break;
                case Shortcut.ACTION_POWER_MENU:
                    powerAction(context, v, className, packageName);
                    break;
                case Shortcut.ACTION_HOME:
                    Utility.homeAction(context, v, className, packageName);
                    break;
                case Shortcut.ACTION_BACK:
                    Utility.backAction(context, v, className, packageName);
                    break;
                case Shortcut.ACTION_NOTI:
                    Utility.notiAction(context, v, className, packageName);
                    break;
                case Shortcut.ACTION_LAST_APP:
                    Utility.lastAppAction(context, lastAppPackageName);
                    break;
                case Shortcut.ACTION_CALL_LOGS:
                    Utility.callLogsAction(context);
                    break;
                case Shortcut.ACTION_DIAL:
                    Log.e(TAG, "startShortcut: Start dial");
                    Utility.dialAction(context);
                    break;
                case Shortcut.ACTION_CONTACT:
                    Utility.contactAction(context);
                    break;
                case Shortcut.ACTION_RECENT:
                    Utility.recentAction(context, v, className, packageName);
                    break;
                case Shortcut.ACTION_VOLUME:
                    Utility.volumeAction(context);
                    break;
                case Shortcut.ACTION_BRIGHTNESS:
                    Utility.brightnessAction(context);
                    break;
                case Shortcut.ACTION_RINGER_MODE:
                    Utility.setRinggerMode(context);
                    break;
                case Shortcut.ACTION_NONE:
                    break;

            }
        } else if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
            switch (contactAction) {
                case EdgeSetting.ACTION_CHOOSE:
                    Intent intent = new Intent(context, ChooseActionDialogActivity.class);
                    intent.putExtra("number", shortcut.getNumber());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    context.startActivity(intent);
                    break;
                case EdgeSetting.ACTION_CALL:
                    String url = "tel:"+ shortcut.getNumber();
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(callIntent);
                    } else {
                        Toast.makeText(context, context.getString(R.string.missing_call_phone_permission), Toast.LENGTH_LONG).show();
                    }
                    break;
                case EdgeSetting.ACTION_SMS:
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                            + shortcut.getNumber()));
                    smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(smsIntent);
            break;
            }

        } else if (shortcut.getType() == Shortcut.TYPE_SHORTCUT) {
            try {
                Intent intent = Intent.parseUri(shortcut.getIntent(), 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "startShortcut: exception when start Shortcut shortcut");
            }

        }
    }

    public static void setIndicatorForQuickAction(SharedPreferences sharedPreferences, Context context, int homeBackNoti, ImageView imageView, TextView label) {
        String action = MainActivity.ACTION_NONE;
        switch (homeBackNoti) {
            case 1:
                action = sharedPreferences.getString(EdgeSetting.ACTION_1_KEY, MainActivity.ACTION_INSTANT_FAVO);
                break;
            case 2:
                action = sharedPreferences.getString(EdgeSetting.ACTION_2_KEY, MainActivity.ACTION_HOME);
                break;
            case 3:
                action = sharedPreferences.getString(EdgeSetting.ACTION_3_KEY, MainActivity.ACTION_BACK);
                break;
            case 4:
                action = sharedPreferences.getString(EdgeSetting.ACTION_4_KEY, MainActivity.ACTION_NOTI);
                break;
        }
        label.setText(getLabelForOuterSetting(context,action));
        switch (action) {
            case MainActivity.ACTION_WIFI:
                if (getWifiState(context)) {
                    imageView.setImageResource(R.drawable.ic_wifi);
                } else {
                    imageView.setImageResource(R.drawable.ic_wifi_off);
                }

                break;
            case MainActivity.ACTION_BLUETOOTH:
                if (getBluetoothState(context)) {
                    imageView.setImageResource(R.drawable.ic_bluetooth);
                } else {
                    imageView.setImageResource(R.drawable.ic_bluetooth_off);
                }

                break;
            case MainActivity.ACTION_ROTATE:
                if (getIsRotationAuto(context)) {
                    imageView.setImageResource(R.drawable.ic_rotation);
                } else {
                    imageView.setImageResource(R.drawable.ic_rotation_lock);
                }

                break;
            case MainActivity.ACTION_POWER_MENU:
                imageView.setImageResource(R.drawable.ic_power_menu);
                break;
            case MainActivity.ACTION_HOME:
                imageView.setImageResource(R.drawable.ic_home);
                break;
            case MainActivity.ACTION_BACK:
                imageView.setImageResource(R.drawable.ic_back);
                break;
            case MainActivity.ACTION_NOTI:
                imageView.setImageResource(R.drawable.ic_notification);
                break;
            case MainActivity.ACTION_LAST_APP:
                imageView.setImageResource(R.drawable.ic_last_app);
                break;
            case MainActivity.ACTION_CALL_LOGS:
                imageView.setImageResource(R.drawable.ic_call_log);
                break;
            case MainActivity.ACTION_DIAL:
                imageView.setImageResource(R.drawable.ic_dial);
                break;
            case MainActivity.ACTION_CONTACT:
                imageView.setImageResource(R.drawable.ic_contact);
                break;
            case MainActivity.ACTION_RECENT:
                imageView.setImageResource(R.drawable.ic_recent);
                break;
            case MainActivity.ACTION_VOLUME:
                imageView.setImageResource(R.drawable.ic_volume);
                break;
            case MainActivity.ACTION_BRIGHTNESS:
                imageView.setImageResource(R.drawable.ic_screen_brightness);
                break;
            case MainActivity.ACTION_RINGER_MODE:
                imageView.setImageResource(R.drawable.ic_sound_normal);
                break;
            case MainActivity.ACTION_NONE:
                imageView.setImageDrawable(null);
        }


    }

    public static Drawable getDrawableForAction(Context context, int action) {
        switch (action) {
            case Shortcut.ACTION_WIFI:
                return ContextCompat.getDrawable(context, R.drawable.ic_wifi);
            case Shortcut.ACTION_BLUETOOTH:
                return ContextCompat.getDrawable(context, R.drawable.ic_bluetooth);
            case Shortcut.ACTION_ROTATION:
                return ContextCompat.getDrawable(context, R.drawable.ic_rotation);
            case Shortcut.ACTION_POWER_MENU:
                return ContextCompat.getDrawable(context, R.drawable.ic_power_menu);
            case Shortcut.ACTION_HOME:
                return ContextCompat.getDrawable(context, R.drawable.ic_home);
            case Shortcut.ACTION_BACK:
                return ContextCompat.getDrawable(context, R.drawable.ic_back);
            case Shortcut.ACTION_NOTI:
                return ContextCompat.getDrawable(context, R.drawable.ic_notification);
            case Shortcut.ACTION_LAST_APP:
                return ContextCompat.getDrawable(context, R.drawable.ic_last_app);
            case Shortcut.ACTION_CALL_LOGS:
                return ContextCompat.getDrawable(context, R.drawable.ic_call_log);
            case Shortcut.ACTION_DIAL:
                return ContextCompat.getDrawable(context, R.drawable.ic_dial);
            case Shortcut.ACTION_CONTACT:
                return ContextCompat.getDrawable(context, R.drawable.ic_contact);
            case Shortcut.ACTION_RECENT:
                return ContextCompat.getDrawable(context, R.drawable.ic_recent);
            case Shortcut.ACTION_VOLUME:
                return ContextCompat.getDrawable(context, R.drawable.ic_volume);
            case Shortcut.ACTION_BRIGHTNESS:
                return ContextCompat.getDrawable(context, R.drawable.ic_screen_brightness);
            case Shortcut.ACTION_RINGER_MODE:
                return ContextCompat.getDrawable(context, R.drawable.ic_sound_normal);
            case Shortcut.ACTION_NONE:
                return null;
        }
        return null;
    }

    public static int[] showFolder(Context context, GridView gridView, WindowManager windowManager, Realm realm,
                                  SharedPreferences sharedPreferences, int mPosition, float mScale, float mIconScale, FolderAdapter adapter) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup viewGroup = (ViewGroup) gridView.getParent();
        float gridX = gridView.getX();
        float gridY = gridView.getY();
        float x = gridView.getChildAt(mPosition).getX()+ gridX;
        float y = gridView.getChildAt(mPosition).getY() + gridY;
        int size = (int) realm.where(Shortcut.class).greaterThan("id",(mPosition+1)*1000 -1).lessThan("id",(mPosition+2)*1000).count();
        if (size == 0) {
            return new int[5];
        }
        int gridColumn = size;
        if (gridColumn > 4) {
            gridColumn = 4;
        }
        int gridRow;
        if ( size % gridColumn == 0) {
            gridRow = size/gridColumn;
        }else gridRow = size/gridColumn +1;
        int gridGap = 5;


        gridView.setVisibility(View.GONE);
        GridView folderGrid = (GridView) viewGroup.findViewById(R.id.folder_grid);
        ViewGroup.LayoutParams gridParams = folderGrid.getLayoutParams();
        folderGrid.setVerticalSpacing((int) (gridGap * mScale));
        folderGrid.setNumColumns(gridColumn);
        folderGrid.setGravity(Gravity.CENTER);
        float gridWide = (int) (mScale * (float) (((EdgeGestureService.GRID_ICON_SIZE * mIconScale) + EdgeGestureService.GRID_2_PADDING) * gridColumn + gridGap * (gridColumn - 1)));
        float gridTall = (int) (mScale * (float) (((EdgeGestureService.GRID_ICON_SIZE * mIconScale) + EdgeGestureService.GRID_2_PADDING) * gridRow + gridGap * (gridRow - 1)));
        gridParams.height = (int) gridTall;
        gridParams.width = (int) gridWide;
        folderGrid.setLayoutParams(gridParams);
        folderGrid.setAdapter(adapter);
        if (x - gridWide / 2 + gridWide > gridX + gridView.getWidth()) {
            folderGrid.setX(gridX + gridView.getWidth() - gridWide);
        } else if (x - gridWide / 2 < 10 * mScale) {
            folderGrid.setX(10*mScale);
        } else {
            folderGrid.setX(x - gridWide / 2);
        }

        folderGrid.setY(y - gridTall + gridTall/gridRow);
        Log.e(TAG,"gridX = " + gridX + "\nGridY = " + gridY +  "\nfolder x = " + folderGrid.getX() + "\nfolder y= " + folderGrid.getY() );
        folderGrid.setVisibility(View.VISIBLE);
        return new int[]{(int) folderGrid.getX(), (int) folderGrid.getY(), gridRow, gridColumn, mPosition};



    }

    public static Bitmap getFolderThumbnail(Realm realm, int mPosition, Context context) {
        Log.e(TAG, "getFolderThumbnail: ");
        float mScale = context. getResources().getDisplayMetrics().density;
        int width =(int)( 48*mScale);
        int height = (int) (48 * mScale);
        int smallWidth, smallHeight;
        smallWidth = width/2;
        smallHeight = height/2;
        int startId = (mPosition +1)* 1000;
        PackageManager packageManager = context.getPackageManager();
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable;
        Shortcut shortcut;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.WHITE);
        int gap1dp = (int) (mScale);
        boolean isFolderEmpty = true;

        for (int i = 0; i < 4; i++) {
            drawable = null;
            shortcut = realm.where(Shortcut.class).equalTo("id",startId + i).findFirst();
            if (shortcut != null && shortcut.getType() == Shortcut.TYPE_APP) {
                isFolderEmpty = false;
                try {
//                    bitmap1 = drawableToBitmap(packageManager.getApplicationIcon(shortcut.getPackageName()));
                    drawable = packageManager.getApplicationIcon(shortcut.getPackageName());
//                    bitmap1 = ((BitmapDrawable)(drawable)).getBitmap();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (drawable != null) {
                    switch (i) {
                        case 0:
                            drawable.setBounds(0,0,smallWidth - gap1dp,smallHeight - gap1dp);
                            drawable.draw(canvas);
                            break;
                        case 1:
                            drawable.setBounds(smallWidth+ gap1dp,0,width,smallHeight - gap1dp);
                            drawable.draw(canvas);
                            break;
                        case 2:
                            drawable.setBounds(0,smallHeight+ gap1dp,smallWidth - gap1dp,height);
                            drawable.draw(canvas);
                            break;
                        case 3:
                            drawable.setBounds(smallWidth+ gap1dp,smallHeight + gap1dp,width,height);
                            drawable.draw(canvas);
                            break;
                    }
                }
            } else if (shortcut != null && shortcut.getType() == Shortcut.TYPE_ACTION) {
                isFolderEmpty = false;
                drawable = getDrawableForAction(context, shortcut.getAction());
                if (drawable != null) {
                    switch (i) {
                        case 0:
                            drawable.setBounds(0,0,smallWidth - gap1dp,smallHeight - gap1dp);
                            drawable.draw(canvas);
                            break;
                        case 1:
                            drawable.setBounds(smallWidth+ gap1dp,0,width,smallHeight - gap1dp);
                            drawable.draw(canvas);
                            break;
                        case 2:
                            drawable.setBounds(0,smallHeight+ gap1dp,smallWidth - gap1dp,height);
                            drawable.draw(canvas);
                            break;
                        case 3:
                            drawable.setBounds(smallWidth+ gap1dp,smallHeight + gap1dp,width,height);
                            drawable.draw(canvas);
                            break;
                    }
                }
            }else if (shortcut != null && shortcut.getType() == Shortcut.TYPE_CONTACT) {
                isFolderEmpty = false;
                String uri = shortcut.getThumbnaiUri();
                if (uri != null) {
                    try {
                        Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));
                        drawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap1);
                        ((RoundedBitmapDrawable) drawable).setCircular(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                        drawable = ContextCompat. getDrawable(context , R.drawable.ic_contact_default);
//                        drawable = context.getDrawable(R.drawable.ic_contact_default);
                    }
                } else {
                    drawable = ContextCompat. getDrawable(context , R.drawable.ic_contact_default);
//                    drawable = context.getDrawable(R.drawable.ic_contact_default);
                }
                if (drawable != null) {
                    switch (i) {
                        case 0:
                            drawable.setBounds(0,0,smallWidth - gap1dp,smallHeight - gap1dp);
                            drawable.draw(canvas);
                            break;
                        case 1:
                            drawable.setBounds(smallWidth+ gap1dp,0,width,smallHeight - gap1dp);
                            drawable.draw(canvas);
                            break;
                        case 2:
                            drawable.setBounds(0,smallHeight+ gap1dp,smallWidth - gap1dp,height);
                            drawable.draw(canvas);
                            break;
                        case 3:
                            drawable.setBounds(smallWidth+ gap1dp,smallHeight + gap1dp,width,height);
                            drawable.draw(canvas);
                            break;
                    }
                }
            } else if (shortcut != null && shortcut.getType() == Shortcut.TYPE_SHORTCUT) {
                Log.e(TAG, "getFolderThumbnail: draw shortcut");
                isFolderEmpty = false;
                byte[] byteArray = shortcut.getBitmap();
                try {
                    Bitmap bmp;
                    Resources resources = packageManager.getResourcesForApplication(shortcut.getPackageName());
                    if (byteArray != null) {

                        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        drawable = new BitmapDrawable(resources, bmp);

                    } else {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        drawable =  resources.getDrawable(shortcut.getResId(), null);
//                        options.inMutable = true;
//                        bmp =BitmapFactory.decodeResource(resources,shortcut.getResId(), options);
//                        Log.e(TAG, "getFolderThumbnail: resource");
                    }
                    if (drawable != null) {
                        switch (i) {
                            case 0:
                                drawable.setBounds(0,0,smallWidth - gap1dp,smallHeight - gap1dp);
                                drawable.draw(canvas);
                                break;
                            case 1:
                                drawable.setBounds(smallWidth+ gap1dp,0,width,smallHeight - gap1dp);
                                drawable.draw(canvas);
                                break;
                            case 2:
                                drawable.setBounds(0,smallHeight+ gap1dp,smallWidth - gap1dp,height);
                                drawable.draw(canvas);
                                break;
                            case 3:
                                drawable.setBounds(smallWidth+ gap1dp,smallHeight + gap1dp,width,height);
                                drawable.draw(canvas);
                                break;
                        }
                    }
//                    if (bmp != null) {
//
//                        bmp = Bitmap.createScaledBitmap(bmp, smallWidth - gap1dp, smallHeight - gap1dp, false);
//                        switch (i) {
//                            case 0:
//                                canvas.drawBitmap(bmp,0,0,paint);
//                                break;
//                            case 1:
//                                canvas.drawBitmap(bmp,smallWidth + gap1dp, 0,paint);
//                                break;
//                            case 2:
//                                canvas.drawBitmap(bmp, 0,smallHeight + gap1dp,paint);
//                                break;
//                            case 3:
//                                canvas.drawBitmap(bmp,smallWidth + gap1dp, smallHeight +gap1dp,paint);
//                                break;
//                        }
//                    }

                } catch (Exception e) {
                    Log.e(TAG, "getView: can not set imageview for shortcut shortcut");
                }
            }
        }
        if (isFolderEmpty) {
//            drawable = context.getDrawable(R.drawable.ic_folder);
            drawable = ContextCompat. getDrawable(context , R.drawable.ic_folder);
            if (drawable != null) {
                drawable.setBounds(0, 0, width, height);
                drawable.draw(canvas);
            }

        }
        File myDir = context.getFilesDir();
        String fname = "folder-"+ mPosition +".png";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static void saveShortcutBitmap(Bitmap bmp, int mPosition, Context context) {
        Log.e(TAG, "getShortcutBitmap: ");
        File myDir = context.getFilesDir();
        String fname = "shortcut-"+ mPosition +".png";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setImageForShortcut(Shortcut shortcut, PackageManager packageManager, ImageView imageView, Context mContext, IconPackManager.IconPack iconPack, Realm myRealm, boolean showOnOff) {

        if (shortcut.getType() == Shortcut.TYPE_APP) {
            try {
                Drawable defaultDrawable = mContext.getPackageManager().getApplicationIcon(shortcut.getPackageName());
                if (iconPack!=null) {
                    imageView.setImageDrawable(iconPack.getDrawableIconForPackage(shortcut.getPackageName(), defaultDrawable));
                } else {
                    imageView.setImageDrawable(defaultDrawable);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "NameNotFound " + e);
            }
        }else if (shortcut.getType() == Shortcut.TYPE_ACTION) {
            switch (shortcut.getAction()) {
                case Shortcut.ACTION_WIFI:
                    if (showOnOff) {
                        if (Utility.getWifiState(mContext)) {
                            imageView.setImageResource(R.drawable.ic_wifi);
                        } else {
                            imageView.setImageResource(R.drawable.ic_wifi_off);
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_wifi);
                    }
                    break;
                case Shortcut.ACTION_BLUETOOTH:
                    if (showOnOff) {
                        if (Utility.getBluetoothState(mContext)) {
                            imageView.setImageResource(R.drawable.ic_bluetooth);
                        } else {
                            imageView.setImageResource(R.drawable.ic_bluetooth_off);
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_bluetooth);
                    }

                    break;
                case Shortcut.ACTION_ROTATION:
                    if (showOnOff) {
                        if (Utility.getIsRotationAuto(mContext)) {
                            imageView.setImageResource(R.drawable.ic_rotation);
                        } else {
                            imageView.setImageResource(R.drawable.ic_rotation_lock);
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_rotation);
                    }
                    break;
                case Shortcut.ACTION_POWER_MENU:
                    imageView.setImageResource(R.drawable.ic_power_menu);
                    break;
                case Shortcut.ACTION_HOME:
                    imageView.setImageResource(R.drawable.ic_home);
                    break;
                case Shortcut.ACTION_BACK:
                    imageView.setImageResource(R.drawable.ic_back);
                    break;
                case Shortcut.ACTION_NOTI:
                    imageView.setImageResource(R.drawable.ic_notification);
                    break;
                case Shortcut.ACTION_LAST_APP:
                    imageView.setImageResource(R.drawable.ic_last_app);
                    break;
                case Shortcut.ACTION_CALL_LOGS:
                    imageView.setImageResource(R.drawable.ic_call_log);
                    break;
                case Shortcut.ACTION_DIAL:
                    imageView.setImageResource(R.drawable.ic_dial);
                    break;
                case Shortcut.ACTION_CONTACT:
                    imageView.setImageResource(R.drawable.ic_contact);
                    break;
                case Shortcut.ACTION_RECENT:
                    imageView.setImageResource(R.drawable.ic_recent);
                    break;
                case Shortcut.ACTION_VOLUME:
                    imageView.setImageResource(R.drawable.ic_volume);
                    break;
                case Shortcut.ACTION_BRIGHTNESS:
                    imageView.setImageResource(R.drawable.ic_screen_brightness);
                    break;
                case Shortcut.ACTION_RINGER_MODE:
                    if (showOnOff) {
                        switch (Utility.getRingerMode(mContext)) {
                            case 0:
                                imageView.setImageResource(R.drawable.ic_sound_normal);
                                break;
                            case 1:
                                imageView.setImageResource(R.drawable.ic_sound_vibrate);
                                break;
                            case 2:
                                imageView.setImageResource(R.drawable.ic_sound_silent);
                                break;
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_sound_normal);
                    }

                    break;
                case Shortcut.ACTION_NONE:
                    imageView.setImageDrawable(null);
            }
        } else if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
            String thumbnaiUri = shortcut.getThumbnaiUri();
            if (thumbnaiUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(thumbnaiUri));
                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
                    drawable.setCircular(true);
                    imageView.setImageDrawable(drawable);
                    imageView.setColorFilter(null);
                } catch (IOException e) {
                    e.printStackTrace();
                    imageView.setImageResource(R.drawable.ic_contact_default);
//                        imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                } catch (SecurityException e) {
                    Toast.makeText(mContext, mContext.getString(R.string.missing_contact_permission), Toast.LENGTH_LONG).show();
                }
            } else {
                imageView.setImageResource(R.drawable.ic_contact_default);
//                    imageView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));

            }
        } else if (shortcut.getType() == Shortcut.TYPE_FOLDER) {
            File myDir = mContext.getFilesDir();
            String fname = "folder-"+ shortcut.getId() +".png";
            File file = new File (myDir, fname);
            if (!file.exists()) {
                imageView.setImageBitmap(Utility.getFolderThumbnail(myRealm, shortcut.getId(), mContext));
            } else {
                try {
                    imageView.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                } catch (Exception e) {
                    Log.e(TAG, "read thumbnail exeption" + e);
                    e.printStackTrace();
                }
            }
        } else if (shortcut.getType() == Shortcut.TYPE_SHORTCUT) {
            byte[] byteArray = shortcut.getBitmap();
            try {
                if (byteArray != null) {
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                } else {
                    Resources resources = packageManager.getResourcesForApplication(shortcut.getPackageName());
                    imageView.setImageBitmap(BitmapFactory.decodeResource(resources,shortcut.getResId()));
                }

            } catch (Exception e) {
                Log.e(TAG, "getView: can not set imageview for shortcut shortcut");
            }
        }
        imageView.setColorFilter(null);
    }


    public static boolean checkDrawPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    public static String getLabelForShortcut(Context context, Shortcut shortcut) {
        if (shortcut != null) {
            if (shortcut.getType() == Shortcut.TYPE_CONTACT) {
                return shortcut.getName();
            } else if (shortcut.getType() == Shortcut.TYPE_APP) {
                try {
                    return(String) context.getPackageManager().getApplicationLabel(context.getPackageManager().getApplicationInfo(shortcut.getPackageName(), 0));
                } catch (PackageManager.NameNotFoundException e) {
                    return "";
                }
            } else if (shortcut.getAction() != Shortcut.ACTION_NONE) {
                return shortcut.getLabel();
            } else return "";

        } else return "";
    }




}
