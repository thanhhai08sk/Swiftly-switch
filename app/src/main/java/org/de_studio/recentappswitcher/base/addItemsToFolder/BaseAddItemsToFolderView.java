package org.de_studio.recentappswitcher.base.addItemsToFolder;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.adapter.ItemsListWithCheckBoxAdapter;
import org.de_studio.recentappswitcher.model.Item;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/3/16.
 */

public class BaseAddItemsToFolderView extends DialogFragment implements BaseAddItemsToFolderPresenter.View , AdapterView.OnItemClickListener{
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
    Unbinder unbinder;

    public static BaseAddItemsToFolderView newInstance(String slotId) {

        Bundle args = new Bundle();
        args.putString(Cons.SLOT_ID, slotId);
        BaseAddItemsToFolderView fragment = new BaseAddItemsToFolderView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        slotId = getArguments().getString(Cons.SLOT_ID);
        inject();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_favorite_app_fragment_list_view, container);
        unbinder = ButterKnife.bind(this, view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        presenter.onViewAttach(this);
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroy() {
        presenter.onViewDetach();
        super.onDestroy();
    }

    protected void inject(){}

    @Override
    public void loadItems() {

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


    @Override
    public void clear() {
        unbinder.unbind();
    }
}
