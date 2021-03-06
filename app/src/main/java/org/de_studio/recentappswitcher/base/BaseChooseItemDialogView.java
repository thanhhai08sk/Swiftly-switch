package org.de_studio.recentappswitcher.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.adapter.ItemsListAdapter;
import org.de_studio.recentappswitcher.model.Item;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import io.realm.RealmResults;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public abstract class BaseChooseItemDialogView extends BaseDialogFragment<BaseChooseItemPresenter> implements BaseChooseItemPresenter.View {
    private static final String TAG = BaseChooseItemFragmentView.class.getSimpleName();
    @BindView(R.id.add_favorite_list_view)
    protected RecyclerView listView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @Inject
    protected ItemsListAdapter adapter;


    protected BehaviorSubject<Item> currentItemChangeSubject;
    protected PublishSubject<Item> setItemSubject;
    protected PublishSubject<Item> itemClickSubject = PublishSubject.create();
    protected PublishSubject<Void> onViewCreatedSJ = PublishSubject.create();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        onViewCreatedSJ.onNext(null);
        adapter.onItemClicked().subscribe(new Consumer<Item>() {
            @Override
            public void accept(Item item) throws Exception {
                itemClickSubject.onNext(item);
            }
        });
    }


    public void setSubjects(BehaviorSubject<Item> currentItemChangeSubject, PublishSubject<Item> setItemSubject) {
        this.currentItemChangeSubject = currentItemChangeSubject;
        this.setItemSubject = setItemSubject;
    }

    public BehaviorSubject<Item> onCurrentItemChange() {
        return currentItemChangeSubject;
    }

    @Override
    public PublishSubject<Item> onItemClick() {
        return itemClickSubject;
    }

    @Override
    public PublishSubject<Item> onSetItemToSlot() {
        return setItemSubject;
    }

    @Override
    public PublishSubject<Void> onViewCreated() {
        return onViewCreatedSJ;
    }

    public void setProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAdapter(RealmResults<Item> items) {
        adapter.updateData(items);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
    }

    public void setCurrentItem(Item item) {
        adapter.setCurrentItem(item);
    }

    public abstract void loadItems();

    @Override
    public PublishSubject<Void> onNeedContactPermission() {
        return null;
    }

    @Override
    public PublishSubject<Void> onContactPermissionGranted() {
        return null;
    }

    @Override
    public void showNeedContactButton(boolean visible) {
    }


    @Override
    protected int getLayoutRes() {
        return R.layout.add_favorite_app_fragment_list_view;
    }


    @Override
    public void dismissIfDialog() {
        dismiss();
    }

    @Override
    public void noticeUserAboutScreenLock() {
        Utility.noticeUserAboutScreenLock(getActivity());
    }

}
