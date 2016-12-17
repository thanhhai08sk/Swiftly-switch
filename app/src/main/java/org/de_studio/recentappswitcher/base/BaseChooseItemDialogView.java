package org.de_studio.recentappswitcher.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.adapter.ItemsListAdapter;
import org.de_studio.recentappswitcher.model.Item;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.RealmResults;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public abstract class BaseChooseItemDialogView extends BaseDialogFragment implements AdapterView.OnItemClickListener, BaseChooseItemPresenter.View {
    private static final String TAG = BaseChooseItemFragmentView.class.getSimpleName();
    @BindView(R.id.list_view)
    protected ListView listView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    protected ItemsListAdapter adapter;
    @Inject
    protected BaseChooseItemPresenter presenter;


    protected BehaviorSubject<Item> currentItemChangeSubject;
    protected PublishSubject<Item> setItemSubject;
    protected PublishSubject<Item> itemClickSubject = PublishSubject.create();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setOnItemClickListener(this);
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
    }

    public void setCurrentItem(Item item) {
        adapter.setCurrentItem(item);
    }





    public abstract void loadItems();

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = ((Item) parent.getAdapter().getItem(position));
        if (item != null) {
            itemClickSubject.onNext(item);
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.choose_app_view;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected PresenterView getPresenterView() {
        return this;
    }

}
