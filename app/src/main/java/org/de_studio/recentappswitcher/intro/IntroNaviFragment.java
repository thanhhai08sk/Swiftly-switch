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

/**
 * Created by HaiNguyen on 5/11/16.
 */
public class IntroNaviFragment extends Fragment {
    private static final String LOG_TAG = IntroNaviFragment.class.getSimpleName();
    private static final int ANIMATION_DURATION = 2000;
    private ImageView introImage,hand;
    private float imageX, imageY, imageHeight, imageWidth;
    private ViewPropertyAnimator[] propertyAnimators = new ViewPropertyAnimator[5];


    public static IntroNaviFragment newInstance(int index) {
        IntroNaviFragment f = new IntroNaviFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(LOG_TAG, "onHiddenChanged = " + hidden);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_intro_navi, container,false);
        introImage = (ImageView) rootView.findViewById(R.id.intro_image);
        introImage.setImageResource(R.drawable.screenshot_1);
        hand = (ImageView) rootView.findViewById(R.id.intro_hand);
//        final ViewTreeObserver observer = introImage.getViewTreeObserver();
//        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                introImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                imageX = introImage.getX();
//                imageY = introImage.getY();
//                imageHeight = introImage.getHeight();
//                imageWidth = introImage.getWidth();
//                Log.e(LOG_TAG, "imageX = " + imageX + "\nimageY = " + imageY + "\nimageHeight = " + imageHeight + "\nimageWidth = " + imageWidth);
//                hand.setX(imageX+ imageWidth + imageWidth/3);
//                hand.setY(imageY + imageHeight/2);
//                hand.animate().setDuration(ANIMATION_DURATION).x(imageX+ imageWidth/2 + imageWidth/3)
//                        .y(imageY + imageHeight/2)
//                        .setListener(new  Animation1());
////                        .withStartAction(new Runnable() {
////                    @Override
////                    public void run() {
////                        hand.setX(imageX+ imageWidth);
////                        hand.setY(imageY + imageHeight/2);
////                    }
////                });
//            }
//        });


        return rootView;
    }

    public void startAnimation() {
        for (ViewPropertyAnimator p : propertyAnimators) {
            if (p != null) {
                p.cancel();
            }
        }
        imageX = introImage.getX();
        imageY = introImage.getY();
        imageHeight = introImage.getHeight();
        imageWidth = introImage.getWidth();
        Log.e(LOG_TAG, "imageX = " + imageX + "\nimageY = " + imageY + "\nimageHeight = " + imageHeight + "\nimageWidth = " + imageWidth);
        introImage.setImageResource(R.drawable.screenshot_1);
        hand.setX(imageX+ imageWidth + imageWidth/3);
        hand.setY(imageY + imageHeight/2  - hand.getHeight()/2);
        propertyAnimators[0] = hand.animate().setDuration(ANIMATION_DURATION).x(imageX+ imageWidth/2 + imageWidth/3)
                .setListener(new  Animation2());
    }

    public class Animation1 implements Animator.AnimatorListener {
        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            introImage.setImageResource(R.drawable.screenshot_2);
            propertyAnimators[1] = hand.animate().setDuration(ANIMATION_DURATION).y(hand.getY() - imageHeight / 4).setListener(new Animation2());
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }
    }

    public class Animation2 implements Animator.AnimatorListener {
        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            introImage.setImageResource(R.drawable.screenshot_2);
            propertyAnimators[2] = hand.animate().setDuration(ANIMATION_DURATION).x(hand.getX() - imageWidth / 3)
                    .y(hand.getY() - imageHeight / 8).setListener(new Animation3());

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }
    }

    private class Animation3 implements Animator.AnimatorListener {
        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            introImage.setImageResource(R.drawable.screenshot_8);
            propertyAnimators[3] = hand.animate().setDuration(ANIMATION_DURATION).x(hand.getX() - imageWidth/5)
                    .y(hand.getY()+ imageHeight/5).setListener(new Animation4());
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }
    }

    private class Animation4 implements Animator.AnimatorListener {
        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            introImage.setImageResource(R.drawable.screenshot_9);
            propertyAnimators[4] = hand.animate().setDuration(ANIMATION_DURATION).x(hand.getX() + imageWidth/6)
                    .y(hand.getY() + imageHeight/8).setListener(new Animation5());
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }
    }

    private class Animation5 implements Animator.AnimatorListener {
        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            introImage.setImageResource(R.drawable.screenshot_10);
            if (IntroNaviFragment.this.isVisible()) {
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
    }





}
