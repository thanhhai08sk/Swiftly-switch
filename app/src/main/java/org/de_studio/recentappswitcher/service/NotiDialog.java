package org.de_studio.recentappswitcher.service;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.shortcut.LockAdmin;

/**
 * Created by HaiNguyen on 7/30/16.
 */
public class NotiDialog extends AppCompatActivity {
    public static final int OUT_OF_TRIAL = 0;
    public static final int WRITE_SETTING_PERMISSION = 1;
    public static final int PHONE_ADMIN_PERMISSION = 2;
    public static final int ACCESSIBILITY_PERMISSION = 3;
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


        switch (type) {
            case OUT_OF_TRIAL:
                titleRes = R.string.out_of_trial;
                textRes = R.string.edge_service_out_of_trial_text_when_homebacknoti;
                buttonTextRes = R.string.main_buy_pro_button_text;
                Uri uri = Uri.parse("mbarket://details?id=" + MainActivity.PRO_VERSION_PACKAGE_NAME);
                buttonIntent = new Intent(Intent.ACTION_VIEW, uri);
                buttonIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                break;
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
                textRes = R.string.admin_permission_explain;
                buttonTextRes = R.string.go_to_setting;
                ComponentName cm = new ComponentName(getApplicationContext(), LockAdmin.class);
                buttonIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                buttonIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cm);
                buttonIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        getString(R.string.admin_desc));
                break;
            case ACCESSIBILITY_PERMISSION:
                titleRes = R.string.enable_accessibility_permission;
                textRes = R.string.ask_user_to_turn_on_accessibility_toast;
                buttonTextRes = R.string.go_to_setting;
                buttonIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                break;

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(buttonTextRes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(buttonIntent);
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