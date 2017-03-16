package org.de_studio.recentappswitcher.main.moreSetting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackListAdapter;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.backup.GoogleDriveBackup;
import org.de_studio.recentappswitcher.base.BaseActivity;
import org.de_studio.recentappswitcher.dagger.AppModule;
import org.de_studio.recentappswitcher.dagger.DaggerMoreSettingComponent;
import org.de_studio.recentappswitcher.dagger.MoreSettingModule;
import org.de_studio.recentappswitcher.main.MainView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 1/14/17.
 */

public class MoreSettingView extends BaseActivity<Void, MoreSettingPresenter> implements MoreSettingPresenter.View {
    private static final String TAG = MoreSettingView.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_FOLDER = 23232;
    private static final int REQUEST_CODE_SELECT_FILE = 32433;
    public static final int REQUEST_BACKUP = 11;
    public static final int REQUEST_RESTORE = 12;
    @BindView(R.id.disable_in_fullscreen_switch)
    SwitchCompat disableInFullScreenSwitch;
    @BindView(R.id.disable_clock_switch)
    SwitchCompat disableClockSwitch;
    @BindView(R.id.disable_indicator_switch)
    SwitchCompat disableIndicatorSwitch;
//    @BindView(R.id.avoid_keyboard_switch)
//    SwitchCompat avoidKeyboardSwitch;
    @BindView(R.id.open_folder_delay_switch)
    SwitchCompat openFolderDelaySwitch;
    @BindView(R.id.disable_in_landscape_switch)
    SwitchCompat disableInLandscapeSwitch;
    @BindView(R.id.contact_action_description)
    TextView contactActionDescription;
    @BindView(R.id.ringer_mode_action_description)
    TextView ringerModeActionDescription;
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

    PublishSubject<Void> googleClientConnectedSJ = PublishSubject.create();
    PublishSubject<Integer> backupOrRestoreRequestSJ = PublishSubject.create();
    PublishSubject<Integer> somethingWrongSJ = PublishSubject.create();
    PublishSubject<DriveId> pickupFolderSuccessSJ = PublishSubject.create();
    PublishSubject<DriveFile> pickupFileSuccessSJ = PublishSubject.create();
    PublishSubject<Void> backupSuccessful = PublishSubject.create();
    PublishSubject<Void> finishReadingGuideSJ = PublishSubject.create();

    MaterialDialog connectingDialog;
    MaterialDialog downloadingDialog;
    MaterialDialog uploadingDialog;



    @Inject
    @Named(Cons.SHARED_PREFERENCE_NAME)
    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            // REQUEST_CODE_PICK_FOLDER
            case REQUEST_CODE_PICK_FOLDER:
                intentPicker = null;

                if (resultCode == RESULT_OK) {
                    //Get the folder drive id
                    DriveId mFolderDriveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    pickupFolderSuccessSJ.onNext(mFolderDriveId);
                }
                break;

            // REQUEST_CODE_SELECT_FILE
            case REQUEST_CODE_SELECT_FILE:
                if (resultCode == RESULT_OK) {
                    // get the selected item's ID
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    DriveFile file = driveId.asDriveFile();
                    pickupFileSuccessSJ.onNext(file);

                }
                break;

        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mGoogleApiClient = backup.getClient();
        googleClientConnectedSJ.onNext(null);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public PublishSubject<Void> onGoogleApiClientConnected() {
        return googleClientConnectedSJ;
    }

    @Override
    public PublishSubject<Void> onFinishReadingGuide() {
        return finishReadingGuideSJ;
    }

    @Override
    public PublishSubject<Integer> onBackupOrRestoreSJ() {
        return backupOrRestoreRequestSJ;
    }

    @Override
    public PublishSubject<DriveId> onPickFolderSuccess() {
        return pickupFolderSuccessSJ;
    }

    @Override
    public PublishSubject<DriveFile> onPickFileToRestoreSuccess() {
        return pickupFileSuccessSJ;
    }

    @Override
    public PublishSubject<Integer> onSomethingWrong() {
        return somethingWrongSJ;
    }

    @Override
    public PublishSubject<Void> onBackupSuccessful() {
        return backupSuccessful;
    }

    @Override
    public void resetService() {
        Utility.restartService(this);
    }

    @Override
    public void updateViews() {
        disableInFullScreenSwitch.setChecked(sharedPreferences.getBoolean(Cons.DISABLE_IN_FULLSCREEN_KEY,false));
        disableClockSwitch.setChecked(sharedPreferences.getBoolean(Cons.DISABLE_CLOCK_KEY, false));
        disableIndicatorSwitch.setChecked(sharedPreferences.getBoolean(Cons.DISABLE_INDICATOR_KEY, false));
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

        openFolderDelaySwitch.setChecked(sharedPreferences.getBoolean(Cons.OPEN_FOLDER_DELAY_KEY, true));

        int contactAction = sharedPreferences.getInt(Cons.CONTACT_ACTION_KEY, Cons.CONTACT_ACTION_CHOOSE);
        switch (contactAction) {
            case Cons.CONTACT_ACTION_CHOOSE:
                contactActionDescription.setText(R.string.choose);
                break;
            case Cons.CONTACT_ACTION_CALL:
                contactActionDescription.setText(R.string.call);
                break;
            case Cons.CONTACT_ACTION_SMS:
                contactActionDescription.setText(R.string.sms);
                break;
        }

        int ringerModeAction = sharedPreferences.getInt(Cons.RINGER_MODE_ACTION_KEY, Cons.RINGER_MODE_ACTION_DEFAULT);
        switch (ringerModeAction) {
            case Cons.RINGER_MODE_ACTION_SOUND_AND_VIBRATE:
                ringerModeActionDescription.setText(R.string.ringer_mode_action_sound_and_vibrate);
                break;
            case Cons.RINGER_MODE_ACTION_SOUND_AND_SILENT:
                ringerModeActionDescription.setText(R.string.ringer_mode_action_sound_and_silent);
                break;
        }

        String iconPackPackage = sharedPreferences.getString(Cons.ICON_PACK_PACKAGE_NAME_KEY, Cons.ICON_PACK_NONE);
        if (!iconPackPackage.equals(Cons.ICON_PACK_NONE)) {
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
        new MaterialDialog.Builder(this)
                .title(R.string.default_action_for_contact)
                .items(R.array.contact_action)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        presenter.setDefaultContactAction(position);
                    }
                }).show();
    }

    @Override
    public void ringerModeActionDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.ringer_mode_action)
                .items(R.array.ringer_mode_actions)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        presenter.setRingerModeAction(position);
                    }
                }).show();
    }

    @Override
    public void longPressDelayDialog(PublishSubject<Integer> subject) {
        Utility.showDialogWithSeekBar(
                Cons.HOLD_TIME_MIN,
                Cons.HOLD_TIME_MAX,
                sharedPreferences.getInt(Cons.LONG_PRESS_DELAY_KEY, Cons.LONG_PRESS_DELAY_DEFAULT),
                "ms", getString(R.string.main_hold_time),
                subject, this);
    }

    @Override
    public void chooseIconPackDialog() {
        if (Utility.isFree(this)) {
            Utility.showProOnlyDialog(this);
        } else {
//            android.app.FragmentManager fragmentManager = getFragmentManager();
//            IconPackSettingDialogFragment newFragment = new IconPackSettingDialogFragment();
//            newFragment.show(fragmentManager, "iconPackDialogFragment");

            IconPackManager manager = new IconPackManager();
            manager.setContext(this);
            HashMap<String, IconPackManager.IconPack> hashMap = manager.getAvailableIconPacks(true);

            MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .customView(R.layout.dialog_fragment_icon_pack, false)
                    .positiveText(R.string.app_tab_fragment_ok_button)
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            resetService();
                        }
                    })
                    .show();
            ListView listView = (ListView) dialog.getView().findViewById(R.id.icon_pack_list_view);
            IconPackListAdapter mAdapter = new IconPackListAdapter(this, hashMap);
            listView.setAdapter(mAdapter);
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
                Cons.VIBRATION_TIME_MIN,
                Cons.VIBRATION_TIME_MAX,
                sharedPreferences.getInt(Cons.VIBRATION_DURATION_KEY, Cons.VIBRATION_TIME_DEFAULT),
                "ms",
                getString(R.string.main_vibration_duration),
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
        disconnectClient();
        sharedPreferences = null;
        connectingDialog = null;
        downloadingDialog = null;
        uploadingDialog = null;
    }


    @OnClick(R.id.disable_in_fullscreen)
    void onDisableInFullscreenClick(){
        if (Utility.isFree(this)) {
            Utility.showProOnlyDialog(this);
        } else {
            presenter.onDisableInFullscreen();
        }
    }
    @OnClick(R.id.disable_clock)
    void onDisableClockClick(){
        presenter.onDisableClock();
    }
    @OnClick(R.id.disable_indicator)
    void onDisableIndicatorClick(){
        presenter.onDisableIndicator();
    }

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

    @OnClick(R.id.ringer_mode_action)
    void ringerModeClick(){
        presenter.onRingerModeAction();
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

    @OnClick(R.id.open_folder_delay)
    void onOpenFolderDelayClick(){
        presenter.onOpenFolderDelay();
    }

    @OnClick(R.id.import_settings)
    void onImportSettingClick(){
        backupOrRestoreRequestSJ.onNext(REQUEST_RESTORE);
    }

    @OnClick(R.id.backup)
    void onBackupClick(){
        backupOrRestoreRequestSJ.onNext(REQUEST_BACKUP);
    }

    @OnClick(R.id.reset_to_default)
    void onResetClick(){
        presenter.onResetSettings();
    }

    public void connectClient() {
        backup = new GoogleDriveBackup();
        backup.init(this,this);
        backup.start();
    }

    public void disconnectClient() {
        if (backup != null) {
            backup.stop();
            backup = null;
        }
    }

    public void openFolderPicker() {
        Log.e(TAG, "openFolderPicker: ");
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (intentPicker == null)
                    intentPicker = buildIntent();
                //Start the picker to choose a folder
                startIntentSenderForResult(
                        intentPicker, REQUEST_CODE_PICK_FOLDER, null, 0, 0, 0);
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
    public void showErrorDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.something_wrong_happen)
                .positiveText(R.string.app_tab_fragment_ok_button)
                .show();
    }

    public void showSuccessDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.backup_successful)
                .positiveText(R.string.app_tab_fragment_ok_button)
                .show();
    }



    public void showLoadingDialog(int titleRes) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(R.layout.loading_dialog);
//        builder.show();
        new MaterialDialog.Builder(this)
                .title(titleRes)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }

    @Override
    public void showBackupGuideDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.backup_guide)
                .positiveText(R.string.app_tab_fragment_ok_button)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finishReadingGuideSJ.onNext(null);
                    }
                })
                .show();
    }

    @Override
    public void showImportGuideDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.import_guide)
                .positiveText(R.string.app_tab_fragment_ok_button)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        finishReadingGuideSJ.onNext(null);
                    }
                })
                .show();
    }

    @Override
    public void showConnectingDialog() {
        connectingDialog = new  MaterialDialog.Builder(this)
                .title(R.string.connecting)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideConnectingDialog() {
        connectingDialog.dismiss();
    }

    @Override
    public void showDownloadingDialog() {
        downloadingDialog =  new  MaterialDialog.Builder(this)
                .title(R.string.downloading)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideDownloadingDialog() {
        downloadingDialog.dismiss();
    }

    @Override
    public void showUploadingDialog() {
        uploadingDialog = new MaterialDialog.Builder(this)
                .title(R.string.uploading)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }

    @Override
    public void hideUploadingDialog() {
        uploadingDialog.dismiss();
    }

    public void openFilePicker() {
        //        build an intent that we'll use to start the open file activity
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
//                these mimetypes enable these folders/files types to be selected
                .setMimeType(new String[]{DriveFolder.MIME_TYPE, "text/plain"})
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_SELECT_FILE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Unable to send intent", e);
            somethingWrongSJ.onNext(REQUEST_RESTORE);
        }
    }

    public void uploadToDrive(final Realm realm, DriveId mFolderDriveId) {
        if (mFolderDriveId != null) {
            //Create the file on GDrive
            final DriveFolder folder = mFolderDriveId.asDriveFolder();
            Drive.DriveApi.newDriveContents(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(DriveApi.DriveContentsResult result) {
                            if (!result.getStatus().isSuccess()) {
                                Log.e(TAG, "Error while trying to create new file contents");
                                somethingWrongSJ.onNext(REQUEST_BACKUP);
                                return;
                            }
                            final DriveContents driveContents = result.getDriveContents();

                            String sharedFile = Environment.getDataDirectory().getAbsolutePath() + "/data/" + getPackageName() + "/" + Cons.SHARED_PREFERENCE_FOLDER_NAME + "/" + Cons.SHARED_PREFERENCE_NAME+".xml";

                            File file = new File(sharedFile);
                            if (!file.exists()) {
                                Log.e(TAG, "onResult: file not exist " + sharedFile +
                                "\nrealm file = " + realm.getPath());
                                File file1 = new File("/data/data/org.de_studio.recentappswitcher.fastbuild/shared_prefs/");
                                for (File file2 : file1.listFiles()) {
                                    Log.e(TAG, "onResult: file = " + file2.getAbsolutePath());

                                }
                                return;
                            }
                            String realmFile = realm.getPath();
                            final File zipFile = new File(getApplicationInfo().dataDir + "/" + Cons.BACKUP_FILE_NAME);
                            try {
                                Utility.zip(new String[]{sharedFile, realmFile}, zipFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.e(TAG, "onResult: IOException when zip");
                                somethingWrongSJ.onNext(REQUEST_BACKUP);
                                return;
                            }

                            // Perform I/O off the UI thread.
                            new Thread() {
                                @Override
                                public void run() {
                                    // write content to DriveContents
                                    OutputStream outputStream = driveContents.getOutputStream();

                                    FileInputStream inputStream = null;
                                    try {
                                        inputStream = new FileInputStream(zipFile);
                                    } catch (FileNotFoundException e) {
                                        somethingWrongSJ.onNext(REQUEST_BACKUP);
                                        Log.e(TAG, "run: file not found");
                                        e.printStackTrace();
                                    }

                                    byte[] buf = new byte[1024];
                                    int bytesRead;
                                    try {
                                        if (inputStream != null) {
                                            while ((bytesRead = inputStream.read(buf)) > 0) {
                                                outputStream.write(buf, 0, bytesRead);
                                            }
                                        }
                                    } catch (IOException e) {
                                        somethingWrongSJ.onNext(REQUEST_BACKUP);
                                        e.printStackTrace();
                                    }


                                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                            .setTitle(Cons.BACKUP_FILE_NAME)
                                            .setMimeType("text/plain")
                                            .build();

                                    // create a file in selected folder
                                    folder.createFile(mGoogleApiClient, changeSet, driveContents)
                                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                                @Override
                                                public void onResult(DriveFolder.DriveFileResult result) {
                                                    if (!result.getStatus().isSuccess()) {
                                                        Log.e(TAG, "Error while trying to create the file");
                                                        somethingWrongSJ.onNext(REQUEST_BACKUP);
                                                        return;
                                                    }
                                                    backupSuccessful.onNext(null);
                                                }
                                            });
                                }
                            }.start();
                        }
                    });
        }
    }

    public void downloadFromDrive(final Realm realm, DriveFile file) {
        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            somethingWrongSJ.onNext(REQUEST_RESTORE);
                            return;
                        }

                        // DriveContents object contains pointers
                        // to the actual byte stream
                        DriveContents contents = result.getDriveContents();
                        InputStream input = contents.getInputStream();

                        try {
                            File zipFile = new File(getApplicationInfo().dataDir + "/" + Cons.BACKUP_FILE_NAME);
                            OutputStream output = new FileOutputStream(zipFile);
                            try {
                                try {
                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                    int read;

                                    while ((read = input.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                    }
                                    output.flush();
                                } finally {
                                    output.close();
                                }

                                Utility.unzip(zipFile.getAbsolutePath(),getFilesDir().getAbsolutePath(),
                                        Environment.getDataDirectory().getAbsolutePath() + "/data/" + getPackageName() + "/" + Cons.SHARED_PREFERENCE_FOLDER_NAME + "/");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                input.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        // Reboot app
                        Intent mStartActivity = new Intent(getApplicationContext(), MainView.class);
                        int mPendingIntentId = 123456;
                        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        System.exit(0);
                    }
                });
    }
}
