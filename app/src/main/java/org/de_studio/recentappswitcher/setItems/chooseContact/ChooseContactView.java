package org.de_studio.recentappswitcher.setItems.chooseContact;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseFragment;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.model.Item;

import butterknife.BindView;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/25/16.
 */

public class ChooseContactView extends BaseFragment implements AdapterView.OnItemClickListener  {
    private static final String TAG = ChooseContactView.class.getSimpleName();
    @BindView(R.id.list_view)
    ListView listView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    BehaviorSubject<Item> currentItemChangeSubject;
    PublishSubject<Item> setItemSubject;

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void inject() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
