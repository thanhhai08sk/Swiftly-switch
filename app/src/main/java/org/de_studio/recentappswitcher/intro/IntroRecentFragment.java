package org.de_studio.recentappswitcher.intro;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.de_studio.recentappswitcher.R;

public class IntroRecentFragment extends Fragment {

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
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_intro_recent, container,false);
        ImageView introImage = (ImageView) rootView.findViewById(R.id.intro_image);
        introImage.setImageResource(R.drawable.screenshot_1);
        ImageView hand = (ImageView) rootView.findViewById(R.id.intro_hand);
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int height = dm.heightPixels;
        int width = dm.widthPixels;
        hand.animate().setDuration(2000).x(height/2).y(width/2);

        return rootView;
    }
}
