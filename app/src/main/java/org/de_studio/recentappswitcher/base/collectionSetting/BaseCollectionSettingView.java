package org.de_studio.recentappswitcher.base.collectionSetting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
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

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.setItems.SetItemsView;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public abstract class BaseCollectionSettingView extends BaseActivity {
    private static final String TAG = BaseCollectionSettingView.class.getSimpleName();
    @BindView(R.id.spinner)
    protected AppCompatSpinner spinner;
    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    @Inject
    protected BaseCollectionSettingPresenter presenter;
    @Inject
    protected SlotsAdapter adapter;

    protected GridSpacingItemDecoration decoration;

    protected ArrayAdapter<CharSequence> spinnerAdapter;
    protected Subscription subscription;
    protected String collectionId;
    protected GridLayoutManager manager;



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

    public void setRecyclerView(OrderedRealmCollection<Slot> slots, RecyclerView.LayoutManager layoutManager, GridSpacingItemDecoration decoration) {
        Log.e(TAG, "setRecyclerView: slots size = " + slots.size());
        adapter.updateData(slots);
        if (decoration != null) {
            this.decoration = decoration;
            recyclerView.addItemDecoration(decoration);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        setOnItemClick();
    }

    public RecyclerView.LayoutManager getLayoutManager(int layoutType, int column) {
        switch (layoutType) {
            case Cons.LAYOUT_TYPE_LINEAR:
                return new LinearLayoutManager(this);
            case Cons.LAYOUT_TYPE_GRID:
                manager = new GridLayoutManager(this, column);
                return manager;
        }
        return null;
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

    public void updateRecyclerView(OrderedRealmCollection<Slot> slots) {
        adapter.updateData(slots);
    }

    public void addCollectionToSpinner(String collectionLabel) {
        int currentCount = spinnerAdapter.getCount();
        spinnerAdapter.insert(collectionLabel, currentCount -1);
        spinner.setSelection(spinnerAdapter.getCount() - 2);
    }

    public void openSetItems(int slotIndex, String collectionId) {
        startActivity(SetItemsView.getIntent(this,SetItemsView.ITEMS_TYPE_STAGE_1,slotIndex,collectionId,null));
    }

    public void showChooseSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.set_size)
                .setItems(new CharSequence[]{"5", "6", "7", "8"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onChooseCollectionSize(which + 5);
                    }
                });
        builder.create().show();
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @CallSuper
    @Override
    protected void clear() {
        subscription.unsubscribe();
    }
    @Optional
    @OnClick(R.id.size)
    void onSizeClick(){
        presenter.onSizeClick();
    }

    @Override
    public void getDataFromRetainFragment() {

    }

    @Override
    public void onDestroyBySystem() {

    }
}
