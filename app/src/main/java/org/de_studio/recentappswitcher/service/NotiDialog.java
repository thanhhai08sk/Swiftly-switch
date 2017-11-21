package org.de_studio.recentappswitcher.service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.main.MainView;

/**
 * Created by HaiNguyen on 7/30/16.
 */
public class NotiDialog extends AppCompatActivity {
    public static final int OUT_OF_TRIAL = 0;
    public static final int WRITE_SETTING_PERMISSION = 1;
    public static final int PHONE_ADMIN_PERMISSION = 2;
    public static final int ACCESSIBILITY_PERMISSION = 3;
    public static final int DRAW_OVER_OTHER_APP = 4;
    public static final int NOTIFICATION_INFO = 5;
    private int type;
    public static final String TYPE_KEY = "type";
    private Intent buttonIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getIntExtra(TYPE_KEY, 0);
        int titleRes = 0;
        int textRes = 0;
        int buttonTextRes= 0;


        if (type == NOTIFICATION_INFO) {

            new MaterialDialog.Builder(this)
                    .title(R.string.notification)
                    .content(R.string.notification_description)
                    .positiveText(R.string.hide_notification)
                    .negativeText(R.string.pause_resume)
                    .neutralText(R.string.open_swiftly_switch)
                    .onAny(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (which.equals(DialogAction.POSITIVE)) {
                                Utility.hideNotification(NotiDialog.this);
                            } else if (which.equals(DialogAction.NEGATIVE)) {
                                Utility.pauseEdgeService(NotiDialog.this);
                            } else if (which.equals(DialogAction.NEUTRAL)) {
                                startActivity(new Intent(NotiDialog.this, MainView.class));
                            }
                        }
                    })
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            finish();
                        }
                    })
                    .show();

        } else {
            switch (type) {
                case WRITE_SETTING_PERMISSION:
                    titleRes = R.string.write_setting_permission;
                    textRes = R.string.write_setting_permission_explain;
                    buttonTextRes = R.string.go_to_setting;
                    buttonIntent = new Intent();
                    buttonIntent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                buttonIntent.setAction(Settings.ACTION_VOICE_INPUT_SETTINGS);
                    buttonIntent.setData(Uri.parse("package:" + getPackageName()));

                    break;
                case PHONE_ADMIN_PERMISSION:
                    titleRes = R.string.admin_permission;
                    textRes = R.string.admin_permission_notice_2;
                    buttonTextRes = R.string.button_close;
//                    ComponentName cm = new ComponentName(getApplicationContext(), LockAdmin.class);
//                    buttonIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                    buttonIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cm);
//                    buttonIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                            getString(R.string.admin_permission_notice));
                    break;
                case ACCESSIBILITY_PERMISSION:
                    titleRes = R.string.enable_accessibility_permission;
                    textRes = R.string.accessibility_service_description;
                    buttonTextRes = R.string.go_to_setting;
                    buttonIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    break;
                case DRAW_OVER_OTHER_APP:
                    titleRes = R.string.set_required_permissions;
                    textRes = R.string.permission_used_for_showing_icons_over_other_apps;
                    buttonTextRes = R.string.go_to_setting;
                    buttonIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    break;

            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton(buttonTextRes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (buttonIntent != null) {
                        startActivity(buttonIntent);
                    }
                }
            })
                    .setTitle(titleRes)
                    .setMessage(textRes)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }







    }

    public static Intent getIntent(Context context, int type) {
        Intent intent = new Intent(context, NotiDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra(NotiDialog.TYPE_KEY, type);
        return intent;
    }

}