package org.de_studio.recentappswitcher.setCircleFavorite;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
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
import org.de_studio.recentappswitcher.setItems.SetItemsView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public class SetCircleFavoriteView extends BaseActivity {
    private static final String TAG = SetCircleFavoriteView.class.getSimpleName();
    @BindView(R.id.spinner)
    AppCompatSpinner spinner;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
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
    ArrayAdapter<CharSequence> spinnerAdapter;
    String collectionId;
    Subscription subscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        collectionId = getIntent().getStringExtra(Cons.COLLECTION_ID);
        super.onCreate(savedInstanceState);
    }

    public void setSpinner(RealmResults<Collection> collections, Collection currentCollection) {

        final String addNew = getString(R.string.add_new);
        List<CharSequence> itemsList = new ArrayList<>();
        for (Collection collection : collections) {
            itemsList.add(collection.label);
        }
        itemsList.add(addNew);
        spinnerAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_dropdown_item, itemsList);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(itemsList.indexOf(currentCollection.label));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    String itemLabel = ((CheckedTextView) view.findViewById(android.R.id.text1)).getText().toString();
                    Log.e(TAG, "onItemSelected: label = " + itemLabel);
                    if (itemLabel.equals(addNew)) {
                        presenter.onAddNewCollection();
                    } else {
                        presenter.onSpinnerItemSelect(itemLabel);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void addCollectionToSpinner(String collectionLabel) {
        int currentCount = spinnerAdapter.getCount();
        spinnerAdapter.insert(collectionLabel, currentCount -1);
        spinner.setSelection(spinnerAdapter.getCount() - 2);
    }

    public void setRecyclerView(OrderedRealmCollection<Slot> slots, RecyclerView.LayoutManager layoutManager) {
        Log.e(TAG, "setRecyclerView: slots size = " + slots.size());
        adapter.updateData(slots);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        setOnItemClick();
    }

    public RecyclerView.LayoutManager getLayoutManager(int layoutType, int column) {
        switch (layoutType) {
            case Cons.LAYOUT_TYPE_LINEAR:
                return new LinearLayoutManager(this);
            case Cons.LAYOUT_TYPE_GRID:
                return new GridLayoutManager(this, column);
        }
        return null;
    }

    public void updateRecyclerView(OrderedRealmCollection<Slot> slots) {
        adapter.updateData(slots);
    }

    public void showChooseSizeDialog() {
        AlertDialog.Builder buider = new AlertDialog.Builder(this);
        buider.setTitle(R.string.set_size)
                .setItems(new CharSequence[]{"5", "6", "7", "8"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onChooseCollectionSize(which + 5);
                    }
                });
        buider.create().show();
    }

    public void openSetItems(int slotIndex, String collectionId) {
        startActivity(SetItemsView.getIntent(this,SetItemsView.ITEMS_TYPE_STAGE_1,slotIndex,collectionId,null));
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

    public void setOnItemClick() {
        subscription = adapter.getKeyClicked().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                Log.e(TAG, "call: " + integer);
                presenter.onSlotClick(integer);
            }
        });
    }

    @Override
    protected void clear() {
        subscription.unsubscribe();
    }

    public static Intent getIntent(Context context, String collectionId) {
        Intent intent = new Intent(context, SetCircleFavoriteView.class);
        intent.putExtra(Cons.COLLECTION_ID, collectionId);
        return intent;
    }

    @Override
    public void getDataFromRetainFragment() {

    }

    @Override
    public void onDestroyBySystem() {

    }
}
