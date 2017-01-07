package org.de_studio.recentappswitcher.base.collectionSetting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageButton;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.base.DragAndDropCallback;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.folderSetting.FolderSettingView;
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
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public abstract class BaseCollectionSettingView<T, P extends BaseCollectionSettingPresenter> extends BaseActivity<T , P> implements BaseCollectionSettingPresenter.View {
    private static final String TAG = BaseCollectionSettingView.class.getSimpleName();
    @Nullable
    @BindView(R.id.spinner)
    protected AppCompatSpinner spinner;
    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;
    @BindView(R.id.delete_image_button)
    protected ImageButton deleteButton;


    @Inject
    protected SlotsAdapter adapter;

    protected GridSpacingItemDecoration decoration;

    protected ArrayAdapter<CharSequence> spinnerAdapter;
    protected Subscription subscription;
    protected String collectionId;
    protected GridLayoutManager manager;

    PublishSubject<DragAndDropCallback.MoveData> moveItemSubject = PublishSubject.create();
    PublishSubject<DragAndDropCallback.DropData> dropItemSubject = PublishSubject.create();
    PublishSubject<DragAndDropCallback.Coord> dragItemSubject = PublishSubject.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        collectionId = getIntent().getStringExtra(Cons.COLLECTION_ID);
        super.onCreate(savedInstanceState);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DragAndDropCallback(moveItemSubject
                , dropItemSubject
                , dragItemSubject));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public PublishSubject<DragAndDropCallback.MoveData> onMoveItem() {
        return moveItemSubject;
    }

    @Override
    public PublishSubject<DragAndDropCallback.DropData> onDropItem() {
        return dropItemSubject;
    }

    @Override
    public PublishSubject<DragAndDropCallback.Coord> onDragItem() {
        return dragItemSubject;
    }

    @Override
    public void notifyItemMove(int from, int to) {
        adapter.notifyItemMoved(from,to);
    }

    @Override
    public void notifyItemRemove(int position) {
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setDeleteButtonVisibility(boolean visible) {
        deleteButton.setVisibility(visible? View.VISIBLE: View.INVISIBLE);
    }

    @Override
    public void setDeleteButtonColor(boolean red) {
        deleteButton.setBackgroundResource(red ? R.drawable.delete_button_red : R.drawable.delete_button_normal);
    }

    @Override
    public boolean isHoverOnDeleteButton(float x, float y) {
        return (x > deleteButton.getX() - deleteButton.getWidth()*2 && x < deleteButton.getX()) && y > deleteButton.getY() - deleteButton.getHeight()*2;
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

    public void showChooseBetweenSetFolderAndSetItems(final int slotIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setItems(new CharSequence[]{getString(R.string.app_shortcut_contact), getString(R.string.setting_shortcut_folder)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                presenter.setItems(slotIndex);
                                break;
                            case 1:
                                presenter.setFolder(slotIndex);
                                break;
                        }
                    }
                });
        builder.create().show();
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
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
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

    public void openSetFolder(String folderId) {
        startActivity(FolderSettingView.getIntent(this, folderId));
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
    public int dpToPixel(int dp) {
        return Utility.dpToPixel(this, dp);
    }

    @CallSuper
    @Override
    public void clear() {
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
