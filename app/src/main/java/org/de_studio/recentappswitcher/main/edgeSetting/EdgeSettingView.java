package org.de_studio.recentappswitcher.main.edgeSetting;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.base.BaseFragment;

import butterknife.OnClick;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class EdgeSettingView extends BaseFragment<EdgeSettingPresenter> {



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
