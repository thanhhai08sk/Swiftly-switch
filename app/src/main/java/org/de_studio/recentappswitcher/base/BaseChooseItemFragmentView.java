package org.de_studio.recentappswitcher.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.adapter.ItemsListAdapter;
import org.de_studio.recentappswitcher.model.Item;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.realm.RealmResults;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public abstract class BaseChooseItemFragmentView<P extends BaseChooseItemPresenter> extends BaseFragment<P> implements BaseChooseItemPresenter.View{
    private static final String TAG = BaseChooseItemFragmentView.class.getSimpleName();
    @BindView(R.id.list_view)
    protected RecyclerView listView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.contact_permission)
    View contactPermission;

    @Inject
    protected ItemsListAdapter adapter;



    protected BehaviorSubject<Item> currentItemChangeSubject;
    protected PublishSubject<Item> setItemSubject;
    protected PublishSubject<Item> itemClickSubject = PublishSubject.create();
    protected PublishSubject<Void> onViewCreatedSJ = PublishSubject.create();

    protected PublishSubject<Void> needContactPermissionSJ = PublishSubject.create();
    protected PublishSubject<Void> contactPermissionGrantedSJ = PublishSubject.create();

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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            switch (requestCode) {
                case Cons.REQUEST_CODE_CONTACT_PERMISSION:
                    Log.e(TAG, "onActivityResult: contact permission ok");
                    boolean permissionOk = false;
                    for (int grantResult : grantResults) {
                        permissionOk = grantResult == PackageManager.PERMISSION_GRANTED;
                        if (!permissionOk) {
                            break;
                        }
                    }
                    if (permissionOk) {
                        contactPermissionGrantedSJ.onNext(null);
                    }
                    break;
            }

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
        if (progressBar != null) {
            if (show) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setAdapter(RealmResults<Item> items) {
        adapter.updateData(items);
        listView.setAdapter(adapter);
    }

    public void setCurrentItem(Item item) {
        adapter.setCurrentItem(item);
    }

    public abstract void loadItems();

    @Override
    public PublishSubject<Void> onNeedContactPermission() {
        return needContactPermissionSJ;
    }

    @Override
    public PublishSubject<Void> onContactPermissionGranted() {
        return contactPermissionGrantedSJ;
    }

    @Override
    public void showNeedContactButton(boolean visible) {
        Log.e(TAG, "showNeedContactButton: " + visible);
        contactPermission.setVisibility(visible ? View.VISIBLE : View.GONE);
        listView.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.choose_app_view;
    }



    @Override
    public void dismissIfDialog() {

    }

    @Override
    public void noticeUserAboutScreenLock() {
        Utility.noticeUserAboutScreenLock(getActivity());
    }


    @OnClick(R.id.contact_permission)
    void onContackPermissionClick(){
//        ActivityCompat.requestPermissions(getActivityForContext(),
//                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},
//                Cons.REQUEST_CODE_CONTACT_PERMISSION);
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE},Cons.REQUEST_CODE_CONTACT_PERMISSION);
    }

}
