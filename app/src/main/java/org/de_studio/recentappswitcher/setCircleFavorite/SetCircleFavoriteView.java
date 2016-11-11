package org.de_studio.recentappswitcher.setCircleFavorite;

import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.base.BasePresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class SetCircleFavoriteView extends BaseActivity {
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


    public void setSpinner(String[] itemIncludeCreateNew) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, itemIncludeCreateNew);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemId = ((CheckedTextView) view.findViewById(android.R.id.text1)).getText().toString();
                presenter.onSpinnerItemSelect(itemId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.set_circle_favorite_view;
    }

    @Override
    protected void inject() {

    }

    @OnClick(R.id.size)
    void onSizeClick(){
        presenter.onSizeClick();
    }

    @OnClick(R.id.long_click_mode)
    void onLongClickModeClick(){
        presenter.onLongClickModeClick();
    }



}
