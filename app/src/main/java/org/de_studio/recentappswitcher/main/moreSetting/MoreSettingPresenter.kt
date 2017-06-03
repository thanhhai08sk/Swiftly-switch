package org.de_studio.recentappswitcher.main.moreSetting

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.DriveId
import io.realm.Realm
import org.de_studio.recentappswitcher.Cons
import org.de_studio.recentappswitcher.IconPackManager
import org.de_studio.recentappswitcher.Utility
import org.de_studio.recentappswitcher.base.BaseModel
import org.de_studio.recentappswitcher.base.BasePresenter
import org.de_studio.recentappswitcher.base.PresenterView
import org.de_studio.recentappswitcher.model.Slot
import rx.Observable
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func2
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject

/**
 * Created by HaiNguyen on 1/14/17.
 */

class MoreSettingPresenter(model: BaseModel, internal var sharedPreferences: SharedPreferences?) : BasePresenter<MoreSettingPresenter.View, BaseModel>(model), SharedPreferences.OnSharedPreferenceChangeListener {
    internal var longPressDelaySJ = PublishSubject.create<Int>()
    internal var iconSizeSJ = PublishSubject.create<Int>()
    internal var animationDurationSJ = PublishSubject.create<Int>()
    internal var vibrationDurationSJ = PublishSubject.create<Int>()
    internal var openFolderDelaySJ = PublishSubject.create<Int>()
    internal var realm = Realm.getDefaultInstance()

    internal val actionSJ = PublishSubject.create<MoreSettingAction>()


    override fun onViewAttach(view: View) {
        super.onViewAttach(view)
        view.updateViews()


        val exportTrans: Observable.Transformer<ExportAction, MoreSettingResult> = Observable.Transformer { observable: Observable<ExportAction>? ->
            observable?.flatMap({i -> view.choosePlaceToBackup().toObservable()})
                    ?.publish({shared -> Observable.merge(
                            shared.filter { t -> t.type == MoreSettingResult.Type.CHOOSE_PLACE_GOOGLE_DRIVE }
                                    .flatMap { view.connectClientRX().toObservable()
                                            .onErrorReturn { t -> MoreSettingResult(MoreSettingResult.Type.EXPORT_FAIL) }
                                            .startWith(MoreSettingResult(MoreSettingResult.Type.EXPORT_START))
                                            .flatMap { t -> view.openFolderPickerRx()}
                                            .flatMap { folderDriveId -> view.uploadToDriveRx(realm,folderDriveId).toObservable() } },
                            shared.filter { t -> t.type == MoreSettingResult.Type.CHOOSE_PLACE_STORAGE }
                                    .flatMap({t ->
                                        view.exportToStorage().toObservable()
                                                .startWith(MoreSettingResult(MoreSettingResult.Type.EXPORT_START))})

                    ) })
        }


        val actionTrans: Observable.Transformer<MoreSettingAction, MoreSettingResult> = Observable.Transformer { observable: Observable<MoreSettingAction>? ->
            observable?.publish({shared -> Observable.merge(
                    shared.ofType(ExportAction::class.java).compose(exportTrans),
                    shared.ofType(ImportAction::class.java).compose { null }
            ) })
        }



        addSubscription(
                actionSJ.observeOn(Schedulers.io())
                        .compose(actionTrans)
                        .observeOn(AndroidSchedulers.mainThread())
                        .scan (MoreSettingUIModel(), fun2@ Func2 { uiModel, result ->
                            uiModel.reset()
                            when (result.type) {
                                MoreSettingResult.Type.EXPORT_START -> return@Func2 uiModel.startExport()
                                MoreSettingResult.Type.EXPORT_SUCCESS -> return@Func2 uiModel.finishExport()
                                else -> return@Func2 uiModel
                            }
                        })
                        .subscribe { model: MoreSettingUIModel? ->
                            if (model != null) {
                                if (model.exporting) view.showUploadingDialog()
                                if (model.exportDone) view.hideUploadingDialog()
                            }
                        })


        addSubscription(
                view.onBackupOrRestoreSJ().subscribe { integer ->
                    when (integer) {
                        MoreSettingView.REQUEST_BACKUP -> actionSJ.onNext(ExportAction())
                    }
                }
        )



        sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
        addSubscription(
                longPressDelaySJ.subscribe { integer -> onSetLongPressDelay(integer!!) }
        )

        addSubscription(
                iconSizeSJ.subscribe { integer -> onSetIconSize(integer as Float / 100f) }
        )

        addSubscription(
                animationDurationSJ.subscribe { integer -> onSetAnimationDuration(integer!!) }
        )

        addSubscription(
                openFolderDelaySJ.subscribe { integer ->
                    sharedPreferences!!.edit().putInt(Cons.OPEN_FOLDER_DELAY_KEY, integer!!).commit()
                    view.resetService()
                }
        )

        addSubscription(
                vibrationDurationSJ.subscribe { integer -> onSetVibrationDuration(integer!!) }
        )




//        addSubscription(
//                view.onBackupOrRestoreSJ().subscribe { integer ->
//                    when (integer) {
//                        MoreSettingView.REQUEST_BACKUP -> view.showBackupGuideDialog()
//                        MoreSettingView.REQUEST_RESTORE -> view.showImportGuideDialog()
//                    }
//                }
//        )

//        addSubscription(
//                view.onFinishReadingGuide().subscribe {
//                    view.connectClient()
//                    view.showConnectingDialog()
//                }
//        )

//        addSubscription(
//                Observable.combineLatest(view.onGoogleApiClientConnected(), view.onBackupOrRestoreSJ()) { aVoid, integer -> integer }.subscribe { integer ->
//                    view.hideConnectingDialog()
//                    when (integer) {
//                        MoreSettingView.REQUEST_BACKUP -> view.openFolderPicker()
//                        MoreSettingView.REQUEST_RESTORE -> view.openFilePicker()
//                    }
//                }
//        )



        addSubscription(
                view.onPickFolderSuccess().subscribe { driveId ->
                    //                        view.showUploadingDialog();
                    view.uploadToDrive(realm, driveId)
                }
        )

        addSubscription(
                view.onPickFileToRestoreSuccess().subscribe { driveFile ->
                    view.showDownloadingDialog()
                    view.downloadFromDrive(realm, driveFile)
                }
        )


        addSubscription(
                view.onSomethingWrong().subscribe { integer ->
                    when (integer) {
                        MoreSettingView.REQUEST_BACKUP -> view.showErrorDialog()
                    }
                }
        )

        addSubscription(
                view.onBackupSuccessful().subscribe { view.showSuccessDialog() }
        )


    }

    fun onDisableInFullscreen() {
        val currentSetting = sharedPreferences!!.getBoolean(Cons.DISABLE_IN_FULLSCREEN_KEY, false)
        sharedPreferences!!.edit().putBoolean(Cons.DISABLE_IN_FULLSCREEN_KEY, !currentSetting).commit()
        view.resetService()
    }

    fun onDisableClock() {
        val currentSetting = sharedPreferences!!.getBoolean(Cons.DISABLE_CLOCK_KEY, false)
        sharedPreferences!!.edit().putBoolean(Cons.DISABLE_CLOCK_KEY, !currentSetting).commit()
        view.resetService()
    }

    fun onDisableIndicator() {
        val currentSetting = sharedPreferences!!.getBoolean(Cons.DISABLE_INDICATOR_KEY, false)
        sharedPreferences!!.edit().putBoolean(Cons.DISABLE_INDICATOR_KEY, !currentSetting).commit()
        view.resetService()
    }

    fun onAvoidKeyboard() {
        val currentSetting = sharedPreferences!!.getBoolean(Cons.AVOID_KEYBOARD_KEY, true)
        sharedPreferences!!.edit().putBoolean(Cons.AVOID_KEYBOARD_KEY, !currentSetting).commit()
        view.resetService()
    }

    fun onDisableInLandscape() {
        val currentSetting = sharedPreferences!!.getBoolean(Cons.IS_DISABLE_IN_LANDSCAPE_KEY, false)
        sharedPreferences!!.edit().putBoolean(Cons.IS_DISABLE_IN_LANDSCAPE_KEY, !currentSetting).commit()
        view.resetService()
    }

    fun onUseHomeButton() {
        view.assistAppDialog()
    }

    fun onDefaultActionForContacts() {
        view.contactActionDialog()
    }

    fun onRingerModeAction() {
        view.ringerModeActionDialog()
    }

    fun setDefaultContactAction(action: Int) {
        sharedPreferences!!.edit().putInt(Cons.CONTACT_ACTION_KEY, action).commit()
    }

    fun setRingerModeAction(action: Int) {
        sharedPreferences!!.edit().putInt(Cons.RINGER_MODE_ACTION_KEY, action).commit()
    }

    fun onLongPressDelay() {
        view.longPressDelayDialog(longPressDelaySJ)
    }

    fun onSetLongPressDelay(time: Int) {
        sharedPreferences!!.edit().putInt(Cons.LONG_PRESS_DELAY_KEY, time).commit()
        view.resetService()
    }

    fun onIconPack() {
        view.chooseIconPackDialog()
    }

    fun resetFolderThumbnail() {
        val iconPackPacka = sharedPreferences!!.getString(Cons.ICON_PACK_PACKAGE_NAME_KEY, Cons.ICON_PACK_NONE)
        var iconPack: IconPackManager.IconPack? = null
        if (iconPackPacka != Cons.ICON_PACK_NONE) {
            val iconPackManager = IconPackManager()
            iconPackManager.setContext(view.activityForContext)
            iconPack = iconPackManager.getInstance(iconPackPacka)
            if (iconPack != null) {
                iconPack.load()
            }
        }
        val folders = realm.where(Slot::class.java).equalTo(Cons.TYPE, Slot.TYPE_FOLDER).findAll()
        for (folder in folders) {
            if (!folder.useIconSetByUser) {
                Utility.createAndSaveFolderThumbnail(folder, realm, view.activityForContext, iconPack)
            }
        }
    }

    fun onSetIconPack(iconPackPackage: String) {
        sharedPreferences!!.edit().putString(Cons.ICON_PACK_PACKAGE_NAME_KEY, iconPackPackage).commit()
        view.resetService()
    }

    fun onIconSize() {
        view.iconSizeDialog(iconSizeSJ)
    }

    fun onSetIconSize(size: Float) {
        sharedPreferences!!.edit().putFloat(Cons.ICON_SCALE_KEY, size).commit()
        view.resetService()
    }

    fun onBackgroundColor() {
        view.backgroundColorDialog()
    }

    fun onSetBackgroundColor(color: Int) {
        sharedPreferences!!.edit().putInt(Cons.BACKGROUND_COLOR_KEY, color).commit()
        view.resetService()
    }

    fun onAnimation() {
        val currentValue = sharedPreferences!!.getBoolean(Cons.USE_ANIMATION_KEY, true)
        sharedPreferences!!.edit().putBoolean(Cons.USE_ANIMATION_KEY, !currentValue).commit()
        view.resetService()
    }

    fun onAnimationDuration() {
        view.animationDurationDialog(animationDurationSJ)
    }

    fun onSetAnimationDuration(duration: Int) {
        sharedPreferences!!.edit().putInt(Cons.ANIMATION_TIME_KEY, duration).commit()
        view.resetService()
    }

    fun onHapticOnTrigger() {
        val currentValue = sharedPreferences!!.getBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, true)
        sharedPreferences!!.edit().putBoolean(Cons.DISABLE_HAPTIC_FEEDBACK_KEY, !currentValue).commit()
        view.resetService()
    }

    fun onHapticOnIcon() {
        val currentValue = sharedPreferences!!.getBoolean(Cons.HAPTIC_ON_ICON_KEY, false)
        sharedPreferences!!.edit().putBoolean(Cons.HAPTIC_ON_ICON_KEY, !currentValue).commit()
        view.resetService()
    }

    fun onVibratioDuration() {
        view.vibrationDurationDialog(vibrationDurationSJ)
    }

    fun onOpenFolderDelay() {
        val currentValue = sharedPreferences!!.getBoolean(Cons.OPEN_FOLDER_DELAY_KEY, true)
        sharedPreferences!!.edit().putBoolean(Cons.OPEN_FOLDER_DELAY_KEY, !currentValue).commit()
        view.resetService()
    }

    fun onSetVibrationDuration(duration: Int) {
        sharedPreferences!!.edit().putInt(Cons.VIBRATION_DURATION_KEY, duration).commit()
        view.resetService()
    }


    fun onResetSettings() {

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        Log.e(TAG, "onSharedPreferenceChanged: key = " + key)
        view.updateViews()
    }

    override fun onViewDetach() {
        sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
        sharedPreferences = null
        realm.close()
        super.onViewDetach()
    }

    interface View : PresenterView, GoogleApiClient.ConnectionCallbacks {
        fun resetService()

        fun updateViews()

        val isAssistApp: Boolean

        fun assistAppDialog()

        fun contactActionDialog()

        fun ringerModeActionDialog()

        fun longPressDelayDialog(subject: PublishSubject<Int>)

        fun chooseIconPackDialog()

        fun iconSizeDialog(subject: PublishSubject<Int>)

        fun backgroundColorDialog()

        fun animationDurationDialog(subject: PublishSubject<Int>)

        fun vibrationDurationDialog(subject: PublishSubject<Int>)

        fun openFolderPicker()
        fun openFolderPickerRx(): PublishSubject<DriveId>

        fun openFilePicker()

        fun connectClient()

        fun connectClientRX(): Single<MoreSettingResult>

        fun disconnectClient()

        fun onFinishReadingGuide(): PublishSubject<Void>

        fun onGoogleApiClientConnected(): PublishSubject<Void>

        fun onBackupOrRestoreSJ(): PublishSubject<Int>

        fun onPickFolderSuccess(): PublishSubject<DriveId>

        fun onPickFileToRestoreSuccess(): PublishSubject<DriveFile>

        fun onSomethingWrong(): PublishSubject<Int>

        fun onBackupSuccessful(): PublishSubject<Void>


        fun showBackupGuideDialog()

        fun showImportGuideDialog()

        fun showConnectingDialog()

        fun hideConnectingDialog()

        fun showDownloadingDialog()

        fun showErrorDialog()

        fun showSuccessDialog()


        fun hideDownloadingDialog()

        fun showUploadingDialog()

        fun hideUploadingDialog()

        fun uploadToDrive(realm: Realm, mFolderDriveId: DriveId)
        fun uploadToDriveRx(realm: Realm, folderId: DriveId): Single<MoreSettingResult>

        fun downloadFromDrive(realm: Realm, file: DriveFile)

        val activityForContext: Activity

        fun choosePlaceToBackup(): Single<MoreSettingResult>
        fun  exportToStorage(): Single<MoreSettingPresenter.MoreSettingResult>

    }

    companion object {
        private val TAG = MoreSettingPresenter::class.java.simpleName
    }

    data class MoreSettingUIModel(var exporting: Boolean = false,
                                  var importing: Boolean = false,
                                  var exportDone: Boolean = false
    ){
        fun reset() {
            exporting = false
            importing = false
            exportDone = false
        }

        fun startExport() : MoreSettingUIModel {
            exporting = true
            return this
        }

        fun finishExport() : MoreSettingUIModel {
            exportDone = true
            return this
        }

        fun startImport(): MoreSettingUIModel {
            importing = true
            return this
        }

    }


    data class MoreSettingResult(val type: Type) {
        enum class Type {
            CHOOSE_PLACE_STORAGE,
            CHOOSE_PLACE_GOOGLE_DRIVE,
            CONNECT_CLIENT_SUCCESS,
            CONNECT_CLIENT_FAIL,
            EXPORT_START,
            EXPORT_SUCCESS,
            EXPORT_FAIL,
            IMPORT_START,
            IMPORT_SUCCESS,
            IMPORT_FAIL,
            WRITE_TO_DRIVE_SUCCESS,
            WRITE_TO_DRIVE_FAIL
        }
    }

    open inner class MoreSettingAction

    inner class ExportAction : MoreSettingAction()
    inner class ImportAction: MoreSettingAction()






}
