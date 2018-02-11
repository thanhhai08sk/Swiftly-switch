package org.de_studio.recentappswitcher.base.collectionSetting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.base.DragAndDropCallback;
import org.de_studio.recentappswitcher.base.SlotsAdapter;
import org.de_studio.recentappswitcher.folderSetting.FolderSettingView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;
import org.de_studio.recentappswitcher.setItemIcon.SetItemIconView;
import org.de_studio.recentappswitcher.setItems.SetItemsView;
import org.de_studio.recentappswitcher.utils.GridSpacingItemDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
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
    @Nullable
    @Inject
    IconPackManager.IconPack iconPack;

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
        adapter.notifyItemMoved(from, to);
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
    public Context getActivity() {
        return this;
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
        onSlotClick();
    }

    public void chooseActionOnSlot(final int slotIndex,boolean isItem, boolean isFolder, boolean folderAvailable) {

        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                switch ((int) item.getId()) {
                    case 0:
                        presenter.setSlots(slotIndex);
                        break;
                    case 1:
                        presenter.setSlotAsFolder(slotIndex);
                        break;
                    case 2:
                        presenter.editItem(slotIndex);
                        break;
                    case 3:
                        presenter.editFolderContent(slotIndex);
                        break;
                }
                dialog.dismiss();
            }
        });

        if (isFolder) {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .content(R.string.folder_content)
                    .iconPadding(4)
                    .icon(R.drawable.ic_shortcuts_dark)
                    .backgroundColor(Color.WHITE)
                    .id(3)
                    .build()
            );
        } else {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .content(R.string.shortcut)
                    .iconPaddingDp(4)
                    .icon(R.drawable.ic_shortcuts_dark)
                    .backgroundColor(Color.WHITE)
                    .id(0)
                    .build());
        }
        if (folderAvailable && !isFolder) {
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .content(R.string.setting_shortcut_folder)
                    .iconPaddingDp(4)
                    .icon(R.drawable.ic_folder_dark)
                    .backgroundColor(Color.WHITE)
                    .id(1)
                    .build());
        }

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.edit)
                .iconPadding(4)
                .icon(R.drawable.ic_action_edit_dark)
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

    public void onSlotClick() {
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

    public void editFolderLabelAndIcon(final Slot folder) {
        boolean showResetButton = !Utility.isFree(this);
        MaterialDialog.Builder builder = buildBaseSetIconDialog(showResetButton);
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                presenter.setFolderLabel(folder, ((EditText) dialog.getCustomView().findViewById(R.id.label)).getText().toString());
            }
        });

        builder.onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                presenter.resetFolderIcon(folder);
            }
        });

        final MaterialDialog materialDialog = builder.build();
        materialDialog.show();

        View view = materialDialog.getCustomView();
        EditText editText = (EditText) view.findViewById(R.id.label);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        editText.setText(folder.label!=null? folder.label : getString(R.string.setting_shortcut_folder));
        Utility.setSlotIcon(folder, this, icon, getPackageManager(), iconPack, true, false);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setFolderIcon(folder);
                materialDialog.dismiss();
            }
        });

    }

    @Override
    public void editItemLabelAndIcon(final Item item) {
        boolean showResetButton = !Utility.isFree(this);
        MaterialDialog.Builder builder = buildBaseSetIconDialog(showResetButton);
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                presenter.setItemLabel(item, ((EditText) dialog.getCustomView().findViewById(R.id.label)).getText().toString());
            }
        });
        if (showResetButton) {
            builder.onNeutral(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    presenter.resetItemIcon(item);
                }
            });
        }


        MaterialDialog materialDialog = builder.build();
        materialDialog.show();

        View view = materialDialog.getCustomView();
        EditText editText = (EditText) view.findViewById(R.id.label);
        editText.setText(item.label);
        setIcons(item, view, materialDialog);
    }

    private MaterialDialog.Builder buildBaseSetIconDialog(boolean showResetButotn) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title(R.string.edit)
                .customView(R.layout.dialog_edit_item, false)
                .positiveText(R.string.app_tab_fragment_ok_button)
                ;
        if (showResetButotn) {
            builder.neutralText(R.string.reset_icon);
        }
        return builder;
    }

    private void setIcons(final Item item, View view, final MaterialDialog dialog) {
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        LinearLayout icons = (LinearLayout) view.findViewById(R.id.icons);
        ImageView icon1 = (ImageView) view.findViewById(R.id.icon1);
        ImageView icon2 = (ImageView) view.findViewById(R.id.icon2);
        ImageView icon3 = (ImageView) view.findViewById(R.id.icon3);

        int stateCount = 1;
        if (item.type.equals(Item.TYPE_ACTION)) {
            if (item.action == Item.ACTION_WIFI ||
                    item.action == Item.ACTION_BLUETOOTH ||
                    item.action == Item.ACTION_ROTATION ||
                    item.action == Item.ACTION_FLASH_LIGHT) {
                stateCount = 2;
            } else if (item.action == Item.ACTION_RINGER_MODE) {
                stateCount = 3;
            }
        }


        switch (stateCount) {
            case 1:
                icon.setVisibility(View.VISIBLE);
                icons.setVisibility(View.GONE);
                Utility.setItemIcon(item, this, icon, getPackageManager(), iconPack, false);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.setItemIcon(item, 1);
                        dialog.dismiss();
                    }
                });
                break;
            case 2:
                icon.setVisibility(View.GONE);
                icons.setVisibility(View.VISIBLE);
                Utility.setActionIconWithState(item, icon1, this, 1);
                Utility.setActionIconWithState(item, icon2, this, 2);
                icon3.setVisibility(View.GONE);
                icon1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.setItemIcon(item, 1);
                        dialog.dismiss();
                    }
                });
                icon2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.setItemIcon(item, 2);
                        dialog.dismiss();
                    }
                });

                break;
            case 3:
                icon.setVisibility(View.GONE);
                icons.setVisibility(View.VISIBLE);
                icon3.setVisibility(View.VISIBLE);
                Utility.setActionIconWithState(item, icon1, this, 1);
                Utility.setActionIconWithState(item, icon2, this, 2);
                Utility.setActionIconWithState(item, icon3, this, 3);
                icon1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.setItemIcon(item, 1);
                        dialog.dismiss();
                    }
                });
                icon2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.setItemIcon(item, 2);
                        dialog.dismiss();
                    }
                });
                icon3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.setItemIcon(item, 3);
                        dialog.dismiss();
                    }
                });
                break;
        }
    }

    @Override
    public boolean isFree() {
        return Utility.isFree(this);
    }

    @Override
    public void proOnlyDialog() {
        Utility.showProOnlyDialog(this);
    }

    @Override
    public void showChooseIconSourceDialog(final Item item, final Slot folder, final int itemState) {
        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem dialogItem) {
                IconPackManager.IconPack iconPack =(IconPackManager.IconPack) dialogItem.getTag();
                if (item != null) {
                    presenter.setItemIconWithSource(new BaseCollectionSettingPresenter.SetItemIconInfo(iconPack, item.itemId, itemState));
                } else if (folder != null) {
                    presenter.setFolderIconWithSource(iconPack, folder);
                }
                dialog.dismiss();
            }
        });

        IconPackManager manager = new IconPackManager();
        manager.setContext(this);
        HashMap<String, IconPackManager.IconPack> hashMap = manager.getAvailableIconPacks(true);

        adapter.add(new MaterialSimpleListItem.Builder(this)
                .content(R.string.system)
                .iconPadding(4)
                .icon(R.drawable.ic_default)
                .backgroundColor(Color.WHITE)
                .build()
        );
        Set<String> keys = hashMap.keySet();
        IconPackManager.IconPack iconPack;
        Drawable icon = null;
        for (String key : keys) {
            iconPack = hashMap.get(key);
            try {
                icon = getPackageManager().getApplicationIcon(iconPack.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            adapter.add(new MaterialSimpleListItem.Builder(this)
                    .content(iconPack.name)
                    .iconPadding(4)
                    .icon(icon)
                    .backgroundColor(Color.WHITE)
                    .tag(iconPack)
                    .build()
            );
        }

        new MaterialDialog.Builder(this)
                .adapter(adapter, null)
                .title(R.string.choose_icon_source)
                .show();
    }

    @Override
    public void openItemIconSetting(BaseCollectionSettingPresenter.SetItemIconInfo info) {
        startActivity(SetItemIconView.getIntent(info.itemId,null, this, info.iconPack != null ? info.iconPack.name : null,
                info.iconPack != null ? info.iconPack.packageName : null, info.itemState));
    }

    @Override
    public void openFolderIconSetting(IconPackManager.IconPack iconPack, Slot folder) {
        startActivity(SetItemIconView.getIntent(null, folder.slotId, this, null,
                iconPack == null ? null : iconPack.packageName,
                -1));
    }

    @Override
    public void resetFolderIcon(Slot folder, Realm realm) {
        Utility.createAndSaveFolderThumbnail(folder, realm, this, iconPack);
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
                .setItems(new CharSequence[]{"3","4","5", "6", "7", "8"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onChooseCollectionSize(which + 3);
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
