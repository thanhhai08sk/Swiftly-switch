package org.de_studio.recentappswitcher.blackListSetting;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseDialogFragment;
import org.de_studio.recentappswitcher.base.adapter.ItemsListWithCheckBoxAdapter;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.BlackListSettingModule;
import org.de_studio.recentappswitcher.dagger.DaggerBlackListSettingComponent;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.setItems.chooseApp.ChooseAppFragmentView;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import butterknife.BindView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmList;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 12/17/16.
 */

public class BlackListSettingView extends BaseDialogFragment<BlackListSettingPresenter> implements BlackListSettingPresenter.View
{
    private static final String TAG = BlackListSettingView.class.getSimpleName();
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.add_favorite_list_view)
    protected ListView listView;


    @Inject
    protected ItemsListWithCheckBoxAdapter adapter;


    protected PublishSubject<Item> setItemSubject = PublishSubject.create();


    public static BlackListSettingView newInstance() {

        Bundle args = new Bundle();
        BlackListSettingView fragment = new BlackListSettingView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.add_favorite_app_fragment_list_view;
    }

    @Override
    public void loadApps() {
        ChooseAppFragmentView.LoadAppsTask task = new ChooseAppFragmentView.LoadAppsTask(new WeakReference<PackageManager>(getActivity().getPackageManager()));
        task.execute();
    }

    @Override
    public void setProgressBar(boolean show) {
        progressBar.setVisibility(show? View.VISIBLE: View.GONE);
    }

    @Override
    public void setAdapter(OrderedRealmCollection<Item> appList, RealmList<Item> blackListItems) {
        adapter.updateData(appList);
        adapter.setCheckedItems(blackListItems);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setItemSubject.onNext(adapter.getItem(position));
    }

    @Override
    public PublishSubject<Item> onSetItem() {
        return setItemSubject;
    }

    @Override
    protected void inject() {
        DaggerBlackListSettingComponent.builder()
                .appModule(new AppModule(getActivity()))
                .blackListSettingModule(new BlackListSettingModule(this))
                .build().inject(this);
    }





}
