package org.de_studio.recentappswitcher.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroViewPager;

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
        finish();

    }

    @Override
    public void onSlideChanged() {

    }
}
