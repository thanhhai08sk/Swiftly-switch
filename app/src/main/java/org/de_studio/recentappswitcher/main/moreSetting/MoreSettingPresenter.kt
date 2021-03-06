package org.de_studio.recentappswitcher.main.moreSetting

import android.app.Activity
import android.content.SharedPreferences
import android.util.Log
import android.util.Pair
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
            observable?.observeOn(AndroidSchedulers.mainThread())
                    ?.flatMap({ view.choosePlaceToBackup().toObservable()})
                    ?.observeOn(Schedulers.io())
                    ?.publish({shared -> Observable.merge(
                            shared.filter { (type) -> type == MoreSettingResult.Type.CHOOSE_PLACE_GOOGLE_DRIVE }
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .flatMap { view.connectClientRX().toObservable()
                                            .flatMap { result -> view.openFolderPickerRx(result.googleApiClient)}
                                            .flatMap { pair -> view.uploadToDriveRx(realm,pair.second,pair.first).toObservable() }
                                            .onErrorReturn { MoreSettingResult(MoreSettingResult.Type.EXPORT_FAIL) }
                                            .startWith(MoreSettingResult(MoreSettingResult.Type.EXPORT_START))
                                    },
                            shared.filter { (type) -> type == MoreSettingResult.Type.CHOOSE_PLACE_STORAGE }
                                    .flatMap({view.exportToStorage().toObservable()
                                                .startWith(MoreSettingResult(MoreSettingResult.Type.EXPORT_START))
                                                .onErrorReturn {  MoreSettingResult(MoreSettingResult.Type.EXPORT_FAIL) }})

                    ) })
        }

        val driveImportTrans: Observable.Transformer<ImportFromDriveAction, MoreSettingResult> = Observable.Transformer { observable ->
            observable.flatMap({
                view.connectClientRX().toObservable()
                        .flatMap({ result: MoreSettingResult ->
                            view.pickDriveFile(result.googleApiClient)
                                    .flatMap({ pair: Pair<GoogleApiClient, DriveFile> ->
                                        view.importFromDriveFile(pair.first, pair.second).toObservable()
                                                .startWith(MoreSettingResult(MoreSettingResult.Type.EXPORT_START))
                                    }).startWith(MoreSettingResult(MoreSettingResult.Type.CONNECT_CLIENT_SUCCESS))
                        })
                        .startWith(MoreSettingResult(MoreSettingResult.Type.CONNECT_CLIENT_START))
            })
        }

        val storageImportTrans: Observable.Transformer<ImportFromStorageAction, MoreSettingResult> = Observable.Transformer { observable ->
            observable.flatMap({view.pickBackupFileFromStorage()})
                    .flatMap({uri ->
                        view.importFromStorageFile(uri).toObservable()
                                .startWith(MoreSettingResult(MoreSettingResult.Type.EXPORT_START))})

        }



        val actionTrans: Observable.Transformer<MoreSettingAction, MoreSettingResult> = Observable.Transformer {observable ->
            observable?.publish({shared ->
                Observable.merge(
                        shared.ofType(ExportAction::class.java).compose(exportTrans),
                        shared.ofType(ImportFromDriveAction::class.java).compose(driveImportTrans),
                        shared.ofType(ImportFromStorageAction::class.java).compose(storageImportTrans)
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
                                MoreSettingResult.Type.EXPORT_TO_DRIVE_SUCCESS -> return@Func2 uiModel.exportToDriveSuccess()
                                MoreSettingResult.Type.EXPORT_TO_STORAGE_SUCCESS -> return@Func2 uiModel.exportToStorageSuccess()
                                MoreSettingResult.Type.IMPORT_START -> return@Func2 uiModel.startImport()
                                MoreSettingResult.Type.IMPORT_SUCCESS -> return@Func2 uiModel.reboot()
                                MoreSettingResult.Type.CONNECT_CLIENT_START -> return@Func2 uiModel.connectClient()
                                MoreSettingResult.Type.CONNECT_CLIENT_SUCCESS -> return@Func2 uiModel.connectClientDone()
                                else -> return@Func2 uiModel
                            }
                        })
                        .subscribe { model: MoreSettingUIModel? ->
                            if (model != null) {
                                if (model.exporting) view.showExportingDialog()
                                if (model.exportDriveSuccess) {
                                    view.hideExportingDialog()
                                    view.showBackupGoogleDriveOk()
                                }

                                if (model.exportStorageSuccess) {
                                    view.hideExportingDialog()
                                    view.showBackupStorageOk()
                                }

                                if (model.importing) view.showImportingDialog()
                                if (model.rebootApp) view.rebootApp()
                                if (model.connectingClient) {
                                    view.showConnectingDialog()
                                }else view.hideConnectingDialog()
                            }
                        })

        addSubscription(
                view.onExport().subscribe { actionSJ.onNext(ExportAction()) }
        )

        addSubscription(
                view.onImport().flatMap { view.choosePlaceToImport().toObservable() }
                        .subscribe { result ->
                            when (result.type) {
                                MoreSettingResult.Type.CHOOSE_PLACE_GOOGLE_DRIVE -> actionSJ.onNext(ImportFromDriveAction())
                                MoreSettingResult.Type.CHOOSE_PLACE_STORAGE -> actionSJ.onNext(ImportFromStorageAction())
                                else -> throw IllegalArgumentException("can not accept this result " + result.toString())
                            }
                        }
        )




        sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
        addSubscription(
                longPressDelaySJ.subscribe { integer -> onSetLongPressDelay(integer!!) }
        )

        addSubscription(
                iconSizeSJ.subscribe { integer -> onSetIconSize(integer.toFloat() / 100f) }
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




//        addSubscription(
//                view.onPickFileToRestoreSuccess().subscribe { driveFile ->
//                    view.showDownloadingDialog()
////                    view.downloadFromDrive(realm, driveFile)
//                }
//        )


        addSubscription(
                view.onSomethingWrong().subscribe { integer ->
                    when (integer) {
                        MoreSettingView.REQUEST_BACKUP -> view.showErrorDialog()
                    }
                }
        )

        addSubscription(
                view.onBackupSuccessful().subscribe { view.showBackupGoogleDriveOk() }
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

    fun onSetBackgroundColor(color: Int) {
        sharedPreferences!!.edit().putInt(Cons.BACKGROUND_COLOR_KEY, color).commit()
        view.resetService()
    }

    fun onSetFolderBackgroundColor(color: Int) {
        sharedPreferences!!.edit().putInt(Cons.FOLDER_BACKGROUND_COLOR_KEY, color).commit()
        view.resetService()
    }

    fun onAnimation() {
        val currentValue = sharedPreferences!!.getBoolean(Cons.USE_ANIMATION_KEY, true)
        sharedPreferences!!.edit().putBoolean(Cons.USE_ANIMATION_KEY, !currentValue).commit()
        view.resetService()
    }

    fun onTransition() {
        val currentValue = sharedPreferences!!.getBoolean(Cons.USE_TRANSITION_KEY, false)
        sharedPreferences!!.edit().putBoolean(Cons.USE_TRANSITION_KEY, !currentValue).commit()
        view.resetService()
//        visible
    }
    fun onAnimationDuration() {
        view.animationDurationDialog(animationDurationSJ)
    }

    fun onSetAnimationDuration(duration: Int) {
        sharedPreferences!!.edit().putInt(Cons.ANIMATION_TIME_KEY, duration).commit()
        view.resetService()
    }

    fun setScreenshotsFolder(directory: String) {
        sharedPreferences!!.edit().putString("screenshotsFolder", directory).apply()
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
        fun animationDurationDialog(subject: PublishSubject<Int>)

        fun vibrationDurationDialog(subject: PublishSubject<Int>)

        fun openFolderPickerRx(client: GoogleApiClient?): PublishSubject<Pair<GoogleApiClient, DriveId>>

        fun openFilePicker()
        fun pickDriveFile(client: GoogleApiClient?): PublishSubject<Pair<GoogleApiClient,DriveFile>>

        fun connectClient()

        fun connectClientRX(): Single<MoreSettingResult>

        fun disconnectClient()

        fun onFinishReadingGuide(): PublishSubject<Void>

        fun onGoogleApiClientConnected(): PublishSubject<Void>


        fun onSomethingWrong(): PublishSubject<Int>

        fun onBackupSuccessful(): PublishSubject<Void>


        fun showBackupGuideDialog()

        fun showImportGuideDialog()

        fun showConnectingDialog()

        fun hideConnectingDialog()

        fun showErrorDialog()

        fun showBackupGoogleDriveOk()

        fun showBackupStorageOk()

        fun showExportingDialog()

        fun showImportingDialog()

        fun hideExportingDialog()

//        fun uploadToDrive(realm: Realm, mFolderDriveId: DriveId)
        fun uploadToDriveRx(realm: Realm, folderId: DriveId, client: GoogleApiClient?): Single<MoreSettingResult>

//        fun downloadFromDrive(realm: Realm, file: DriveFile)
        val activityForContext: Activity
        fun choosePlaceToBackup(): Single<MoreSettingResult>
        fun choosePlaceToImport(): Single<MoreSettingResult>
        fun exportToStorage(): Single<MoreSettingPresenter.MoreSettingResult>
        fun  importFromDriveFile(client: GoogleApiClient, driveFile: DriveFile): Single<MoreSettingResult>
        fun  pickBackupFileFromStorage(): PublishSubject<String>
        fun importFromStorageFile(uir: String): Single<MoreSettingResult>
        fun rebootApp()
        fun  onImport(): PublishSubject<Void>
        fun onExport(): PublishSubject<Void>


    }

    companion object {
        private val TAG = MoreSettingPresenter::class.java.simpleName
    }

    data class MoreSettingUIModel(var exporting: Boolean = false,
                                  var importing: Boolean = false,
                                  var exportDriveSuccess: Boolean = false,
                                  var exportStorageSuccess: Boolean = false,
                                  var rebootApp: Boolean = false,
                                  var connectingClient: Boolean = false
    ){
        fun reset() {
            exporting = false
            importing = false
            exportDriveSuccess = false
            exportStorageSuccess = false
            rebootApp = false
        }

        fun startExport() : MoreSettingUIModel {
            exporting = true
            return this
        }

        fun exportToDriveSuccess() : MoreSettingUIModel {
            exportDriveSuccess = true
            return this
        }

        fun exportToStorageSuccess(): MoreSettingUIModel {
            exportStorageSuccess = true
            return this
        }

        fun startImport(): MoreSettingUIModel {
            importing = true
            return this
        }

        fun reboot(): MoreSettingUIModel {
            rebootApp = true
            return this
        }

        fun connectClient(): MoreSettingUIModel{
            connectingClient = true
            return this
        }

        fun connectClientDone(): MoreSettingUIModel {
            connectingClient = false
            return this
        }

    }


    data class MoreSettingResult(val type: Type, var googleApiClient: GoogleApiClient? = null) {
        enum class Type {
            CHOOSE_PLACE_STORAGE,
            CHOOSE_PLACE_GOOGLE_DRIVE,
            CONNECT_CLIENT_SUCCESS,
            CONNECT_CLIENT_FAIL,
            CONNECT_CLIENT_START,
            EXPORT_START,
            EXPORT_TO_DRIVE_SUCCESS,
            EXPORT_TO_STORAGE_SUCCESS,
            EXPORT_FAIL,
            IMPORT_START,
            IMPORT_SUCCESS,
            IMPORT_FAIL,
            WRITE_TO_DRIVE_SUCCESS,
            WRITE_TO_DRIVE_FAIL,
        }
    }

    open inner class MoreSettingAction

    inner class ExportAction : MoreSettingAction()
    open inner class ImportAction: MoreSettingAction()
    inner class ImportFromStorageAction: ImportAction()
    inner class ImportFromDriveAction : ImportAction()





}
