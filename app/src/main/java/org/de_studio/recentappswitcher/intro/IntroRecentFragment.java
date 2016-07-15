package org.de_studio.recentappswitcher.intro;


import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.R;

public class IntroRecentFragment extends Fragment {
    private static final String LOG_TAG = IntroRecentFragment.class.getSimpleName();
    private static final int ANIMATION_DURATION = 2000;
    private ImageView introImage,hand;
    private float imageX, imageY, imageHeight, imageWidth;
    private ViewPropertyAnimator[] propertyAnimators = new ViewPropertyAnimator[4];

    public static IntroRecentFragment newInstance(int index) {
        IntroRecentFragment f = new IntroRecentFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro_recent, container,false);
        introImage = (ImageView) rootView.findViewById(R.id.intro_image);
        introImage.setImageResource(R.drawable.screenshot_1);
        hand = (ImageView) rootView.findViewById(R.id.intro_hand);
        return rootView;
    }

    public void startAnimation() {
        for (ViewPropertyAnimator p : propertyAnimators) {
            if (p != null) {
                p.cancel();
            }
        }
        if (hand != null) {
            hand.setAlpha(1f);
        }
        imageX = introImage.getX();
        imageY = introImage.getY();
        imageHeight = introImage.getHeight();
        imageWidth = introImage.getWidth();
        Log.e(LOG_TAG, "imageX = " + imageX + "\nimageY = " + imageY + "\nimageHeight = " + imageHeight + "\nimageWidth = " + imageWidth);
        hand.setX(imageX+ imageWidth + imageWidth/3);
        hand.setY(imageY + imageHeight/2 - hand.getHeight()/2);
        introImage.setImageResource(R.drawable.screenshot_1);
        propertyAnimators[0] = hand.animate().setDuration(ANIMATION_DURATION).x(imageX+ imageWidth/2 + imageWidth/3)
                .setListener(new  Animation1());
    }

    private class Animation1 implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            introImage.setImageResource(R.drawable.screenshot_2);
            propertyAnimators[1] = hand.animate().x(hand.getX() - imageWidth / 8)
                    .y(hand.getY() + imageHeight / 15 - imageHeight/6)
                    .setListener(new Animation3());
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private class Animation2 implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            introImage.setImageResource(R.drawable.screenshot_3);
            propertyAnimators[2] = hand.animate().setDuration(ANIMATION_DURATION).x(hand.getX() - imageWidth / 8)
                    .y(hand.getY() + imageHeight / 15)
                    .setListener(new Animation3());
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private class Animation3 implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            introImage.setImageResource(R.drawable.screenshot_4);
            propertyAnimators[3] = hand.animate().alpha(0f).setDuration(ANIMATION_DURATION).setListener(new Animation4());


        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private class Animation4 implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            introImage.setImageResource(R.drawable.screenshot_1a);
            if (IntroRecentFragment.this.isVisible()) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startAnimation();
                    }
                },ANIMATION_DURATION);
            }




        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }



}
