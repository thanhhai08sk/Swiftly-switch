package org.de_studio.recentappswitcher.shortcut;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

/**
 * Created by HaiNguyen on 7/29/16.
 */
    public class FlashService extends Service implements Runnable, SurfaceHolder.Callback {
        private static final String TAG = FlashService.class.getSimpleName();

        public FlashService() {
        }

        public static boolean FLASH_ON = false;

        private Camera camera;

        private WindowManager wm;
        private LightSurfaceView surface;

        private PowerManager.WakeLock lock;
        private Handler handler;

        @Override
        public void onCreate() {
            super.onCreate();
            surface = new LightSurfaceView(this);
            surface.getHolder().addCallback(this);

            wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(1, 1, -10, -10,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.LEFT;

            wm.addView(surface, params);

            surface.setZOrderOnTop(true);

            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            lock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "PTF");
            lock.acquire();

            handler = new Handler();
            handler.postDelayed(this, 500);
        }

        @Override
        public void run() {
            if (surface.getHolder().isCreating()) {
                handler.postDelayed(this, 500);
            } else {
                surfaceCreated(surface.getHolder());
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                handler.removeCallbacks(this);
            } catch (Throwable e) { }
            try {
                camera = Camera.open();
            } catch (RuntimeException e) {
                // Some unexpected error occurred.
                // Nothing can be done. Flash will not be supported.
            }

            if (camera != null) {
                final Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                try {
                    camera.setParameters(parameters);
                    camera.setPreviewDisplay(surface.getHolder());
                    camera.startPreview();
                } catch (final Throwable e) {
                    Log.e(TAG, "surfaceCreated: " + e);
                }
            }
            FLASH_ON = true;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            releaseCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        private void releaseCamera() {
            if (camera != null) {
                try {
                    camera.stopPreview();
                } catch (Throwable e) {
                    Log.e(TAG, "releaseCamera: " + e);
                }
                try {
                    camera.release();
                } catch (Throwable e) {
                    Log.e(TAG, "releaseCamera: "+ e);
                }
                camera = null;
            }
        }

        @Override
        public void onDestroy() {
            handler.removeCallbacks(this);

            releaseCamera();

            lock.release();
            try {
                wm.removeView(surface);
            } catch (Throwable e) {
                Log.e(TAG, "onDestroy: " + e);
            }
            FLASH_ON = false;
            super.onDestroy();
        }

        private static class LightSurfaceView extends SurfaceView {

            public LightSurfaceView(Context paramContext) {
                super(paramContext);
                getHolder().setType(3);
            }
        }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

