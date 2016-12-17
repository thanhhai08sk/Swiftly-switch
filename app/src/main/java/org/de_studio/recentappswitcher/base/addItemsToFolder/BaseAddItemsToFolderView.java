package org.de_studio.recentappswitcher.base.addItemsToFolder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseDialogFragment;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.OnDialogClosed;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.base.adapter.ItemsListWithCheckBoxAdapter;
import org.de_studio.recentappswitcher.model.Item;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/3/16.
 */

public abstract class BaseAddItemsToFolderView extends BaseDialogFragment implements BaseAddItemsToFolderPresenter.View , AdapterView.OnItemClickListener{
    private static final String TAG = BaseAddItemsToFolderView.class.getSimpleName();
    @BindView(R.id.add_favorite_list_view)
    protected ListView listView;
    @BindView(R.id.progress_bar)
    protected ProgressBar progressBar;

    @Inject
    protected BaseAddItemsToFolderPresenter presenter;
    @Inject
    protected ItemsListWithCheckBoxAdapter adapter;


    protected PublishSubject<Item> setItemSubject = PublishSubject.create();
    protected String slotId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        slotId = getArguments().getString(Cons.SLOT_ID);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() != null) {
            ((OnDialogClosed) getActivity()).dialogClosed();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.add_favorite_app_fragment_list_view;
    }

    @Override
    protected PresenterView getPresenterView() {
        return this;
    }

    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         setItemSubject.onNext(adapter.getItem(position));
    }

    @Override
    public void setProgressBar(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void setAdapter(OrderedRealmCollection<Item> result, RealmList<Item> folderItems) {
        adapter.updateData(result);
        adapter.setCheckedItems(folderItems);
    }

    @Override
    public PublishSubject<Item> onSetItem() {
        return setItemSubject;
    }

}
