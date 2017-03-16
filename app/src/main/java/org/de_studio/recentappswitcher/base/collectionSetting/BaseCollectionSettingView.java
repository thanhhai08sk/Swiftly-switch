package org.de_studio.recentappswitcher.base.collectionSetting;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

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

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;
import io.realm.OrderedRealmCollection;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/26/16.
 */

public abstract class BaseCollectionSettingView<T, P extends BaseCollectionSettingPresenter> extends BaseActivity<T , P> implements BaseCollectionSettingPresenter.View {
    private static final String TAG = BaseCollectionSettingView.class.getSimpleName();
    @BindView(R.id.current_set_text)
    TextView currentSetText;
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
    PublishSubject<String> chooseCurrentSetSJ = PublishSubject.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        collectionId = getIntent().getStringExtra(Cons.COLLECTION_ID);
        super.onCreate(savedInstanceState);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DragAndDropCallback(moveItemSubject
                , dropItemSubject
                , dragItemSubject));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collection_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            presenter.onDeleteCollection();
            return true;
        } else if (id == R.id.action_set_label) {
            presenter.onSetCollectionLabel();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    public void highlightItem(int position) {
        adapter.setHighlightItem(position);
    }

    @Override
    public void notifyItemMove(int from, int to) {
        adapter.notifyItemMoved(from,to);
    }

    @Override
    public void notifyItemRemove(int position) {
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
        adapter.setHighlightItem(-1);
    }
    @CallSuper
    @Override
    public void updateCollectionInfo(Collection collection) {
        currentSetText.setText(collection.label);
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
        int[] deleteCoord = new int[2];
        deleteButton.getLocationOnScreen(deleteCoord);
        return (x > deleteCoord[0] - Cons.ICON_SIZE_DEFAULT * getResources().getDisplayMetrics().density && x < deleteCoord[0] +Cons.ICON_SIZE_DEFAULT * getResources().getDisplayMetrics().density  )  &&  y > deleteCoord[1] - deleteButton.getHeight()*1.5;
    }

    @Override
    public void setCircleSizeDialog(PublishSubject<Integer> subject, int currentValue) {
        Utility.showDialogWithSeekBar(
                Cons.CIRCLE_SIZE_MIN,
                Cons.CIRCLE_SIZE_MAX,
                currentValue,
                "dp", getString(R.string.edge_dialog_set_circle_size_text),
                subject, this);
    }


    @Override
    public void chooseCurrentCollection(final List<Collection> collections, Collection currentCollection) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        CharSequence[] items = new CharSequence[collections.size() + 1];
//        for (int i = 0; i < collections.size(); i++) {
//            items[i] = collections.get(i).label;
//        }
//        items[items.length - 1] = getString(R.string.add_new);
//        builder.setTitle(R.string.choose_set)
//                .setSingleChoiceItems(items, collections.indexOf(currentCollection), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which < collections.size()) {
//                            chooseCurrentSetSJ.onNext(collections.get(which).collectionId);
//                        } else {
//                            presenter.onAddNewCollection();
//                        }
//                    }
//                })
//                .setPositiveButton(R.string.app_tab_fragment_ok_button, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        builder.create().show();


        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                if (index < collections.size()) {
                    chooseCurrentSetSJ.onNext(collections.get(index).collectionId);
                } else {
                    presenter.onAddNewCollection();
                }

                dialog.dismiss();
            }
        });

        int icon = 0;
        switch (currentCollection.type) {
            case Collection.TYPE_CIRCLE_FAVORITE:
                icon = R.drawable.ic_circle_favorite_set;
                break;
            case Collection.TYPE_QUICK_ACTION:
                icon = R.drawable.ic_quick_actions_set;
                break;
            case Collection.TYPE_RECENT:
                icon = R.drawable.ic_recent_set;
                break;
            case Collection.TYPE_GRID_FAVORITE:
                icon = R.drawable.ic_grid_favorite_set;
                break;
        }

        for (Collection collection : collections) {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .content(collection.label)
                    .icon(icon)
                    .iconPaddingDp(4)
                    .backgroundColor(Color.WHITE)
                    .build());
        }

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.add_new)
                .icon(R.drawable.ic_add)
                .backgroundColor(Color.WHITE)
                .iconPaddingDp(4)
                .build());


        new MaterialDialog.Builder(this)
                .adapter(adapter, null)
                .show();
    }

    @Override
    public void notifyCannotDelete(int reason, String id) {
        String text = "";
        switch (reason) {
            case BaseCollectionSettingPresenter.CANNOT_DELETE_REASON_BEING_USED:
                text = getString(R.string.can_not_delete_cause_being_used_in) + id;
                break;
            case BaseCollectionSettingPresenter.CANNOT_DELETE_REASON_THIS_IS_ONLY_ONE:
                text = getString(R.string.can_not_delete_cause_this_is_the_only_one);
                break;
        }
        new MaterialDialog.Builder(this)
                .title(R.string.can_not_delete)
                .content(text)
                .positiveText(R.string.app_tab_fragment_ok_button)
                .show();
    }

    @Override
    public PublishSubject<String> onChooseCurrentSet() {
        return chooseCurrentSetSJ;
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

    public void showChooseBetweenSetFolderAndSetItems(final int slotIndex, boolean folderAvailable) {

        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                switch ((int) item.getId()) {
                    case 0:
                        presenter.setItems(slotIndex);
                        break;
                    case 1:
                        presenter.setFolder(slotIndex);
                        break;
                    case 2:
                        presenter.editItem(slotIndex);
                        break;
                }
                dialog.dismiss();
            }
        });

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.shortcut)
                .iconPaddingDp(4)
                .icon(R.drawable.ic_shortcuts)
                .backgroundColor(Color.WHITE)
                .id(0)
                .build());
        if (folderAvailable) {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .content(R.string.setting_shortcut_folder)
                    .iconPaddingDp(4)
                    .icon(R.drawable.ic_folder)
                    .backgroundColor(Color.WHITE)
                    .id(1)
                    .build());
        }

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.edit)
                .iconPadding(4)
                .icon(R.drawable.ic_edit_white_24dp)
                .backgroundColor(Color.WHITE)
                .id(2)
                .build()
        );


        new MaterialDialog.Builder(this)
                .adapter(adapter, null)
                .show();
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
    public void showSetLabelDialog(String currentLabel) {
        new MaterialDialog.Builder(this)
                .title(R.string.set_label)
                .input(null, currentLabel, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        presenter.setCollectionLabel(input.toString());
                    }
                }).show();
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

    @OnClick(R.id.current_set)
    void currentSetClick(){
        presenter.onCurrentSetClick();
    }

    @Override
    public void getDataFromRetainFragment() {

    }

    @Override
    public void onDestroyBySystem() {

    }

    @Override
    public void restartService() {
        Utility.restartService(this);
    }
}
