package org.de_studio.recentappswitcher.main.moreSetting;

import android.content.SharedPreferences;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;

import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 1/14/17.
 */

public class MoreSettingPresenter extends BasePresenter<MoreSettingPresenter.View, BaseModel> implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences sharedPreferences;
    PublishSubject<Integer> longPressDelaySJ = PublishSubject.create();
    PublishSubject<Integer> iconSizeSJ = PublishSubject.create();
    PublishSubject<Integer> animationDurationSJ = PublishSubject.create();
    PublishSubject<Integer> vibrationDurationSJ = PublishSubject.create();


    public MoreSettingPresenter(BaseModel model, SharedPreferences sharedPreferences) {
        super(model);
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void onViewAttach(View view) {
        super.onViewAttach(view);
        view.updateViews();
        addSubscription(
                longPressDelaySJ.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        onSetLongPressDelay(integer);
                    }
                })
        );

        addSubscription(
                iconSizeSJ.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        onSetIconSize(((float) integer) / (100f));
                    }
                })
        );

        addSubscription(
                animationDurationSJ.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        onSetAnimationDuration(integer);
                    }
                })
        );

        addSubscription(
                vibrationDurationSJ.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        onSetVibrationDuration(integer);
                    }
                })
        );



    }

    public void onDisableClock() {
        boolean currentSetting = sharedPreferences.getBoolean(Cons.DISABLE_CLOCK_KEY, false);
        sharedPreferences.edit().putBoolean(Cons.DISABLE_CLOCK_KEY, !currentSetting).commit();
        view.resetService();
    }

    public void onAvoidKeyboard() {
        boolean currentSetting = sharedPreferences.getBoolean(Cons.AVOID_KEYBOARD_KEY, true);
        sharedPreferences.edit().putBoolean(Cons.AVOID_KEYBOARD_KEY, !currentSetting).commit();
        view.resetService();
    }

    public void onDisableInLandscape() {
        boolean currentSetting = sharedPreferences.getBoolean(Cons.IS_DISABLE_IN_LANDSCAPE_KEY, false);
        sharedPreferences.edit().putBoolean(Cons.IS_DISABLE_IN_LANDSCAPE_KEY, !currentSetting).commit();
        view.resetService();
    }

    public void onUseHomeButton() {
        view.assistAppDialog();
    }

    public void onDefaultActionForContacts() {
        view.contactActionDialog();
    }

    public void setDefaultContactAction(int action) {
        sharedPreferences.edit().putInt(Cons.CONTACT_ACTION_KEY, action).commit();
    }

    public void onLongPressDelay() {
        view.longPressDelayDialog(longPressDelaySJ);
    }

    public void onSetLongPressDelay(int time) {
        sharedPreferences.edit().putInt(Cons.HOLD_TIME_KEY, time).commit();
        view.resetService();
    }

    public void onIconPack() {
        view.chooseIconPackDialog();
    }

    public void onSetIconPack(String iconPackPackage) {
        sharedPreferences.edit().putString(Cons.ICON_PACK_PACKAGE_NAME_KEY, iconPackPackage).commit();
        view.resetService();
    }

    public void onIconSize() {
        view.iconSizeDialog(iconSizeSJ);
    }

    public void onSetIconSize(float size) {
        sharedPreferences.edit().putFloat(Cons.ICON_SCALE_KEY, size).commit();
        view.resetService();
    }

    public void onBackgroundColor() {
        view.backgroundColorDialog();
    }

    public void onSetBackgroundColor(int color) {
        sharedPreferences.edit().putInt(Cons.BACKGROUND_COLOR_KEY, color).commit();
        view.resetService();
    }

    public void onAnimation() {
        boolean currentValue = sharedPreferences.getBoolean(Cons.USE_ANIMATION_KEY, true);
        sharedPreferences.edit().putBoolean(Cons.USE_ANIMATION_KEY, !currentValue).commit();
        view.resetService();
    }

    public void onAnimationDuration() {
        view.animationDurationDialog(animationDurationSJ);
    }

    public void onSetAnimationDuration(int duration) {
        sharedPreferences.edit().putInt(Cons.ANIMATION_TIME_KEY, duration).commit();
        view.resetService();
    }

    public void onHapticOnTrigger() {
        boolean currentValue = sharedPreferences.getBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, true);
        sharedPreferences.edit().putBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, !currentValue).commit();
        view.resetService();
    }

    public void onHapticOnIcon() {
        boolean currentValue = sharedPreferences.getBoolean(Cons.HAPTIC_ON_ICON_KEY, false);
        sharedPreferences.edit().putBoolean(Cons.HAPTIC_ON_ICON_KEY, !currentValue).commit();
        view.resetService();
    }

    public void onVibratioDuration() {
        view.vibrationDurationDialog(vibrationDurationSJ);
    }

    public void onSetVibrationDuration(int duration) {
        sharedPreferences.edit().putInt(Cons.VIBRATION_DURATION_KEY, duration).commit();
        view.resetService();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        view.updateViews();
    }

    @Override
    public void onViewDetach() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        sharedPreferences = null;
        super.onViewDetach();
    }

    public interface View extends PresenterView {
        void resetService();
        void updateViews();

        boolean isAssistApp();

        void assistAppDialog();

        void contactActionDialog();

        void longPressDelayDialog(PublishSubject<Integer> subject);

        void chooseIconPackDialog();

        void iconSizeDialog(PublishSubject<Integer> subject);

        void backgroundColorDialog();

        void animationDurationDialog(PublishSubject<Integer> subject);

        void vibrationDurationDialog(PublishSubject<Integer> subject);
    }
}
