package org.de_studio.recentappswitcher.base.collectionSetting;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Switch;
import android.widget.TextView;

import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.setItems.chooseShortcutsSet.ChooseShortcutsSetDialogView;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 2/17/17.
 */

public abstract class BaseCircleCollectionSettingView<T, P extends BaseCircleCollectionSettingPresenter> extends BaseCollectionSettingView<T,P> implements BaseCircleCollectionSettingPresenter.View {
    @BindView(R.id.size_text)
    TextView sizeDescription;
    @BindView(R.id.circle_size_description)
    TextView circleSizeDescription;
    @BindView(R.id.long_press_description)
    TextView longPressDescription;
    @BindView(R.id.stay_on_screen_switch)
    Switch stayOnScreenSwitch;
    @BindView(R.id.stay_on_screen_description)
    TextView stayOnScreenDescription;


    PublishSubject<Integer> chooseLongPressModeSJ = PublishSubject.create();


    @Override
    public PublishSubject<Integer> onChooseLongPressMode() {
        return chooseLongPressModeSJ;
    }

    @Override
    public boolean isHoverOnDeleteButton(float x, float y) {
        int[] deleteCoord = new int[2];
        deleteButton.getLocationOnScreen(deleteCoord);
        return  y > deleteCoord[1] - deleteButton.getHeight()*2;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.circle_favorite_setting;
    }

    @Override
    public void updateCollectionInfo(Collection collection) {
        super.updateCollectionInfo(collection);
        sizeDescription.setText(String.valueOf(collection.slots.size()));
        circleSizeDescription.setText(String.valueOf(collection.radius) + " dp");
        if (collection.longClickMode == Collection.LONG_CLICK_MODE_OPEN_COLLECTION &&
                collection.longPressCollection != null) {
            longPressDescription.setText(collection.longPressCollection.label);
        } else {
            longPressDescription.setText(R.string.long_press_action_description);
        }


        boolean stayOnScreen = collection.stayOnScreen == null ? true : collection.stayOnScreen;
        stayOnScreenSwitch.setChecked(stayOnScreen);
        if (stayOnScreen) {
            stayOnScreenDescription.setText(R.string.stay_on_screen_enable_description);
        } else stayOnScreenDescription.setText(R.string.stay_on_screen_disable_description);

    }

    @Override
    public void chooseLongPressMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.long_press_action);
        builder.setItems(new CharSequence[]{ getString(R.string.shortcuts_sets), getString(R.string.no_action)}
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                chooseLongPressModeSJ.onNext(Collection.LONG_CLICK_MODE_OPEN_COLLECTION);
                                break;
                            case 1:
                                chooseLongPressModeSJ.onNext(Collection.LONG_CLICK_MODE_NONE);
                                break;


                        }
                    }
                });
        builder.create().show();
    }

    @Override
    public void chooseLongPressCollection(PublishSubject<Item> chooseLongPressCollectionSJ,String collectionType) {
        ChooseShortcutsSetDialogView dialogView = ChooseShortcutsSetDialogView.newInstance(collectionType);
        dialogView.setSubjects(null, chooseLongPressCollectionSJ);
        dialogView.show(getSupportFragmentManager(), "chooseLongPressCollection");
    }

    @OnClick(R.id.circle_size)
    void onCircleSizeModeClick(){
        presenter.onCircleSize();
    }

    @OnClick(R.id.long_press_action)
    void onLongPressClick(){
        presenter.onLongPressAction();
    }

    @OnClick(R.id.stay_on_screen)
    void onStayOnScreenClick(){
        presenter.onSetStayOnScreen();
    }

}
