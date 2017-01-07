package org.de_studio.recentappswitcher.main.edgeSetting;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseFragment;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;

import butterknife.OnClick;
import io.realm.RealmResults;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class EdgeSettingView extends BaseFragment<EdgeSettingPresenter> implements EdgeSettingPresenter.View{

    PublishSubject<Void> setDataCompleteSJ = PublishSubject.create();

    @Override
    public PublishSubject<Void> onSetDataComplete() {
        return setDataCompleteSJ;
    }

    @Override
    public void chooseMode(int currentMode, PublishSubject<Integer> setModeSubject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.edge_dialog_set_mode_text)
                .setItems(new CharSequence[]{getString(R.string.edge_mode__recent_and_quick_actions)
                        , getString(R.string.edge_mode__circle_favorite_and_quick_actions)
                        , getString(R.string.edge_mode__grid_favorite)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
    }

    @Override
    public void chooseRecent(RealmResults<Collection> recents, Collection currentRecent, PublishSubject<String> setRecentSetSj) {

    }

    @Override
    public void chooseQuickActions(RealmResults<Collection> quickActions, Collection currentQuickActions, PublishSubject<String> setQuickActionsSj) {

    }

    @Override
    public void chooseCircle(RealmResults<Collection> circles, Collection currentCircle, PublishSubject<String> setCircleSj) {

    }

    @Override
    public void chooseGrid(RealmResults<Collection> grids, Collection currentGrid, PublishSubject<String> setGridSj) {

    }

    @Override
    public void chooseGuideColor(int currentColor, PublishSubject<Integer> setGuideColorSj) {

    }

    @Override
    public void showPositionSetting(Edge edge) {

    }

    @Override
    public void setEnable(boolean enable) {

    }

    @Override
    public void setCurrentMode(int mode) {

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

    @OnClick(R.id.recent)
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
