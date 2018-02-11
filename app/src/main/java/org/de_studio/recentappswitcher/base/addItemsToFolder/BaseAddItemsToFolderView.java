package org.de_studio.recentappswitcher.base.addItemsToFolder;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseDialogFragment;
import org.de_studio.recentappswitcher.base.OnDialogClosed;
import org.de_studio.recentappswitcher.base.adapter.ItemsListWithCheckBoxAdapter;
import org.de_studio.recentappswitcher.model.Item;

import javax.inject.Inject;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/3/16.
 */

public abstract class BaseAddItemsToFolderView extends BaseDialogFragment<BaseAddItemsToFolderPresenter> implements BaseAddItemsToFolderPresenter.View{
    private static final String TAG = BaseAddItemsToFolderView.class.getSimpleName();
    @BindView(R.id.add_favorite_list_view)
    protected RecyclerView listView;
    @BindView(R.id.progress_bar)
    protected ProgressBar progressBar;

    @Inject
    protected ItemsListWithCheckBoxAdapter adapter;


    protected PublishSubject<Item> setItemSubject = PublishSubject.create();
    protected PublishSubject<Void> layoutedSJ = PublishSubject.create();
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
        adapter.onItemClicked().subscribe(new Consumer<Item>() {
            @Override
            public void accept(Item item) throws Exception {
                setItemSubject.onNext(item);
            }
        });
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
    public PublishSubject<Void> onLayouted() {
        return layoutedSJ;
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
