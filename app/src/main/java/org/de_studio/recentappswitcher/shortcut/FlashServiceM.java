package org.de_studio.recentappswitcher.shortcut;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by HaiNguyen on 7/29/16.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FlashServiceM extends Service {
    private static final String TAG = FlashServiceM.class.getSimpleName();

    private CameraCallback mCallback;
    private CameraManager mCameraManager;
    private String mCameraId;

    boolean mWasEnabled;

    @Override
    public void onCreate() {
        super.onCreate();
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        mCallback = new CameraCallback();
        mCameraManager.registerTorchCallback(mCallback, null);

        try {
            mCameraId = getCameraId();
            mCameraManager.setTorchMode(mCameraId, true);
        } catch (Throwable e) {
            Log.e(TAG, "onCreate: " + e);
            stopSelf();
        }
    }

    private String getCameraId() throws CameraAccessException {
        String[] ids = mCameraManager.getCameraIdList();
        for (String id : ids) {
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(id);
            Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            Integer lensFacing = c.get(CameraCharacteristics.LENS_FACING);
            if (flashAvailable != null && flashAvailable
                    && lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_BACK) {
                return id;
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        mCameraManager.unregisterTorchCallback(mCallback);
        if (mCameraId != null) {
            try {
                mCameraManager.setTorchMode(mCameraId, false);
            } catch (Throwable e) {
                Log.e(TAG, "onDestroy: " + e);
            }
        }
        FlashService.FLASH_ON = false;
        super.onDestroy();
    }

    class CameraCallback extends CameraManager.TorchCallback {

        @Override
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            if (enabled) {
                mWasEnabled = true;
                FlashService.FLASH_ON = true;
            } else if (mWasEnabled) {
                FlashService.FLASH_ON = false;
                stopSelf();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}