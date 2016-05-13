package org.de_studio.recentappswitcher.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.de_studio.recentappswitcher.R;

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
        View rootView = inflater.inflate(R.layout.fragment_intro_navi, container,false);
        permission1Layout = (LinearLayout) rootView.findViewById(R.id.ask_permission_1_linear_layout);
        permission2Layout = (LinearLayout) rootView.findViewById(R.id.ask_permission_2_linear_layout);
        permission3Layout = (LinearLayout) rootView.findViewById(R.id.ask_permission_3_linear_layout);




        return rootView;
    }
}
