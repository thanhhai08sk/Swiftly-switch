package org.de_studio.recentappswitcher.main.edgeSetting;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseFragment;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerEdgeSettingComponent;
import org.de_studio.recentappswitcher.dagger.EdgeSettingModule;
import org.de_studio.recentappswitcher.main.triggerZoneSetting.TriggerZoneSettingView;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.RealmResults;
import rx.subjects.PublishSubject;


/**
 * Created by HaiNguyen on 11/5/16.
 */

public class EdgeSettingView extends BaseFragment<EdgeSettingPresenter> implements EdgeSettingPresenter.View{
    private static final String TAG = EdgeSettingView.class.getSimpleName();
    @BindView(R.id.enable_edge)
    Switch enableSwitch;
    @BindView(R.id.avoid_keyboard_switch)
    Switch avoidKeyboardSwitch;
    @BindView(R.id.show_guide_switch)
    Switch showGuideSwitch;
    @BindView(R.id.mode_description)
    TextView currentModeText;
    @BindView(R.id.recent_set)
    View recent;
    @BindView(R.id.recent_set_separator)
    View recentSeparator;
    @BindView(R.id.quick_actions_set)
    View quickAction;
    @BindView(R.id.quick_actions_set_separator)
    View quickActionSeparator;
    @BindView(R.id.circle_favorite_set)
    View circleFavorite;
    @BindView(R.id.circle_favorite_set_separator)
    View circleFavoriteSeparator;
    @BindView(R.id.grid_favorite_set)
    View gridFavorite;
    @BindView(R.id.grid_favorite_set_separator)
    View gridFavoriteSeparator;
    @BindView(R.id.recent_set_description)
    TextView recentSetDescription;
    @BindView(R.id.quick_actions_set_description)
    TextView quickActionsSetDescription;
    @BindView(R.id.grid_favorite_set_description)
    TextView gridFavoriteSetDescription;
    @BindView(R.id.circle_favorite_set_description)
    TextView circleFavoriteSetDescription;


    PublishSubject<Void> setDataCompleteSJ = PublishSubject.create();
    PublishSubject<Void> updateLayoutRequestSJ = PublishSubject.create();
    String edgeId;

    public static EdgeSettingView newInstance(String edgeId) {

        Bundle args = new Bundle();
        args.putString(Cons.EDGE_ID, edgeId);
        EdgeSettingView fragment = new EdgeSettingView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        edgeId = getArguments().getString(Cons.EDGE_ID);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateLayoutRequestSJ.onNext(null);
    }


    @Override
    public PublishSubject<Void> onSetDataComplete() {
        return setDataCompleteSJ;
    }

    @Override
    public PublishSubject<Void> onUpdateLayoutRequest() {
        return updateLayoutRequestSJ;
    }

    @Override
    public void showEdge(Edge edge, boolean enable) {
        setEnable(enable);
        avoidKeyboardSwitch.setChecked(edge.keyboardOption != Edge.KEYBOARD_OPTION_NONE);
        showGuideSwitch.setChecked(edge.useGuide);
        setCurrentMode(edge.mode);
        if (edge.recent != null) {
            recentSetDescription.setText(edge.recent.label);
        }
        if (edge.quickAction != null) {
            quickActionsSetDescription.setText(edge.quickAction.label);
        }
        if (edge.circleFav != null) {
            circleFavoriteSetDescription.setText(edge.circleFav.label);
        }
        if (edge.grid != null) {
            gridFavoriteSetDescription.setText(edge.grid.label);
        }
    }

    @Override
    public void chooseMode(int currentMode, final PublishSubject<Integer> setModeSubject) {

        new MaterialDialog.Builder(getActivity())
                .title(R.string.edge_dialog_set_mode_text)
                .items(R.array.edge_modes)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        setModeSubject.onNext(which);
                    }
                })
                .show();
    }

    @Override
    public void chooseCollection(final RealmResults<Collection> collections, Collection currentCollection, final PublishSubject<String> setCollectionSetSj) {
        final CharSequence[] labels = new CharSequence[collections.size()];
        for (int i = 0; i < collections.size(); i++) {
            labels[i] = collections.get(i).label;
        }

        final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
            @Override
            public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                setCollectionSetSj.onNext(collections.where().equalTo(Cons.LABEL, labels[index].toString()).findFirst().collectionId);
                dialog.dismiss();
            }
        });

        int icon = 0;
        switch (currentCollection.type) {
            case Collection.TYPE_CIRCLE_FAVORITE:
                icon = R.drawable.ic_circle_favorite_set;
                break;
            case Collection.TYPE_QUICK_ACTION:
                icon = R.drawable.ic_quick_actions_set;
                break;
            case Collection.TYPE_RECENT:
                icon = R.drawable.ic_recent_set;
                break;
            case Collection.TYPE_GRID_FAVORITE:
                icon = R.drawable.ic_grid_favorite_set;
                break;
        }


        for (Collection collection : collections) {
            adapter.add(new MaterialSimpleListItem.Builder(getActivity())
                    .content(collection.label)
                    .icon(icon)
                    .backgroundColor(Color.WHITE)
                    .build());

        }
        new MaterialDialog.Builder(getActivity())
                .adapter(adapter, null)
                .show();
    }


    @Override
    public void chooseGuideColor(int currentColor, final PublishSubject<Integer> setGuideColorSj) {
        ColorPickerDialogBuilder
                .with(getActivity())
                .setTitle(getString(R.string.main_set_guide_color))
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        setGuideColorSj.onNext(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    @Override
    public void restartService() {
        Utility.restartService(getActivity());
    }

    @Override
    public void showPositionSetting(Edge edge) {
        Log.e(TAG, "showPositionSetting: ");
        TriggerZoneSettingView view = TriggerZoneSettingView.newInstance(edge.edgeId);
        view.show(getFragmentManager(), "TriggerZone");
    }


    public void setEnable(boolean enable) {
        enableSwitch.setChecked(enable);
    }

    public void setCurrentMode(int mode) {
        String currentMode = null;
        switch (mode) {
            case Edge.MODE_RECENT_AND_QUICK_ACTION:
                currentMode = getString(R.string.edge_mode__recent_and_quick_actions);
                recent.setVisibility(View.VISIBLE);
                recentSeparator.setVisibility(View.VISIBLE);
                quickAction.setVisibility(View.VISIBLE);
                quickActionSeparator.setVisibility(View.VISIBLE);
                circleFavorite.setVisibility(View.GONE);
                circleFavoriteSeparator.setVisibility(View.GONE);
                gridFavorite.setVisibility(View.GONE);
                gridFavoriteSeparator.setVisibility(View.GONE);
                break;
            case Edge.MODE_CIRCLE_FAV_AND_QUICK_ACTION:
                currentMode = getString(R.string.edge_mode__circle_favorite_and_quick_actions);
                recent.setVisibility(View.GONE);
                recentSeparator.setVisibility(View.GONE);
                quickAction.setVisibility(View.VISIBLE);
                quickActionSeparator.setVisibility(View.VISIBLE);
                circleFavorite.setVisibility(View.VISIBLE);
                circleFavoriteSeparator.setVisibility(View.VISIBLE);
                gridFavorite.setVisibility(View.GONE);
                gridFavoriteSeparator.setVisibility(View.GONE);
                break;
            case Edge.MODE_GRID:
                currentMode = getString(R.string.edge_mode__grid_favorite);
                recent.setVisibility(View.GONE);
                recentSeparator.setVisibility(View.GONE);
                quickAction.setVisibility(View.GONE);
                quickActionSeparator.setVisibility(View.GONE);
                circleFavorite.setVisibility(View.GONE);
                circleFavoriteSeparator.setVisibility(View.GONE);
                gridFavorite.setVisibility(View.VISIBLE);
                gridFavoriteSeparator.setVisibility(View.VISIBLE);
                break;
        }
        currentModeText.setText(currentMode);
    }

    @Override
    public void registerSetDataCompleteEven() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.edge_setting_view;
    }

    @Override
    protected void inject() {
        DaggerEdgeSettingComponent.builder()
                .appModule(new AppModule(getActivity()))
                .edgeSettingModule(new EdgeSettingModule(this,edgeId))
                .build().inject(this);
    }

    @OnClick(R.id.enable_layout)
    void enableClick(){
        presenter.onEnable();
    }

    @OnClick(R.id.mode_layout)
    void modeClick(){
        presenter.onSetMode();
    }

    @OnClick(R.id.trigger_zone)
    void triggerZoneClick(){
        presenter.onSetPosition();
    }

    @OnClick(R.id.recent_set)
    void recenClick(){
        presenter.onSetRecent();
    }

    @OnClick(R.id.quick_actions_set)
    void quickActionsClick(){
        presenter.onSetQuickActions();
    }
    @OnClick(R.id.circle_favorite_set)
    void circleClick(){
        presenter.onSetCircle();
    }

    @OnClick(R.id.grid_favorite_set)
    void gridClick(){
        presenter.onSetGrid();
    }
    @OnClick(R.id.avoid_keyboard)
    void avoidKeyboardClick(){
        presenter.onSetAvoidKeyboard();
    }
    @OnClick(R.id.show_guide)
    void showGuideClick(){
        presenter.onSetShowGuide();
    }

    @OnClick(R.id.set_guide_color)
    void guideColorClick() {
        presenter.onSetGuideColor();
    }


}
