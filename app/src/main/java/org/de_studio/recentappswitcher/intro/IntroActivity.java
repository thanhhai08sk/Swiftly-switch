package org.de_studio.recentappswitcher.intro;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.AppIntroViewPager;

import org.de_studio.recentappswitcher.R;

/**
 * Created by hai on 5/7/2016.
 */
public class IntroActivity extends AppIntro2 {
    private static final String LOG_TAG = IntroActivity.class.getSimpleName();
    private IntroRecentFragment recentSlide;
    private IntroNaviFragment naviSlide;
    private IntroFavoFragment favoSlide;


    @Override
    public void init(@Nullable Bundle savedInstanceState) {
//        addSlide(new IntroRecentFragment());
        recentSlide = IntroRecentFragment.newInstance(1);
        naviSlide = IntroNaviFragment.newInstance(2);
        favoSlide = IntroFavoFragment.newInstance(3);
        addSlide(AppIntroFragment.newInstance("fragment", "test", R.drawable.screenshot_1, Color.GREEN));
        addSlide(recentSlide);
        addSlide(naviSlide);
        addSlide(favoSlide);
        showStatusBar(true);
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

    }

    @Override
    public void onDonePressed() {
        finish();

    }

    @Override
    public void onSlideChanged() {

    }
}
