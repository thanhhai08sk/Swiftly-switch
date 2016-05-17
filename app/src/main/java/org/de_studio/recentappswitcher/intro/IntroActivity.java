package org.de_studio.recentappswitcher.intro;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroViewPager;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;

/**
 * Created by hai on 5/7/2016.
 */
public class IntroActivity extends AppIntro2 {
    private static final String LOG_TAG = IntroActivity.class.getSimpleName();
    private IntroRecentFragment recentSlide;
    private IntroNaviFragment naviSlide;
    private IntroFavoFragment favoSlide;
    private IntroFirstPageFragment firstSlide;
    private IntroSettingFragment settingSlide;
    private int startPage = 0;


    @Override
    public void init(@Nullable Bundle savedInstanceState) {
//        addSlide(new IntroRecentFragment());
        Intent intent = getIntent();
        if (intent != null) {
            startPage = intent.getIntExtra("page",0);
        }
        firstSlide = IntroFirstPageFragment.newInstance(0);
        recentSlide = IntroRecentFragment.newInstance(1);
        naviSlide = IntroNaviFragment.newInstance(2);
        favoSlide = IntroFavoFragment.newInstance(3);
        settingSlide = IntroSettingFragment.newInstance(4);
        addSlide(firstSlide);
        addSlide(recentSlide);
        addSlide(naviSlide);
        addSlide(favoSlide);
        addSlide(settingSlide);
        showStatusBar(false);
        pager.setCurrentItem(startPage);
        AppIntroViewPager pager = this.pager;
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e(LOG_TAG, "onPageSelected " + position);
                switch (position) {
                    case 1:
                        recentSlide.startAnimation();
                        break;
                    case 2:
                        naviSlide.startAnimation();
                        break;
                    case 3:
                        favoSlide.startAnimation();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    @Override
    public void onNextPressed() {
        if (pager.getCurrentItem() == 4 && startPage == 4) {
            finish();
        }
    }

    @Override
    public void onDonePressed() {
        boolean isOk = isStep1Ok() && Settings.canDrawOverlays(this) && Utility.isAccessibilityEnable(this);
        Log.e(LOG_TAG, "finish Intro");
        if (isOk) {
            finish();
        } else {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(IntroActivity.this);
            builder.setMessage(R.string.you_have_not_finished_all_permission_yet)
                    .setPositiveButton(R.string.app_tab_fragment_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton(R.string.skip, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.show();
        }

    }

    @Override
    public void onSlideChanged() {

    }
    private boolean isStep1Ok() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                    android.os.Process.myUid(), this.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } else return true;


    }

}
