package org.de_studio.recentappswitcher.setItems.chooseApp;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.adapter.ItemsListAdapter;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.ChooseAppModule;
import org.de_studio.recentappswitcher.dagger.DaggerChooseAppComponent;
import org.de_studio.recentappswitcher.model.Item;

import java.lang.ref.WeakReference;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/18/16.
 */

public class ChooseAppView extends Fragment implements AdapterView.OnItemClickListener{
    private static final String TAG = ChooseAppView.class.getSimpleName();
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    @Inject
    ChooseAppPresenter presenter;
    @Inject
    ChooseAppModel model;
    @Inject
    ItemsListAdapter adapter;

    BehaviorSubject<Item> currentItemChangeSubject;
    PublishSubject<Item> setItemSubject;


    Unbinder unbinder;
    public static ChooseAppView newInstance() {

        Bundle args = new Bundle();

        ChooseAppView fragment  = new ChooseAppView();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        inject();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_app_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        listView.setOnItemClickListener(this);
        presenter.onViewAttach();
        return view;
    }

    @Override
    public void onDestroy() {
        presenter.onViewDetach();
        super.onDestroy();
    }

    public void setSubjects(BehaviorSubject<Item> currentItemChangeSubject, PublishSubject<Item> setItemSubject) {
        this.currentItemChangeSubject = currentItemChangeSubject;
        this.setItemSubject = setItemSubject;
    }

    public BehaviorSubject<Item> onCurrentItemChange() {
        return currentItemChangeSubject;
    }

    public PublishSubject<Item> onSetItemSubject() {
        return setItemSubject;
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

    public void loadApps() {
        LoadAppsTask loadAppsTask = new LoadAppsTask(new WeakReference<PackageManager>(getActivity().getPackageManager()));
        loadAppsTask.execute();
    }

    void inject() {
        DaggerChooseAppComponent.builder()
                .appModule(new AppModule(getActivity()))
                .chooseAppModule(new ChooseAppModule(this))
                .build().inject(this);
    }

    void clear() {
        unbinder.unbind();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Item item = ((Item) parent.getAdapter().getItem(position));
        if (item != null) {
            Log.e(TAG, "onItemClick: " + position);
            presenter.onItemClick(item);
        }
    }

    static class LoadAppsTask extends AsyncTask<Void, Void, Void> {
        WeakReference<PackageManager> packageManagerWeakReference;

        public LoadAppsTask(WeakReference<PackageManager> packageManagerWeakReference) {
            this.packageManagerWeakReference = packageManagerWeakReference;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Realm realm = Realm.getDefaultInstance();
            Set<PackageInfo> packageInfos = Utility.getInstalledApps(packageManagerWeakReference.get());
            Item tempItem;
            for (final PackageInfo info : packageInfos) {
                final String itemId = Item.TYPE_APP + info.packageName;
                tempItem = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
                if (tempItem == null) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Item item = new Item();
                            item.type = Item.TYPE_APP;
                            item.itemId = itemId;
                            item.packageName = info.packageName;
                            item.label = info.applicationInfo.loadLabel(packageManagerWeakReference.get()).toString();
                            realm.copyToRealm(item);
                        }
                    });
                }
            }
            realm.close();
            return null;
        }
    }
}
