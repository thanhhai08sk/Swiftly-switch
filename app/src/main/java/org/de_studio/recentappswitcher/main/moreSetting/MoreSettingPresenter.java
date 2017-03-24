package org.de_studio.recentappswitcher.main.moreSetting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.base.BasePresenter;
import org.de_studio.recentappswitcher.base.PresenterView;
import org.de_studio.recentappswitcher.model.Slot;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 1/14/17.
 */

public class MoreSettingPresenter extends BasePresenter<MoreSettingPresenter.View, BaseModel> implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = MoreSettingPresenter.class.getSimpleName();
    SharedPreferences sharedPreferences;
    PublishSubject<Integer> longPressDelaySJ = PublishSubject.create();
    PublishSubject<Integer> iconSizeSJ = PublishSubject.create();
    PublishSubject<Integer> animationDurationSJ = PublishSubject.create();
    PublishSubject<Integer> vibrationDurationSJ = PublishSubject.create();
    PublishSubject<Integer> openFolderDelaySJ = PublishSubject.create();
    Realm realm = Realm.getDefaultInstance();


    public MoreSettingPresenter(BaseModel model, SharedPreferences sharedPreferences) {
        super(model);
        this.sharedPreferences = sharedPreferences;

    }

    @Override
    public void onViewAttach(final View view) {
        super.onViewAttach(view);
        view.updateViews();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
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
                openFolderDelaySJ.subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        sharedPreferences.edit().putInt(Cons.OPEN_FOLDER_DELAY_KEY, integer).commit();
                        view.resetService();
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


        addSubscription(
                view.onBackupOrRestoreSJ().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        switch (integer) {
                            case MoreSettingView.REQUEST_BACKUP:
                                view.showBackupGuideDialog();
                                break;
                            case MoreSettingView.REQUEST_RESTORE:
                                view.showImportGuideDialog();
                                break;
                        }
                    }
                })
        );

        addSubscription(
                view.onFinishReadingGuide().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.connectClient();
                        view.showConnectingDialog();
                    }
                })
        );

        addSubscription(
                Observable.combineLatest(view.onGoogleApiClientConnected(), view.onBackupOrRestoreSJ(), new Func2<Void, Integer, Integer>() {
                    @Override
                    public Integer call(Void aVoid, Integer integer) {
                        return integer;
                    }
                }).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        view.hideConnectingDialog();
                        switch (integer) {
                            case MoreSettingView.REQUEST_BACKUP:
                                view.openFolderPicker();
                                break;
                            case MoreSettingView.REQUEST_RESTORE:
                                view.openFilePicker();
                                break;
                        }
                    }
                })
        );

        addSubscription(
                view.onPickFolderSuccess().subscribe(new Action1<DriveId>() {
                    @Override
                    public void call(DriveId driveId) {
//                        view.showUploadingDialog();
                        view.uploadToDrive(realm, driveId);
                    }
                })
        );

        addSubscription(
                view.onPickFileToRestoreSuccess().subscribe(new Action1<DriveFile>() {
                    @Override
                    public void call(DriveFile driveFile) {
                        view.showDownloadingDialog();
                        view.downloadFromDrive(realm, driveFile);
                    }
                })
        );


        addSubscription(
                view.onSomethingWrong().subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        switch (integer) {
                            case MoreSettingView.REQUEST_BACKUP:
                                view.showErrorDialog();
                        }
                    }
                })
        );

        addSubscription(
                view.onBackupSuccessful().subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        view.showSuccessDialog();
                    }
                })
        );


    }

    public void onDisableInFullscreen() {
        boolean currentSetting = sharedPreferences.getBoolean(Cons.DISABLE_IN_FULLSCREEN_KEY, false);
        sharedPreferences.edit().putBoolean(Cons.DISABLE_IN_FULLSCREEN_KEY, !currentSetting).commit();
        view.resetService();
    }
    public void onDisableClock() {
        boolean currentSetting = sharedPreferences.getBoolean(Cons.DISABLE_CLOCK_KEY, false);
        sharedPreferences.edit().putBoolean(Cons.DISABLE_CLOCK_KEY, !currentSetting).commit();
        view.resetService();
    }

    public void onDisableIndicator() {
        boolean currentSetting = sharedPreferences.getBoolean(Cons.DISABLE_INDICATOR_KEY, false);
        sharedPreferences.edit().putBoolean(Cons.DISABLE_INDICATOR_KEY, !currentSetting).commit();
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

    public void onRingerModeAction() {
        view.ringerModeActionDialog();
    }

    public void setDefaultContactAction(int action) {
        sharedPreferences.edit().putInt(Cons.CONTACT_ACTION_KEY, action).commit();
    }

    public void setRingerModeAction(int action) {
        sharedPreferences.edit().putInt(Cons.RINGER_MODE_ACTION_KEY, action).commit();
    }

    public void onLongPressDelay() {
        view.longPressDelayDialog(longPressDelaySJ);
    }

    public void onSetLongPressDelay(int time) {
        sharedPreferences.edit().putInt(Cons.LONG_PRESS_DELAY_KEY, time).commit();
        view.resetService();
    }

    public void onIconPack() {
        view.chooseIconPackDialog();
    }

    public void resetFolderThumbnail() {
        String iconPackPacka = sharedPreferences.getString(Cons.ICON_PACK_PACKAGE_NAME_KEY, Cons.ICON_PACK_NONE);
        IconPackManager.IconPack iconPack = null;
        if (!iconPackPacka.equals(Cons.ICON_PACK_NONE)) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(view.getActivityForContext());
            iconPack = iconPackManager.getInstance(iconPackPacka);
            if (iconPack != null) {
                iconPack.load();
            }
        }
        RealmResults<Slot> folders = realm.where(Slot.class).equalTo(Cons.TYPE, Slot.TYPE_FOLDER).findAll();
        for (Slot folder : folders) {
            if (!folder.useIconSetByUser) {
                Utility.createAndSaveFolderThumbnail(folder, realm, view.getActivityForContext(), iconPack);
            }
        }
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

    public void onOpenFolderDelay() {
        boolean currentValue = sharedPreferences.getBoolean(Cons.OPEN_FOLDER_DELAY_KEY, true);
        sharedPreferences.edit().putBoolean(Cons.OPEN_FOLDER_DELAY_KEY, !currentValue).commit();
        view.resetService();
    }

    public void onSetVibrationDuration(int duration) {
        sharedPreferences.edit().putInt(Cons.VIBRATION_DURATION_KEY, duration).commit();
        view.resetService();
    }


    public void onResetSettings() {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.e(TAG, "onSharedPreferenceChanged: key = " + key);
        view.updateViews();
    }

    @Override
    public void onViewDetach() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        sharedPreferences = null;
        realm.close();
        super.onViewDetach();
    }

    public interface View extends PresenterView, GoogleApiClient.ConnectionCallbacks {
        void resetService();

        void updateViews();

        boolean isAssistApp();

        void assistAppDialog();

        void contactActionDialog();

        void ringerModeActionDialog();

        void longPressDelayDialog(PublishSubject<Integer> subject);

        void chooseIconPackDialog();

        void iconSizeDialog(PublishSubject<Integer> subject);

        void backgroundColorDialog();

        void animationDurationDialog(PublishSubject<Integer> subject);

        void vibrationDurationDialog(PublishSubject<Integer> subject);

        void openFolderPicker();

        void openFilePicker();

        void connectClient();

        void disconnectClient();

        PublishSubject<Void> onFinishReadingGuide();

        PublishSubject<Void> onGoogleApiClientConnected();

        PublishSubject<Integer> onBackupOrRestoreSJ();

        PublishSubject<DriveId> onPickFolderSuccess();

        PublishSubject<DriveFile> onPickFileToRestoreSuccess();

        PublishSubject<Integer> onSomethingWrong();

        PublishSubject<Void> onBackupSuccessful();


        void showBackupGuideDialog();

        void showImportGuideDialog();

        void showConnectingDialog();

        void hideConnectingDialog();

        void showDownloadingDialog();

        void showErrorDialog();

        void showSuccessDialog();


        void hideDownloadingDialog();

        void showUploadingDialog();

        void hideUploadingDialog();

        void uploadToDrive(final Realm realm, DriveId mFolderDriveId);

        void downloadFromDrive(final Realm realm, DriveFile file);

        Activity getActivityForContext();

    }
}
