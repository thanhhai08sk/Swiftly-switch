package org.de_studio.recentappswitcher.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.R;

/**
 * Created by HaiNguyen on 5/12/16.
 */
public class IntroFirstPageFragment extends Fragment {
    private static final String LOG_TAG = IntroFirstPageFragment.class.getSimpleName();
    private static final int ANIMATION_DURATION = 2000;

    public static IntroFirstPageFragment newInstance(int index) {
        IntroFirstPageFragment f = new IntroFirstPageFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro_first_page, container,false);
        ImageView icon = (ImageView) rootView.findViewById(R.id.icon);
        TextView appName = (TextView) rootView.findViewById(R.id.app_name);
        TextView description = (TextView) rootView.findViewById(R.id.description);
//        icon.setScaleX(0f);
//        icon.setScaleY(0f);
        icon.setAlpha(0f);
        description.setAlpha(0f);
        icon.animate().setDuration(ANIMATION_DURATION).alpha(1f);
        description.animate().setDuration(ANIMATION_DURATION).alpha(1f);


        return rootView;
    }
}
