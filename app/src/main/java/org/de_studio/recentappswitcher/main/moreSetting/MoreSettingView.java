package org.de_studio.recentappswitcher.main.moreSetting;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackSettingDialogFragment;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.backup.GoogleDriveBackup;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerMoreSettingComponent;
import org.de_studio.recentappswitcher.dagger.MoreSettingModule;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 1/14/17.
 */

public class MoreSettingView extends BaseActivity<Void, MoreSettingPresenter> implements MoreSettingPresenter.View {
    private static final String TAG = MoreSettingView.class.getSimpleName();
    private int REQUEST_CODE_PICKER = 2;
    private int REQUEST_CODE_SELECT = 3;
    
    @BindView(R.id.disable_clock_switch)
    SwitchCompat disableClockSwitch;
//    @BindView(R.id.avoid_keyboard_switch)
//    SwitchCompat avoidKeyboardSwitch;
    @BindView(R.id.disable_in_landscape_switch)
    SwitchCompat disableInLandscapeSwitch;
    @BindView(R.id.contact_action_description)
    TextView contactActionDescription;
    @BindView(R.id.icon_pack_description)
    TextView iconPackDescription;
    @BindView(R.id.icon_size_description)
    TextView iconSizeDescription;
    @BindView(R.id.animation_switch)
    SwitchCompat animationSwitch;
    @BindView(R.id.animation_time_description)
    TextView animationTimeDescription;
    @BindView(R.id.haptic_feedback_on_trigger_switch)
    SwitchCompat hapticFeedbackOnTriggerSwitch;
    @BindView(R.id.haptic_feedback_on_icon_switch)
    SwitchCompat hapticFeedbackOnIconSwitch;
    @BindView(R.id.vibration_duration_description)
    TextView vibrationDurationDescription;
    @BindView(R.id.use_home_button_switch)
    SwitchCompat useHomeButtonSwitch;
    @BindView(R.id.use_home_button_layout)
    View useHomeButtonLayout;
    @BindView(R.id.use_home_button_separator)
    View useHomeButtonSeparator;
    GoogleDriveBackup backup;
    GoogleApiClient mGoogleApiClient;
    private IntentSender intentPicker;



    @Inject
    @Named(Cons.SHARED_PREFERENCE_NAME)
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backup = new GoogleDriveBackup();
        backup.init(this);
        connectClient();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Log.e(TAG, "onActivityResult: connect result ok");
                    backup.start();
                } else {
                    Log.e(TAG, "onActivityResult: connect result fail");
                }
                break;
            // REQUEST_CODE_PICKER
            case 2:
                intentPicker = null;

                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

//                    uploadToDrive(mFolderDriveId);
                }
                break;

            // REQUEST_CODE_SELECT
            case 3:
                if (resultCode == RESULT_OK) {
                    // get the selected item's ID
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    DriveFile file = driveId.asDriveFile();
//                    downloadFromDrive(file);

                } else {
                    showErrorDialog();
                }
                finish();
                break;

        }
    }

    @Override
    public void resetService() {
        Utility.restartService(this);
    }

    @Override
    public void updateViews() {
        disableClockSwitch.setChecked(sharedPreferences.getBoolean(Cons.DISABLE_CLOCK_KEY, false));
//        avoidKeyboardSwitch.setChecked(sharedPreferences.getBoolean(Cons.AVOID_KEYBOARD_KEY, true));
        disableInLandscapeSwitch.setChecked(sharedPreferences.getBoolean(Cons.IS_DISABLE_IN_LANDSCAPE_KEY, false));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            useHomeButtonLayout.setVisibility(View.GONE);
            useHomeButtonSeparator.setVisibility(View.GONE);
            useHomeButtonSwitch.setChecked(isAssistApp());
        } else {
            useHomeButtonLayout.setVisibility(View.GONE);
            useHomeButtonSeparator.setVisibility(View.GONE);
        }

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
        iconSizeDescription.setText(String.valueOf(100 * sharedPreferences.getFloat(Cons.ICON_SCALE_KEY, 1f)) + "%");
        animationSwitch.setChecked(sharedPreferences.getBoolean(Cons.USE_ANIMATION_KEY, true));
        animationTimeDescription.setText(String.valueOf(sharedPreferences.getInt(Cons.ANIMATION_TIME_KEY, Cons.ANIMATION_TIME_DEFAULT)) + "ms" );
        hapticFeedbackOnTriggerSwitch.setChecked(!sharedPreferences.getBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, true));
        hapticFeedbackOnIconSwitch.setChecked(sharedPreferences.getBoolean(Cons.HAPTIC_ON_ICON_KEY, false));
        vibrationDurationDescription.setText(String.valueOf(sharedPreferences.getInt(Cons.VIBRATION_DURATION_KEY, Cons.DEFAULT_VIBRATE_DURATION)) + "ms");




    }

    @Override
    public boolean isAssistApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String assistant =
                    Settings.Secure.getString(getContentResolver(),
                            "voice_interaction_service");
            return assistant != null && assistant.contains("de_studio");
        } else {
            return false;
        }
    }

    @Override
    public void assistAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enable_assist_app);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setMessage(R.string.enable_long_press_to_toggle_swiftly_switch)
                    .setPositiveButton(R.string.go_to_setting, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_VOICE_INPUT_SETTINGS);
                            startActivity(intent);
                        }
                    });
        } else {
            builder.setMessage(R.string.assist_app_is_only_available_for_mashmallow_and_above)
                    .setPositiveButton(R.string.app_tab_fragment_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // nothing
                        }
                    });
        }
        builder.create().show();
    }

    @Override
    public void contactActionDialog() {
        int current = sharedPreferences.getInt(Cons.CONTACT_ACTION_KEY, Cons.ACTION_CHOOSE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.default_action_for_contact).
                setSingleChoiceItems(R.array.contact_action, current, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.setDefaultContactAction(which);
                    }
                }).
                setPositiveButton(R.string.app_tab_fragment_ok_button, null);
        builder.create().show();
    }

    @Override
    public void longPressDelayDialog(PublishSubject<Integer> subject) {
        Utility.showDialogWithSeekBar(
                Cons.HOLD_TIME_MIN,
                Cons.HOLD_TIME_MAX,
                sharedPreferences.getInt(Cons.HOLD_TIME_KEY, Cons.HOLD_TIME_DEFAULT),
                "ms", getString(R.string.main_hold_time),
                subject, this);
    }

    @Override
    public void chooseIconPackDialog() {
        if (Utility.isTrial(this)) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.main_icon_pack_trial_dialog_message)
                    .setPositiveButton(R.string.main_edge_switch_2_trial_buy_pro_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse("mbarket://details?id=" + Cons.PRO_VERSION_PACKAGE_NAME);
                            Intent gotoMarket = new Intent(Intent.ACTION_VIEW, uri);
                            gotoMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            try {
                                startActivity(gotoMarket);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + Cons.PRO_VERSION_PACKAGE_NAME)));
                            }
                        }
                    })
                    .setNegativeButton(R.string.edge_dialog_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
            builder.show();


        } else {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            IconPackSettingDialogFragment newFragment = new IconPackSettingDialogFragment();
            newFragment.show(fragmentManager, "iconPackDialogFragment");
        }
    }

    @Override
    public void iconSizeDialog(PublishSubject<Integer> subject) {
        Utility.showDialogWithSeekBar(
                70,
                130,
                ((int) (sharedPreferences.getFloat(Cons.ICON_SCALE_KEY, 1f) * 100f)),
                "%",
                getString(R.string.main_icon_size),
                subject,
                this
        );
    }

    @Override
    public void backgroundColorDialog() {
        int currentColor = sharedPreferences.getInt(Cons.BACKGROUND_COLOR_KEY,Cons.BACKGROUND_COLOR_DEFAULT);
        ColorPickerDialogBuilder
                .with(this)
                .setTitle(getApplicationContext().getString(R.string.main_set_background_color))
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        presenter.onSetBackgroundColor(selectedColor);
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
    public void animationDurationDialog(PublishSubject<Integer> subject) {
        Utility.showDialogWithSeekBar(
                Cons.ANIMATION_TIME_MIN,
                Cons.ANIMATION_TIME_MAX,
                sharedPreferences.getInt(Cons.ANIMATION_TIME_KEY, Cons.ANIMATION_TIME_DEFAULT),
                "ms",
                getString(R.string.main_ani_time),
                subject, this
        );
    }

    @Override
    public void vibrationDurationDialog(PublishSubject<Integer> subject) {
        Utility.showDialogWithSeekBar(
                Cons.HOLD_TIME_MIN,
                Cons.HOLD_TIME_MAX,
                sharedPreferences.getInt(Cons.HOLD_TIME_KEY, Cons.HOLD_TIME_DEFAULT),
                "ms",
                getString(R.string.main_hold_time),
                subject, this
        );
    }

    @Override
    protected void inject() {
        DaggerMoreSettingComponent.builder()
                .appModule(new AppModule(this))
                .moreSettingModule(new MoreSettingModule(this))
                .build().inject(this);
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

    @OnClick(R.id.disable_clock)
    void onDisableClockClick(){
        presenter.onDisableClock();
    }

//    @OnClick(R.id.avoid_keyboard)
//    void onAvoidKeyboardClick(){
//        presenter.onAvoidKeyboard();
//    }

    @OnClick(R.id.disable_in_landscape)
    void onDisableInLandscapeClick(){
        presenter.onDisableInLandscape();
    }

    @OnClick(R.id.use_home_button_layout)
    void onUseHomeClick(){
        presenter.onUseHomeButton();
    }

    @OnClick(R.id.contact_action)
    void onContactActionClick(){
        presenter.onDefaultActionForContacts();
    }

    @OnClick(R.id.long_press_delay)
    void onLongPressDelayClick(){
        presenter.onLongPressDelay();
    }

    @OnClick(R.id.icon_pack)
    void onIconPackClick(){
        presenter.onIconPack();
    }

    @OnClick(R.id.icon_size)
    void onIconSizeClick(){
        presenter.onIconSize();
    }

    @OnClick(R.id.background_color)
    void onBackgroudnColorClick(){
        presenter.onBackgroundColor();
    }

    @OnClick(R.id.use_animation)
    void onUseAnimationClick(){
        presenter.onAnimation();
    }

    @OnClick(R.id.animation_time)
    void onAnimationTimeClick(){
        presenter.onAnimationDuration();
    }

    @OnClick(R.id.haptic_feedback_on_trigger)
    void onHapticOnTriggerClick(){
        presenter.onHapticOnTrigger();
    }

    @OnClick(R.id.haptic_feedback_on_icon)
    void onHapticIconClick(){
        presenter.onHapticOnIcon();
    }

    @OnClick(R.id.vibration_duration)
    void onVibrationDurationClick(){
        presenter.onVibratioDuration();
    }

    @OnClick(R.id.import_settings)
    void onImportSettingClick(){
        presenter.onImportSetting();
    }

    @OnClick(R.id.backup)
    void onBackupClick(){
        presenter.onBackup();
        mGoogleApiClient = backup.getClient();
        openFolderPicker();

    }

    @OnClick(R.id.reset_to_default)
    void onResetClick(){
        presenter.onResetSettings();
    }

    public void connectClient() {
        backup.start();
    }

    public void disconnectClient() {
        backup.stop();
    }

    private void openFolderPicker() {
        Log.e(TAG, "openFolderPicker: ");
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (intentPicker == null)
                    intentPicker = buildIntent();
                //Start the picker to choose a folder
                startIntentSenderForResult(
                        intentPicker, REQUEST_CODE_PICKER, null, 0, 0, 0);
            } else {
                Log.e(TAG, "openFolderPicker: error");
            }
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Unable to send intent", e);
            showErrorDialog();
        }
    }

    private IntentSender buildIntent() {
        return Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(mGoogleApiClient);
    }
    private void showErrorDialog() {
        Toast.makeText(getApplicationContext(), R.string.backup_fail, Toast.LENGTH_SHORT).show();
    }
}
