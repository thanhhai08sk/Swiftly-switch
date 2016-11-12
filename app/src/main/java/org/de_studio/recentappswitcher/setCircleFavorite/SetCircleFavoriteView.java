package org.de_studio.recentappswitcher.setCircleFavorite;

import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerSetCircleFavoriteComponent;
import org.de_studio.recentappswitcher.dagger.SetCircleFavoriteModule;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

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
    @Inject
    SlotsAdapter adapter;
    String collectionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        collectionId = getIntent().getStringExtra(Cons.COLLECTION_ID);
        super.onCreate(savedInstanceState);
        listView.setAdapter(adapter);
    }


    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
    public void setSpinner(RealmResults<Collection> collections, Collection currentCollection) {

        String addNew = getString(R.string.add_new);
        String[] adapterItems = new String[collections.size() + 1];
        int position = 0;
        for (int i = 0; i < collections.size(); i++) {
            adapterItems[i] = collections.get(i).label;
            if (collections.get(i).equals(currentCollection)) {
                position = i;
            }
        }
        adapterItems[adapterItems.length - 1] = addNew;

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, adapterItems);
        spinner.setAdapter(adapter);
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemLabel = ((CheckedTextView) view.findViewById(android.R.id.text1)).getText().toString();
                presenter.onSpinnerItemSelect(itemLabel);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setListView(OrderedRealmCollection<Slot> slots) {
        adapter.updateData(slots);
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
        DaggerSetCircleFavoriteComponent.builder()
                .appModule(new AppModule(this.getApplicationContext()))
                .setCircleFavoriteModule(new SetCircleFavoriteModule(this, collectionId))
                .build().inject(this);
    }

    @OnClick(R.id.size)
    void onSizeClick(){
        presenter.onSizeClick();
    }

    @OnClick(R.id.long_click_mode)
    void onLongClickModeClick(){
        presenter.onLongClickModeClick();
    }

    @Override
    protected void clear() {

    }
}
