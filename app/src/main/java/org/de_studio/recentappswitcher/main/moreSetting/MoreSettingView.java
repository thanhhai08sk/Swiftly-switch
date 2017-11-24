package org.de_studio.recentappswitcher.main.moreSetting;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import org.de_studio.recentappswitcher.ui.component.Dialog;
import org.jetbrains.annotations.NotNull;

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
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import rx.Single;
import rx.SingleSubscriber;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 1/14/17.
 */
@RuntimePermissions
public class MoreSettingView extends BaseActivity<Void, MoreSettingPresenter> implements MoreSettingPresenter.View {
    private static final String TAG = MoreSettingView.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_FOLDER = 23232;
    private static final int REQUEST_CODE_PICK_DRIVE_FILE = 32433;
    private static final int REQUEST_CODE_PICK_STORAGE_FILE = 32434;

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
    @BindView(R.id.transition_switch)
    SwitchCompat transitionSwitch;
    @BindView(R.id.disable_in_fullscreen_text)
    TextView disableInFullscreenText;
    GoogleDriveBackup backup;
    GoogleApiClient mGoogleApiClient;
    private IntentSender intentPicker;

    PublishSubject<Void> googleClientConnectedSJ = PublishSubject.create();
    PublishSubject<Integer> somethingWrongSJ = PublishSubject.create();
    PublishSubject<Pair<GoogleApiClient, DriveId>> pickupFolderSuccessSJ;
    PublishSubject<Pair<GoogleApiClient, DriveFile>> pickupDriveFileSuccessSJ;
    PublishSubject<String> pickupStorageFileSuccessSJ;
    PublishSubject<Void> backupSuccessful = PublishSubject.create();
    PublishSubject<Void> finishReadingGuideSJ = PublishSubject.create();
    PublishSubject<Void> importSJ = PublishSubject.create();
    PublishSubject<Void> exportSJ = PublishSubject.create();

    MaterialDialog connectingDialog;
    MaterialDialog importingDialog;
    MaterialDialog exportingDialog;



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
                    Log.e(TAG, "onActivityResult: pick folder ok");
                    pickupFolderSuccessSJ.onNext(new Pair<>(mGoogleApiClient,mFolderDriveId));
                }
                break;

            // REQUEST_CODE_PICK_DRIVE_FILE
            case REQUEST_CODE_PICK_DRIVE_FILE:
                if (resultCode == RESULT_OK) {
                    // get the selected item's ID
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    DriveFile file = driveId.asDriveFile();
                    pickupDriveFileSuccessSJ.onNext(new Pair<>(mGoogleApiClient, file));

                }
                break;
            case REQUEST_CODE_PICK_STORAGE_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.e(TAG, "onActivityResult: pick journey file ok");
                    pickupStorageFileSuccessSJ.onNext(data.getData().toString());
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MoreSettingViewPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (backup != null) {
//            mGoogleApiClient = backup.getClient();
            googleClientConnectedSJ.onNext(null);
        }
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

    @NotNull
    @Override
    public PublishSubject<Void> onImport() {
        return importSJ;
    }

    @NotNull
    @Override
    public PublishSubject<Void> onExport() {
        return exportSJ;
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
        setDisableInFullScreenModeTextColor();
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
        transitionSwitch.setChecked(sharedPreferences.getBoolean(Cons.USE_TRANSITION_KEY, false));
        animationTimeDescription.setText(String.valueOf(sharedPreferences.getInt(Cons.ANIMATION_TIME_KEY, Cons.ANIMATION_TIME_DEFAULT)) + "ms" );
        hapticFeedbackOnTriggerSwitch.setChecked(!sharedPreferences.getBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, true));
        hapticFeedbackOnIconSwitch.setChecked(sharedPreferences.getBoolean(Cons.HAPTIC_ON_ICON_KEY, false));
        vibrationDurationDescription.setText(String.valueOf(sharedPreferences.getInt(Cons.VIBRATION_DURATION_KEY, Cons.DEFAULT_VIBRATE_DURATION)) + "ms");




    }

    private void setDisableInFullScreenModeTextColor() {
        int textColor;
        if (Utility.isFree(this)) {
            textColor = R.color.text_primary_dark_disabled;
        } else textColor = R.color.text_primary_dark;
        disableInFullscreenText.setTextColor(ContextCompat.getColor(this, textColor));
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
                            presenter.resetFolderThumbnail();
                        }
                    })
                    .show();
            ListView listView = (ListView) dialog.getView().findViewById(R.id.icon_pack_list_view);
            IconPackListAdapter mAdapter = new IconPackListAdapter(this, hashMap);
            listView.setAdapter(mAdapter);
        }

    }


    @Override
    public Activity getActivityForContext() {
        return this;
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
        importingDialog = null;
        exportingDialog = null;
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
        int currentColor = sharedPreferences.getInt(Cons.BACKGROUND_COLOR_KEY,Cons.BACKGROUND_COLOR_DEFAULT);
        Dialog.INSTANCE.pickColor(this, currentColor, new Function1<Integer, Unit>() {
            @Override
            public Unit invoke(Integer integer) {
                presenter.onSetBackgroundColor(integer);
                return  Unit.INSTANCE;
            }
        });
    }

    @OnClick(R.id.folderBackgroundColor)
    void onFolderBackgroundClick(){
        int currentColor = sharedPreferences.getInt(Cons.FOLDER_BACKGROUND_COLOR_KEY, Cons.FOLDER_BACKGROUND_COLOR_DEFAULT);
        Dialog.INSTANCE.pickColor(this, currentColor, new Function1<Integer, Unit>() {
            @Override
            public Unit invoke(Integer integer) {
                presenter.onSetFolderBackgroundColor(integer);
                return  Unit.INSTANCE;
            }
        });
    }


    @OnClick(R.id.use_animation)
    void onUseAnimationClick(){
        presenter.onAnimation();
    }
    @OnClick(R.id.transition)
    void onTransitionClick(){
        presenter.onTransition();
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
        importSJ.onNext(null);
    }

    @OnClick(R.id.backup)
    void onBackupClick(){
        exportSJ.onNext(null);
    }

    @OnClick(R.id.reset_to_default)
    void onResetClick(){
        presenter.onResetSettings();
    }

    @NotNull
    @Override
    public Single<MoreSettingPresenter.MoreSettingResult> connectClientRX() {
        return Single.create(new Single.OnSubscribe<MoreSettingPresenter.MoreSettingResult>() {
            @Override
            public void call(final SingleSubscriber<? super MoreSettingPresenter.MoreSettingResult> singleSubscriber) {
                backup = new GoogleDriveBackup();
                backup.init(MoreSettingView.this, new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.e(TAG, "onConnected: client connected, thread = " + Thread.currentThread().getName());
                        singleSubscriber.onSuccess(new MoreSettingPresenter.MoreSettingResult(MoreSettingPresenter.MoreSettingResult.Type.CONNECT_CLIENT_SUCCESS,backup.getClient()));
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        singleSubscriber.onError(new Throwable("connect fail"));
                    }
                });
                backup.start();
            }
        });
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

    @NotNull
    @Override
    public PublishSubject<Pair<GoogleApiClient, DriveId>> openFolderPickerRx(@org.jetbrains.annotations.Nullable GoogleApiClient client) {
        openFolderPicker(client);
        mGoogleApiClient = client;
        pickupFolderSuccessSJ = PublishSubject.create();
        return pickupFolderSuccessSJ;
    }


    public void openFolderPicker(GoogleApiClient mGoogleApiClient) {
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                if (intentPicker == null)
                    intentPicker = buildIntent(mGoogleApiClient);
                //Start the picker to choose a folder
                Log.e(TAG, "openFolderPicker: open");
                startIntentSenderForResult(
                        intentPicker, REQUEST_CODE_PICK_FOLDER, null, 0, 0, 0);
            } else {
                Log.e(TAG, "openFolderPicker: error, api client not ready, thread = " + Thread.currentThread().getName());
            }
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Unable to send intent", e);
            showErrorDialog();
        }
    }
    private IntentSender buildIntent(GoogleApiClient client) {
        return Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                .build(client);
    }
    public void showErrorDialog() {
        Utility.showSimpleDialog(this, R.string.something_wrong_happen);
    }

    public void showBackupGoogleDriveOk() {
        Utility.showSimpleDialog(this, R.string.backup_successful);
    }

    @Override
    public void showBackupStorageOk() {
        Utility.showSimpleDialog(this,R.string.exported_storage_success);
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
        if (connectingDialog != null && !connectingDialog.isShowing()) {
            connectingDialog.show();
        } else {
            connectingDialog = Utility.showProgressDialog(this, R.string.connecting, R.string.please_wait);
        }
    }

    @Override
    public void hideConnectingDialog() {
        if (connectingDialog != null) {
            connectingDialog.dismiss();
        }
    }

    @Override
    public void showExportingDialog() {
        exportingDialog = Utility.showProgressDialog(this, R.string.exporting, R.string.please_wait);
    }

    @Override
    public void showImportingDialog() {
        Log.e(TAG, "showImportingDialog: ");
        importingDialog = Utility.showProgressDialog(this, R.string.importing, R.string.please_wait);
    }


    @Override
    public void hideExportingDialog() {
        exportingDialog.dismiss();
    }


    public void openFilePicker() {
        //        build an intent that we'll use to start the open file activity
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
//                these mimetypes enable these folders/files types to be selected
                .setMimeType(new String[]{DriveFolder.MIME_TYPE, "text/plain"})
                .build(null);
        try {
            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_PICK_DRIVE_FILE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Unable to send intent", e);
            somethingWrongSJ.onNext(REQUEST_RESTORE);
        }
    }

    @NotNull
    @Override
    public PublishSubject<Pair<GoogleApiClient, DriveFile>> pickDriveFile(@NonNull GoogleApiClient client) {
        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
//                these mimetypes enable these folders/files types to be selected
                .setMimeType(new String[]{DriveFolder.MIME_TYPE, "text/plain"})
                .build(client);
        mGoogleApiClient = client;
        pickupDriveFileSuccessSJ = PublishSubject.create();
        try {
            return pickupDriveFileSuccessSJ;
        }finally {
            try {
                startIntentSenderForResult(
                        intentSender, REQUEST_CODE_PICK_DRIVE_FILE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Unable to send intent", e);
                pickupDriveFileSuccessSJ.onError(new Throwable("unable to send intent"));
            }
        }

    }

    @NotNull
    @Override
    public Single<MoreSettingPresenter.MoreSettingResult> choosePlaceToBackup() {
        return Single.create(new Single.OnSubscribe<MoreSettingPresenter.MoreSettingResult>() {
            @Override
            public void call(final SingleSubscriber<? super MoreSettingPresenter.MoreSettingResult> singleSubscriber) {
                new MaterialDialog.Builder(MoreSettingView.this)
                        .items(R.array.backup_option)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                switch (position) {
                                    case 0:
                                        singleSubscriber.onSuccess(new MoreSettingPresenter.MoreSettingResult(MoreSettingPresenter.MoreSettingResult.Type.CHOOSE_PLACE_STORAGE, null));
                                        break;
                                    case 1:
                                        singleSubscriber.onSuccess(new MoreSettingPresenter.MoreSettingResult(MoreSettingPresenter.MoreSettingResult.Type.CHOOSE_PLACE_GOOGLE_DRIVE, null));
                                        break;
                                }
                            }
                        }).show();
            }
        });
    }

    @NotNull
    @Override
    public Single<MoreSettingPresenter.MoreSettingResult> choosePlaceToImport() {
        return Single.create(new Single.OnSubscribe<MoreSettingPresenter.MoreSettingResult>() {
            @Override
            public void call(final SingleSubscriber<? super MoreSettingPresenter.MoreSettingResult> singleSubscriber) {
                new MaterialDialog.Builder(MoreSettingView.this)
                        .items(R.array.import_options)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                switch (position) {
                                    case 0:
                                        singleSubscriber.onSuccess(new MoreSettingPresenter.MoreSettingResult(MoreSettingPresenter.MoreSettingResult.Type.CHOOSE_PLACE_STORAGE, null));
                                        break;
                                    case 1:
                                        singleSubscriber.onSuccess(new MoreSettingPresenter.MoreSettingResult(MoreSettingPresenter.MoreSettingResult.Type.CHOOSE_PLACE_GOOGLE_DRIVE, null));
                                        break;
                                }
                            }
                        }).show();
            }
        });
    }



    @NotNull
    @Override
    public Single<MoreSettingPresenter.MoreSettingResult> exportToStorage() {
        return Single.create(new Single.OnSubscribe<MoreSettingPresenter.MoreSettingResult>() {
            @Override
            public void call(SingleSubscriber<? super MoreSettingPresenter.MoreSettingResult> singleSubscriber) {
                MoreSettingViewPermissionsDispatcher.exportToStorage_checkPermissionWithCheck(MoreSettingView.this, singleSubscriber);
            }
        });
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void exportToStorage_checkPermission(SingleSubscriber singleSubscriber) {
        Realm realm = Realm.getDefaultInstance();
        File zipFile = Utility.createDownloadBackupZipFile();
        if (zipFile.exists()) {
            zipFile.delete();
        }

        String sharedFile;
        try {
            sharedFile = Utility.getSharedPreferenceFile(MoreSettingView.this);
        } catch (IOException e) {
            e.printStackTrace();
            singleSubscriber.onError(new Throwable("error when getting shared file"));
            realm.close();
            return;
        }
        String realmFile = realm.getPath();
        try {
            Utility.zip(new String[]{sharedFile, realmFile}, zipFile);

        } catch (IOException e) {
            e.printStackTrace();
            singleSubscriber.onError(new Throwable("IOException when zip"));
            realm.close();
            return;
        }
        singleSubscriber.onSuccess(new MoreSettingPresenter.MoreSettingResult(MoreSettingPresenter.MoreSettingResult.Type.EXPORT_TO_STORAGE_SUCCESS,null));    }

    @NotNull
    @Override
    public Single<MoreSettingPresenter.MoreSettingResult> uploadToDriveRx(@NotNull final Realm realm, @NotNull final DriveId folderId, @org.jetbrains.annotations.Nullable final GoogleApiClient client) {
        return Single.create(new Single.OnSubscribe<MoreSettingPresenter.MoreSettingResult>() {
            @Override
            public void call(final SingleSubscriber<? super MoreSettingPresenter.MoreSettingResult> singleSubscriber) {
                //Create the file on GDrive
                final DriveFolder folder = folderId.asDriveFolder();
                Drive.DriveApi.newDriveContents(mGoogleApiClient)
                        .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                            @Override
                            public void onResult(DriveApi.DriveContentsResult result) {
                                if (!result.getStatus().isSuccess()) {
                                    singleSubscriber.onError(new Throwable("Error while trying to create new file contents"));
                                    return;
                                }
                                final DriveContents driveContents = result.getDriveContents();

                                String sharedFile;
                                try {
                                    sharedFile = Utility.getSharedPreferenceFile(MoreSettingView.this);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    singleSubscriber.onError(new Throwable("error when getting shared file"));
                                    return;
                                }
                                String realmFile = realm.getPath();
                                final File zipFile = Utility.createTempBackupZipFile(MoreSettingView.this);
                                try {
                                    Utility.zip(new String[]{sharedFile, realmFile}, zipFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    singleSubscriber.onError(new Throwable("IOException when zip"));
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
                                            singleSubscriber.onError(new Throwable("file not found"));
                                            e.printStackTrace();
                                        }

                                        try {
                                            Utility.writeToStream(inputStream, outputStream);
                                        } catch (IOException e) {
                                            singleSubscriber.onError(new Throwable("IoException when write file"));
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
                                                            singleSubscriber.onError(new Throwable("Error while trying to create the file"));

                                                            return;
                                                        }
                                                        singleSubscriber.onSuccess(new MoreSettingPresenter.MoreSettingResult(MoreSettingPresenter.MoreSettingResult.Type.EXPORT_TO_DRIVE_SUCCESS, null));
                                                    }
                                                });
                                    }
                                }.start();
                            }
                        });


            }
        });

    }

    @Override
    public void rebootApp() {
        Utility.rebootApp(getApplicationContext());
    }

    @NotNull
    @Override
    public PublishSubject<String> pickBackupFileFromStorage() {
        MoreSettingViewPermissionsDispatcher.pickBackupFileFromStorage_checkPermissionWithCheck(this);
        pickupStorageFileSuccessSJ = PublishSubject.create();
        return pickupStorageFileSuccessSJ;
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void pickBackupFileFromStorage_checkPermission() {
        Intent filePickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        filePickerIntent.setType("application/*");
        filePickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(filePickerIntent, REQUEST_CODE_PICK_STORAGE_FILE);
    }


    @NotNull
    @Override
    public Single<MoreSettingPresenter.MoreSettingResult> importFromStorageFile(@NotNull final String uir) {
        return Single.create(new Single.OnSubscribe<MoreSettingPresenter.MoreSettingResult>() {
            @Override
            public void call(SingleSubscriber<? super MoreSettingPresenter.MoreSettingResult> singleSubscriber) {
                try {
                    InputStream input = getContentResolver().openInputStream(Uri.parse(uir));
                    File zipFile = new File(getApplicationInfo().dataDir + "/" + Cons.BACKUP_FILE_NAME);
                    OutputStream output = new FileOutputStream(zipFile);
                    try {
                        Utility.writeToStream(input,output);
                        Utility.unzip(zipFile.getAbsolutePath(),getFilesDir().getAbsolutePath(),
                                Environment.getDataDirectory().getAbsolutePath() + "/data/" + getPackageName() + "/" + Cons.SHARED_PREFERENCE_FOLDER_NAME + "/");
                    } catch (Exception e) {
                        e.printStackTrace();
                        singleSubscriber.onError(new Throwable("error when write data"));
                    }
                    zipFile.delete();
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "handleActionImport: file not found " + e);
                    singleSubscriber.onError(new Throwable("file not found"));
                }
                singleSubscriber.onSuccess(new MoreSettingPresenter.MoreSettingResult(MoreSettingPresenter.MoreSettingResult.Type.IMPORT_SUCCESS, null));
            }
        });
    }

    @NotNull
    @Override
    public Single<MoreSettingPresenter.MoreSettingResult> importFromDriveFile(@NotNull final GoogleApiClient client, @NotNull final DriveFile driveFile) {
        return Single.create(new Single.OnSubscribe<MoreSettingPresenter.MoreSettingResult>() {
            @Override
            public void call(final SingleSubscriber<? super MoreSettingPresenter.MoreSettingResult> singleSubscriber) {
                driveFile.open(client, DriveFile.MODE_READ_ONLY, null)
                        .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                            @Override
                            public void onResult(DriveApi.DriveContentsResult result) {
                                if (!result.getStatus().isSuccess()) {
                                    singleSubscriber.onError(new Throwable("Error when opening drive file"));
                                    return;
                                }
                                DriveContents contents = result.getDriveContents();
                                InputStream input = contents.getInputStream();

                                try {
                                    File zipFile = new File(getApplicationInfo().dataDir + "/" + Cons.BACKUP_FILE_NAME);
                                    OutputStream output = new FileOutputStream(zipFile);
                                    try {
                                        Utility.writeToStream(input,output);
                                        Utility.unzip(zipFile.getAbsolutePath(),getFilesDir().getAbsolutePath(),
                                                Environment.getDataDirectory().getAbsolutePath() + "/data/" + getPackageName() + "/" + Cons.SHARED_PREFERENCE_FOLDER_NAME + "/");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        singleSubscriber.onError(new Throwable("error when write data"));
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    singleSubscriber.onError(new Throwable("file not found"));
                                }
                                singleSubscriber.onSuccess(new MoreSettingPresenter.MoreSettingResult(MoreSettingPresenter.MoreSettingResult.Type.IMPORT_SUCCESS, null));
                            }
                        });
            }
        });
    }

    public void downloadFromDrive(final Realm realm, DriveFile file, GoogleApiClient mGoogleApiClient) {
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
                        Utility.rebootApp(getApplicationContext());
                    }
                });
    }
}
