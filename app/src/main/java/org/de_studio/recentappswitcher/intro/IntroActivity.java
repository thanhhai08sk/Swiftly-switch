package org.de_studio.recentappswitcher.intro;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.de_studio.recentappswitcher.R;

/**
 * Created by hai on 5/7/2016.
 */
public class IntroActivity extends AppIntro2 {
    @Override
    public void init(@Nullable Bundle savedInstanceState) {
//        addSlide(new IntroRecentFragment());
        addSlide(AppIntroFragment.newInstance("fragment", "test", R.drawable.screenshot_1, Color.GREEN));
        addSlide(IntroRecentFragment.newInstance(1));
        showStatusBar(true);


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
