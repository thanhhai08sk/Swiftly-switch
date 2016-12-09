package org.de_studio.recentappswitcher.folderSetting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.DragAndDropCallback;
import org.de_studio.recentappswitcher.base.adapter.ItemsAdapter;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerFolderSettingComponent;
import org.de_studio.recentappswitcher.dagger.FolderSettingModule;
import org.de_studio.recentappswitcher.folderSetting.addActionToFolder.AddActionToFolderView;
import org.de_studio.recentappswitcher.folderSetting.addAppToFolder.AddAppToFolderView;
import org.de_studio.recentappswitcher.folderSetting.addContactToFolder.AddContactToFolderView;
import org.de_studio.recentappswitcher.folderSetting.addShortcutToFolder.AddShortcutToFolderView;
import org.de_studio.recentappswitcher.model.Item;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/2/16.
 */

public class FolderSettingView extends BaseActivity implements FolderSettingPresenter.View {
    private static final String TAG = FolderSettingView.class.getSimpleName();
    private String folderId;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.delete_image_button)
    ImageButton deleteButton;

    @Inject
    FolderSettingPresenter presenter;
    @Inject
    ItemsAdapter adapter;

    PublishSubject<DragAndDropCallback.MoveData> moveItemSubject = PublishSubject.create();
    PublishSubject<DragAndDropCallback.DropData> dropItemSubject = PublishSubject.create();
    PublishSubject<DragAndDropCallback.Coord> currentlyDragSubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        folderId = getIntent().getStringExtra(Cons.SLOT_ID);
        super.onCreate(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DragAndDropCallback(moveItemSubject
                , dropItemSubject
                , currentlyDragSubject));

        itemTouchHelper.attachToRecyclerView(recyclerView);


    }

    PublishSubject<Void> addItemSubject = PublishSubject.create();
    PublishSubject<Void> addAppsSubject = PublishSubject.create();
    PublishSubject<Void> addActionsSubject = PublishSubject.create();
    PublishSubject<Void> addContactsSubject = PublishSubject.create();
    PublishSubject<Void> addShortcutsSubject = PublishSubject.create();


    @Override
    protected void inject() {
        DaggerFolderSettingComponent.builder()
                .appModule(new AppModule(this))
                .folderSettingModule(new FolderSettingModule(this,folderId))
                .build().inject(this);
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.folder_setting_view;
    }

    @Override
    public void getDataFromRetainFragment() {

    }

    @Override
    public void onDestroyBySystem() {

    }

    @Override
    public PublishSubject<Void> onAddItemToFolder() {
        return addItemSubject;
    }

    @Override
    public PublishSubject<Void> onAddApps() {
        return addAppsSubject;
    }

    @Override
    public PublishSubject<Void> onAddActions() {
        return addActionsSubject;
    }

    @Override
    public PublishSubject<Void> onAddContacts() {
        return addContactsSubject;
    }

    @Override
    public PublishSubject<Void> onAddShortcuts() {
        return addShortcutsSubject;
    }

    @Override
    public PublishSubject<DragAndDropCallback.MoveData> onMoveItem() {
        return moveItemSubject;
    }

    @Override
    public PublishSubject<DragAndDropCallback.Coord> onCurrentlyDrag() {
        return currentlyDragSubject;
    }

    @Override
    public PublishSubject<DragAndDropCallback.DropData> onDropItem() {
        return dropItemSubject;
    }

    @Override
    public float getDeleteButtonY() {
        return deleteButton.getY();
    }

    @Override
    public void setDeleteButtonVisibility(boolean visible) {
        deleteButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setDeleteButtonColor(boolean redColor) {
        deleteButton.setBackgroundResource(redColor ? R.drawable.delete_button_red : R.drawable.delete_button_normal);
    }

    @Override
    public void notifyItemMove(int from, int to) {
        adapter.notifyItemMoved(from, to);
    }

    @Override
    public void notifyItemRemove(int position) {
//        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void chooseTypeOfItemsToAdd() {
        Utility.showDialogWithOptionToChoose(this, 0, new CharSequence[]{getString(R.string.apps)
                        , getString(R.string.actions), getString(R.string.contacts), getString(R.string.shortcut)}
                , new PublishSubject[]{addAppsSubject, addActionsSubject, addContactsSubject, addShortcutsSubject});
    }

    @Override
    public void addApps() {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        AddAppToFolderView newFragment1 = AddAppToFolderView.newInstance(folderId);
        newFragment1.show(fragmentManager1, "addAppToFolder");
    }

    @Override
    public void addActions() {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        AddActionToFolderView newFragment1 = AddActionToFolderView.newInstance(folderId);
        newFragment1.show(fragmentManager1, "addActionToFolder");
    }

    @Override
    public void addContacts() {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        AddContactToFolderView newFragment1 = AddContactToFolderView.newInstance(folderId);
        newFragment1.show(fragmentManager1, "addContactToFolder");

    }

    @Override
    public void addShortcuts() {
        FragmentManager fragmentManager1 = getSupportFragmentManager();
        AddShortcutToFolderView newFragment1 = AddShortcutToFolderView.newInstance(folderId);
        newFragment1.show(fragmentManager1, "addShortcutToFolder");

    }

    @Override
    public void setAdapter(OrderedRealmCollection<Item> folderItems) {
        Log.e(TAG, "setAdapter: size =   " + folderItems.size());
        adapter.updateData(folderItems);
    }

    @Override
    public void clear() {

    }

    @OnClick(R.id.fab)
    void onFabClick(){
        addItemSubject.onNext(null);
    }

    public static Intent getIntent(Context context, String folderId) {
        Intent intent = new Intent(context, FolderSettingView.class);
        intent.putExtra(Cons.SLOT_ID, folderId);
        return intent;
    }

}
