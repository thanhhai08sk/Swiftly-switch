package org.de_studio.recentappswitcher.setItems;

import android.content.Context;
import android.content.Intent;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.model.Item;

import butterknife.OnClick;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class SetItemsView extends BaseActivity {
    private static final String TAG = SetItemsView.class.getSimpleName();
    public static final int ITEMS_STYLE_STAGE_1 = 1;
    public static final int ITEMS_STYLE_STAGE_2 = 2;
    public static final int ITEMS_STYLE_FOLDER = 3;
    public static final String KEY_ITEMS_TYPE = "itemsType";
    public static final String KEY_ITEM_INDEX = "itemIndex";
    public static final String KEY_COLLECTION_ID = "collectionId";
    public static final String KEY_ITEM_ID = "itemId";//in case of folder


    int itemsType;
    int itemIndex;
    String collectionId;
    String itemId;

    BehaviorSubject<Item> currentItemChangeSubject = BehaviorSubject.create();
    PublishSubject<Item> setItemSubject = PublishSubject.create();
    PublishSubject<Void> nextButtonSubject = PublishSubject.create();
    PublishSubject<Void> previousButtonSubject = PublishSubject.create();
    PublishSubject<Void> okButtonSubject = PublishSubject.create();


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

    @Override
    protected void inject() {

    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
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

    public static Intent getIntent(Context context, int itemsType, int itemIndex, String collectionId, String itemId) {
        Intent intent = new Intent(context, SetItemsView.class);
        intent.putExtra(KEY_ITEMS_TYPE, itemsType);
        intent.putExtra(KEY_ITEM_INDEX, itemIndex);
        intent.putExtra(KEY_COLLECTION_ID, collectionId);
        intent.putExtra(KEY_ITEM_ID, itemId);
        return intent;
    }
}
