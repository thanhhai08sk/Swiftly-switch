package org.de_studio.recentappswitcher.main.edgeSetting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
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
    @BindView(R.id.show_guide_switch)
    Switch showGuideSwitch;
    @BindView(R.id.mode_description)
    TextView currentModeText;
    @BindView(R.id.recent_set)
    View recent;
    @BindView(R.id.quick_actions_set)
    View quickAction;
    @BindView(R.id.circle_favorite_set)
    View circleFavorite;
    @BindView(R.id.grid_favorite_set)
    View gridFavorite;

    PublishSubject<Void> setDataCompleteSJ = PublishSubject.create();
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
    public PublishSubject<Void> onSetDataComplete() {
        return setDataCompleteSJ;
    }

    @Override
    public void chooseMode(int currentMode, final PublishSubject<Integer> setModeSubject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        CharSequence[] modes = new CharSequence[]{getString(R.string.edge_mode__recent_and_quick_actions)
                , getString(R.string.edge_mode__circle_favorite_and_quick_actions)
                , getString(R.string.edge_mode__grid_favorite)};
        final int[] modeValues = new int[]{Edge.MODE_RECENT_AND_QUICK_ACTION
                , Edge.MODE_CIRCLE_FAV_AND_QUICK_ACTION
                , Edge.MODE_GRID};
        int currentChoice = 0;
        for (int i = 0; i < modeValues.length; i++) {
            if (currentMode == modeValues[i]) {
                currentChoice = i;
            }
        }

        builder.setTitle(R.string.edge_dialog_set_mode_text)
                .setSingleChoiceItems(modes, currentChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setModeSubject.onNext(modeValues[which]);
                    }
                })
                .setPositiveButton(R.string.app_tab_fragment_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
    }

    @Override
    public void chooseCollection(final RealmResults<Collection> collections, Collection currentCollection, final PublishSubject<String> setCollectionSetSj) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CharSequence[] labels = new CharSequence[collections.size()];
        for (int i = 0; i < collections.size(); i++) {
            labels[i] = collections.get(i).label;
        }
        int currentChoice = 0;
        for (int i = 0; i < labels.length; i++) {
            if (currentCollection.label.equals(labels[i])) {
                currentChoice = i;
            }
        }
        builder.setTitle(R.string.choose_recent_set)
                .setSingleChoiceItems(labels, currentChoice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setCollectionSetSj.onNext(collections.where().equalTo(Cons.LABEL, labels[which].toString()).findFirst().collectionId);
                    }
                })
                .setPositiveButton(R.string.app_tab_fragment_ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
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
    public void showPositionSetting(Edge edge) {
        Log.e(TAG, "showPositionSetting: ");
        TriggerZoneSettingView view = TriggerZoneSettingView.newInstance(edge.edgeId);
        view.show(getFragmentManager(), "TriggerZone");
    }


    @Override
    public void setEnable(boolean enable) {
        enableSwitch.setChecked(enable);
    }

    @Override
    public void setCurrentMode(int mode) {
        String currentMode = null;
        switch (mode) {
            case Edge.MODE_RECENT_AND_QUICK_ACTION:
                currentMode = getString(R.string.edge_mode__recent_and_quick_actions);
                recent.setVisibility(View.VISIBLE);
                quickAction.setVisibility(View.VISIBLE);
                circleFavorite.setVisibility(View.GONE);
                gridFavorite.setVisibility(View.GONE);
                break;
            case Edge.MODE_CIRCLE_FAV_AND_QUICK_ACTION:
                currentMode = getString(R.string.edge_mode__circle_favorite_and_quick_actions);
                recent.setVisibility(View.GONE);
                quickAction.setVisibility(View.VISIBLE);
                circleFavorite.setVisibility(View.VISIBLE);
                gridFavorite.setVisibility(View.GONE);
                break;
            case Edge.MODE_GRID:
                currentMode = getString(R.string.edge_mode__grid_favorite);
                recent.setVisibility(View.GONE);
                quickAction.setVisibility(View.GONE);
                circleFavorite.setVisibility(View.GONE);
                gridFavorite.setVisibility(View.VISIBLE);
                break;
        }
        currentModeText.setText(currentMode);
    }

    @Override
    public void setCurrentRecent(String label) {

    }

    @Override
    public void setCurrentQuickActions(String label) {

    }

    @Override
    public void setCurrentCircle(String label) {

    }

    @Override
    public void setCurrentGrid(String label) {

    }

    @Override
    public void setShowGuideEnable(boolean enable) {
        showGuideSwitch.setChecked(enable);
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
    @OnClick(R.id.show_guide)
    void showGuideClick(){
        presenter.onSetShowGuide();
    }

    @OnClick(R.id.set_guide_color)
    void guideColorClick() {
        presenter.onSetGuideColor();
    }


}
