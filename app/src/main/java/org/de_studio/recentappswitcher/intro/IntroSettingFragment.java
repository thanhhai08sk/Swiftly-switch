package org.de_studio.recentappswitcher.intro;

import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

/**
 * Created by HaiNguyen on 5/13/16.
 */
public class IntroSettingFragment extends Fragment {
    private static final String LOG_TAG = IntroSettingFragment.class.getSimpleName();
    private LinearLayout permission1Layout, permission2Layout, permission3Layout;
    private boolean isPauseForPermission = false;


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
        setPermission1Layout();
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
                    } catch (Exception e) {
                        new MaterialDialog.Builder(getActivity())
                                .content(R.string.main_usage_access_can_not_found)
                                .positiveText(R.string.app_tab_fragment_ok_button)
                                .show();
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
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.enable_accessibility_permission)
                            .content(R.string.accessibility_service_description)
                            .positiveText(R.string.enable)
                            .negativeText(R.string.md_cancel_label)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    openAccessibilitySettings();
                                }
                            })
                            .cancelable(false)
                            .show();
                }
            });
        }


    }

    private void openAccessibilitySettings() {
        new MaterialDialog.Builder(getActivity())
                .content(R.string.enable_accessibility_permission_guide)
                .positiveText(R.string.cast_tracks_chooser_dialog_ok)
                .negativeText(R.string.md_cancel_label)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (Utility.isOreo() && Utility.isEdgesOn(getActivity())) {
                            isPauseForPermission = true;
                            Utility.toggleEdges(getActivity());
                            Utility.toast(getActivity(), R.string.pause_while_giving_permission);
                        }
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    }
                })
                .show();
    }

    private void setPermission1Layout() {
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
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getActivity(), "Cannot find the setting on your device, please grant the permission manually", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            permission1Layout.setBackgroundResource(R.drawable.set_permission_ok_background);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setPermission1Layout();
        setPermission2Layout();
        setPermission3Layout();
        if (Utility.isOreo() && isPauseForPermission && !Utility.isEdgesOn(getActivity())) {
            isPauseForPermission = false;
            Utility.toggleEdges(getActivity());
        }
    }

    public boolean checkPermissionBeforeFinish() {
         final boolean isOk = isStep1Ok() && Settings.canDrawOverlays(getContext()) && Utility.isAccessibilityEnable(getContext());
        final boolean isSkip;
        if (!isOk) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setMessage(R.string.you_have_not_finished_all_permission_yet)
                    .setPositiveButton(R.string.app_tab_fragment_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
        }
        return isOk;
    }
}
