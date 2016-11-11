package org.de_studio.recentappswitcher.setCircleFavorite;

import android.app.Activity;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.ListView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class SetCircleFavoriteView extends Activity {
    private static final String TAG = SetCircleFavoriteView.class.getSimpleName();
    @BindView(R.id.spinner)
    AppCompatSpinner spinner;
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.size_text)
    TextView sizeText;
    @BindView(R.id.long_click_mode_text)
    TextView longClickModeText;

    @Inject
    SetCircleFavoritePresenter presenter;
    @Inject
    SetCircleFavoriteModel model;










    @OnClick(R.id.size)
    void onSizeClick(){
        presenter.onSizeClick();
    }

    @OnClick(R.id.long_click_mode)
    void onLongClickModeClick(){
        presenter.onLongClickModeClick();
    }



}
