package org.de_studio.recentappswitcher.setItems.chooseAction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseFragment;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.adapter.ItemsListAdapter;
import org.de_studio.recentappswitcher.model.Item;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.OrderedRealmCollection;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/19/16.
 */

public class ChooseActionView extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = ChooseActionView.class.getSimpleName();
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    ItemsListAdapter adapter;
    @Inject
    ChooseActionPresenter presenter;

    BehaviorSubject<Item> currentItemChangeSubject;
    PublishSubject<Item> setItemSubject;





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setOnItemClickListener(this);
    }


    public void setSubjects(BehaviorSubject<Item> currentItemChangeSubject, PublishSubject<Item> setItemSubject) {
        this.currentItemChangeSubject = currentItemChangeSubject;
        this.setItemSubject = setItemSubject;
    }

    public void setProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void setAdapter(OrderedRealmCollection<Item> items) {
        adapter.updateData(items);
        listView.setAdapter(adapter);
    }

    public void setCurrentItem(Item item) {
        adapter.setCurrentItem(item);
    }

    public BehaviorSubject<Item> onCurrentItemChange() {
        return currentItemChangeSubject;
    }

    public PublishSubject<Item> onSetItemSubject() {
        return setItemSubject;
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
    protected void inject() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = ((Item) parent.getAdapter().getItem(position));
        if (item != null) {
            Log.e(TAG, "onItemClick: " + position);
            presenter.onItemClick(item);
        }
    }

    public void loadActions() {

    }
}
