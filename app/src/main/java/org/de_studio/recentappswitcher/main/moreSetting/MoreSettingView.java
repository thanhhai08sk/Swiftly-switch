package org.de_studio.recentappswitcher.main.moreSetting;

import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseActivity;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;

/**
 * Created by HaiNguyen on 1/14/17.
 */

public class MoreSettingView extends BaseActivity<Void, MoreSettingPresenter> implements MoreSettingPresenter.View {
    
    @BindView(R.id.disable_clock_switch)
    Switch disableClockSwitch;
    @BindView(R.id.avoid_keyboard_switch)
    Switch avoidKeyboardSwitch;
    @BindView(R.id.disable_in_landscape_switch)
    Switch disableInLandscapeSwitch;
    @BindView(R.id.contact_action_description)
    TextView contactActionDescription;
    @BindView(R.id.icon_pack_description)
    TextView iconPackDescription;
    @BindView(R.id.icon_size_description)
    TextView iconSizeDescription;
    @BindView(R.id.animation_switch)
    Switch animationSwitch;
    @BindView(R.id.animation_time_description)
    TextView animationTimeDescription;
    @BindView(R.id.haptic_feedback_on_trigger_switch)
    Switch hapticFeedbackOnTriggerSwitch;
    @BindView(R.id.haptic_feedback_on_icon_switch)
    Switch hapticFeedbackOnIconSwitch;
    @BindView(R.id.vibration_duration_description)
    TextView vibrationDurationDescription;
    @BindView(R.id.use_home_button_switch)
    Switch useHomeButtonSwitch;
    @BindView(R.id.use_home_button_layout)
    View useHomeButtonLayout;
    @BindView(R.id.use_home_button_separator)
    View useHomeButtonSeparator;

    


    @Inject
    @Named(Cons.SHARED_PREFERENCE_NAME)
    SharedPreferences sharedPreferences;


    @Override
    public void resetService() {
        Utility.restartService(this);
    }

    @Override
    public void updateViews() {
        disableClockSwitch.setChecked(sharedPreferences.getBoolean(Cons.DISABLE_CLOCK_KEY, false));
        avoidKeyboardSwitch.setChecked(sharedPreferences.getBoolean(Cons.AVOID_KEYBOARD_KEY, true));
        disableInLandscapeSwitch.setChecked(sharedPreferences.getBoolean(Cons.IS_DISABLE_IN_LANDSCAPE_KEY, false));
        int contactAction = sharedPreferences.getInt(Cons.CONTACT_ACTION_KEY, Cons.ACTION_CHOOSE);
        switch (contactAction) {
            case Cons.ACTION_CHOOSE:
                contactActionDescription.setText(R.string.choose);
                break;
            case Cons.ACTION_CALL:
                contactActionDescription.setText(R.string.call);
                break;
            case Cons.ACTION_SMS:
                contactActionDescription.setText(R.string.sms);
                break;
        }

        String iconPackPackage = sharedPreferences.getString(Cons.ICON_PACK_PACKAGE_NAME_KEY, null);
        if (iconPackPackage != null) {
            iconPackDescription.setText(Utility.getLabelFromPackageName(iconPackPackage, getPackageManager()));
        } else {
            iconPackDescription.setText(getString(R.string.system));
        }
        iconSizeDescription.setText(String.valueOf(sharedPreferences.getFloat(Cons.ICON_SCALE_KEY, 1f)));
        animationSwitch.setChecked(sharedPreferences.getBoolean(Cons.USE_ANIMATION_KEY, true));
        animationTimeDescription.setText(sharedPreferences.getInt(Cons.ANIMATION_TIME_KEY, Cons.ANIMATION_TIME_DEFAULT));
        hapticFeedbackOnTriggerSwitch.setChecked(!sharedPreferences.getBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, true));
        hapticFeedbackOnIconSwitch.setChecked(sharedPreferences.getBoolean(Cons.HAPTIC_ON_ICON_KEY, false));
        vibrationDurationDescription.setText(String.valueOf(sharedPreferences.getInt(Cons.VIBRATION_DURATION_KEY, Cons.DEFAULT_VIBRATE_DURATION)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String assistant =
                    Settings.Secure.getString(getContentResolver(),
                            "voice_interaction_service");
            if (assistant != null && assistant.contains("de_studio")) {
                useHomeButtonSwitch.setChecked(true);
            } else {
                useHomeButtonSwitch.setChecked(false);
            }
        } else {
            useHomeButtonLayout.setVisibility(View.GONE);
            useHomeButtonSeparator.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isAssistApp() {
        return false;
    }

    @Override
    public void assistAppDialog() {

    }

    @Override
    public void contactActionDialog() {

    }

    @Override
    public void longPressDelayDialog() {

    }

    @Override
    public void chooseIconPackDialog() {

    }

    @Override
    public void iconSizeDialog() {

    }

    @Override
    public void backgroundColorDialog() {

    }

    @Override
    public void animationDurationDialog() {

    }

    @Override
    public void vibrationDurationDialog() {

    }

    @Override
    protected void inject() {
        
    }

    @Override
    protected int getLayoutId() {
        return R.layout.more_setting_view;
    }

    @Override
    public void getDataFromRetainFragment() {

    }

    @Override
    public void onDestroyBySystem() {

    }

    @Override
    public void clear() {
        sharedPreferences = null;
    }
}
