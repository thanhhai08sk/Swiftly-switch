package org.de_studio.recentappswitcher.setItems;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerSetItemsComponent;
import org.de_studio.recentappswitcher.dagger.SetItemsModule;
import org.de_studio.recentappswitcher.model.Item;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class SetItemsView extends BaseActivity {
    private static final String TAG = SetItemsView.class.getSimpleName();
    public static final int ITEMS_TYPE_STAGE_1 = 1;
    public static final int ITEMS_TYPE_STAGE_2 = 2;
    public static final int ITEMS_TYPE_FOLDER = 3;
    public static final String KEY_ITEMS_TYPE = "itemsType";
    public static final String KEY_ITEM_INDEX = "itemIndex";
    public static final String KEY_COLLECTION_ID = "collectionId";
    public static final String KEY_SLOT_ID = "slotId";//in case of folder

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.index)
    TextView index;
    @BindView(R.id.icon)
    ImageView icon;


    int itemsType;
    int itemIndex;
    String collectionId;
    String slotId;

    @Inject
    SetItemsPresenter presenter;
    @Inject
    SetItemsModel model;
    @Inject
    SetItemsPagerAdapter adapter;
    @Nullable
    @Inject
    IconPackManager.IconPack iconPack;

    BehaviorSubject<Item> currentItemChangeSubject = BehaviorSubject.create();
    PublishSubject<Item> setItemSubject = PublishSubject.create();
    PublishSubject<Void> nextButtonSubject = PublishSubject.create();
    PublishSubject<Void> previousButtonSubject = PublishSubject.create();
    PublishSubject<Void> okButtonSubject = PublishSubject.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        itemsType = getIntent().getIntExtra(KEY_ITEMS_TYPE, ITEMS_TYPE_STAGE_1);
        itemIndex = getIntent().getIntExtra(KEY_ITEM_INDEX, 0);
        collectionId = getIntent().getStringExtra(KEY_COLLECTION_ID);
        slotId = getIntent().getStringExtra(KEY_SLOT_ID);
        super.onCreate(savedInstanceState);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);

    }





    public BehaviorSubject<Item> onCurrentItemChange() {
        return currentItemChangeSubject;
    }

    public PublishSubject<Item> onSetItem() {
        return setItemSubject;
    }

    public PublishSubject<Void> onNextButton() {
        return nextButtonSubject;
    }

    public PublishSubject<Void> onPreviousButton() {
        return previousButtonSubject;
    }

    public PublishSubject<Void> onOkButton() {
        return okButtonSubject;
    }

    @Override
    protected void inject() {
        DaggerSetItemsComponent.builder()
                .appModule(new AppModule(this))
                .setItemsModule(new SetItemsModule(this, itemsType, itemIndex, collectionId, slotId))
                .build().inject(this);
    }

    public void showCurrentIconAndIndex(Item currentItem, int itemIndex) {
        if (currentItem != null) {
            Utility.setItemIcon(currentItem, this, icon, getPackageManager(), iconPack);
        } else {
            icon.setImageDrawable(null);
        }
        index.setText(String.valueOf(itemIndex + 1));
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.set_items_view;
    }

    @Override
    protected void clear() {

    }
    @OnClick(R.id.next)
    void onNextClick(){
        nextButtonSubject.onNext(null);
    }
    @OnClick(R.id.previous)
    void onPreviousClick(){
        previousButtonSubject.onNext(null);
    }
    @OnClick(R.id.ok)
    void onOkClick(){
        okButtonSubject.onNext(null);
    }

    public static Intent getIntent(Context context, int itemsType, int itemIndex, String collectionId, String slotId) {
        Intent intent = new Intent(context, SetItemsView.class);
        intent.putExtra(KEY_ITEMS_TYPE, itemsType);
        intent.putExtra(KEY_ITEM_INDEX, itemIndex);
        intent.putExtra(KEY_COLLECTION_ID, collectionId);
        intent.putExtra(KEY_SLOT_ID, slotId);
        return intent;
    }
}
