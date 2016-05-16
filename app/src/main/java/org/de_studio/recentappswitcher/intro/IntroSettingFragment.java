package org.de_studio.recentappswitcher.intro;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

/**
 * Created by HaiNguyen on 5/13/16.
 */
public class IntroSettingFragment extends Fragment {
    private static final String LOG_TAG = IntroSettingFragment.class.getSimpleName();
    private LinearLayout permission1Layout, permission2Layout, permission3Layout;


    public static IntroSettingFragment newInstance(int index) {
        IntroSettingFragment f = new IntroSettingFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro_setting, container,false);
        permission1Layout = (LinearLayout) rootView.findViewById(R.id.ask_permission_1_linear_layout);
        permission2Layout = (LinearLayout) rootView.findViewById(R.id.ask_permission_2_linear_layout);
        permission3Layout = (LinearLayout) rootView.findViewById(R.id.ask_permission_3_linear_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(getContext())) {
                permission1Layout.setBackgroundResource(R.drawable.set_permission_ok_background);
                permission1Layout.setOnClickListener(null);
            } else {
                permission1Layout.setBackgroundResource(R.drawable.set_permission_ask_background);
            }
            permission1Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Settings.canDrawOverlays(getContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getContext().getPackageName()));
                        startActivity(intent);
                    }
                }
            });
        } else {
            permission1Layout.setBackgroundResource(R.drawable.set_permission_ok_background);
        }
        setPermission2Layout();
        setPermission3Layout();




        return rootView;
    }
    private boolean isStep1Ok() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOps = (AppOpsManager) getContext().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), getContext().getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } else return true;


    }

    private void setPermission2Layout() {
        if (isStep1Ok()) {
            permission2Layout.setBackgroundResource(R.drawable.set_permission_ok_background);
            permission2Layout.setOnClickListener(null);
        } else {
            permission2Layout.setBackgroundResource(R.drawable.set_permission_ask_background);
            permission2Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    } catch (ActivityNotFoundException e) {
                        Log.e(LOG_TAG, "Can not found usage access setting");
                        Toast.makeText(getContext(),R.string.main_usage_access_can_not_found,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void setPermission3Layout() {
        if (Utility.isAccessibilityEnable(getContext())) {
            permission3Layout.setBackgroundResource(R.drawable.set_permission_ok_background);
            permission3Layout.setOnClickListener(null);
        } else {
            permission3Layout.setBackgroundResource(R.drawable.set_permission_ask_background);
            permission3Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                }
            });
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        setPermission2Layout();
        setPermission3Layout();


    }
}