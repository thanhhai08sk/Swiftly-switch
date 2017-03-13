package org.de_studio.recentappswitcher.service;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;

public class ChooseActionDialogActivity extends AppCompatActivity {

    private static final String TAG = ChooseActionDialogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean canCall = false;
        boolean canSms = false;

        final String number = getIntent().getStringExtra("number");
        String url = "tel:"+ number;
        final Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                + number));
        smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ResolveInfo calRes = getPackageManager().resolveActivity(callIntent, 0);
        ResolveInfo smsRes = getPackageManager().resolveActivity(smsIntent, 0);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.choose_action_dialog);

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        final CheckBox checkBox = (CheckBox) dialog.findViewById(R.id.use_as_default_check_box);
        LinearLayout setDefaultLayout = (LinearLayout) dialog.findViewById(R.id.use_as_default_layout);
        ImageView callImage = (ImageView) dialog.findViewById(R.id.action_call);
        ImageView smsImage = (ImageView) dialog.findViewById(R.id.action_sms);

        if (setDefaultLayout != null && checkBox !=null) {
            setDefaultLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });
        }


        if (calRes.activityInfo != null) {
            canCall = true;
            try {
                if (callImage != null) {
                    callImage.setImageDrawable(getPackageManager().getApplicationIcon(calRes.activityInfo.packageName));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }else canCall = false;


        if (smsRes != null) {
            canSms = true;
            try {
                if (smsImage != null) {
                    smsImage.setImageDrawable(getPackageManager().getApplicationIcon(smsRes.activityInfo.packageName));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }else canCall = false;



        if (callImage != null && canCall) {
            callImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        SharedPreferences sharedPreferences = ChooseActionDialogActivity.this.getSharedPreferences(Cons.SHARED_PREFERENCE_NAME, 0);
                        sharedPreferences.edit().putInt(Cons.CONTACT_ACTION_KEY, Cons.ACTION_CALL).apply();
                    }
                    if (ContextCompat.checkSelfPermission(ChooseActionDialogActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                        ChooseActionDialogActivity.this.startActivity(callIntent);
                    }
                    finish();
                }
            });
        }
        if (smsImage != null && canSms) {
            smsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        SharedPreferences sharedPreferences = ChooseActionDialogActivity.this.getSharedPreferences(Cons.SHARED_PREFERENCE_NAME, 0);
                        sharedPreferences.edit().putInt(Cons.CONTACT_ACTION_KEY, Cons.ACTION_SMS).apply();
                    }
                        try {
                            startActivity(smsIntent);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(ChooseActionDialogActivity.this,
                                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    finish();
                    }

            });
        }


    }
}
