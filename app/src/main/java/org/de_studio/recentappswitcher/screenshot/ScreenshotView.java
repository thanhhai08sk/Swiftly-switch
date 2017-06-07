package org.de_studio.recentappswitcher.screenshot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;

import org.de_studio.recentappswitcher.Cons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.de_studio.recentappswitcher.MyApplication.getContext;


public class ScreenshotView extends Activity {

    private static final String TAG = ScreenshotView.class.getName();
    private static final int REQUEST_CODE_CAPTURE = 100;
    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection sMediaProjection;

    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private boolean captured;
    private MediaProjectionStopCallback mediaProjectionStopCallback = new MediaProjectionStopCallback();

//    private OrientationChangeCallback mOrientationChangeCallback;

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            if (captured) {
                finishScreenshot();
                return;
            }


            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;
            File file = new File(STORE_DIRECTORY + "/myscreen_" + System.currentTimeMillis() + ".png");

            try {
                image = mImageReader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    // create bitmap
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                    Rect rect = image.getCropRect();
                    bitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());

                    fos = new FileOutputStream(file);
                    bitmap.compress(CompressFormat.JPEG, 100, fos);
                    bitmap.recycle();




                    IMAGES_PRODUCED++;

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }
            captured = true;
            Intent screenshotOkBroadcast = new Intent(Cons.ACTION_SCREENSHOT_OK);
            screenshotOkBroadcast.putExtra("uri", Uri.fromFile(file));
            sendBroadcast(screenshotOkBroadcast);
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
            finishScreenshot();
        }
    }

    private void finishScreenshot() {
        Log.e(TAG, "onImageAvailable: finish screenshot");
        stopProjection();
//            finishAffinity();
        finishAndRemoveTask();
    }

    private class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");
            if (mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mVirtualDisplay != null) mVirtualDisplay.release();
                        if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
//                        if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                        sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                    }
                });

            }
        }
    }

    /****************************************** Activity Lifecycle methods ************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startTakingScreenshot();
        } else {
            requestPermissions(new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, Cons.REQUEST_CODE_STORAGE_PERMISSION);
        }

    }

    private void startTakingScreenshot() {
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startProjection();

        // start capture handling thread
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CAPTURE) {
            sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);

            if (sMediaProjection != null) {
                File externalFilesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                if (externalFilesDir != null) {
                    STORE_DIRECTORY = externalFilesDir.getAbsolutePath() + "/screenshots/";

                    File storeDirectory = new File(STORE_DIRECTORY);
                    if (!storeDirectory.exists()) {
                        boolean success = storeDirectory.mkdirs();
                        if (!success) {
                            Log.e(TAG, "failed to create file storage directory.");
                            return;
                        }
                    }
                } else {
                    Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
                    return;
                }

                // display metrics
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DisplayMetrics metrics = getResources().getDisplayMetrics();
                        mDensity = metrics.densityDpi;
                        mDisplay = getWindowManager().getDefaultDisplay();

                        // create virtual display depending on device width / height
                        createVirtualDisplay();

                        // register orientation change callback
//                        mOrientationChangeCallback = new OrientationChangeCallback(ScreenshotView.this);
//                        if (mOrientationChangeCallback.canDetectOrientation()) {
//                            mOrientationChangeCallback.enable();
//                        }

                        // register media projection stop callback
                        sMediaProjection.registerCallback(mediaProjectionStopCallback, mHandler);
                    }
                }, 200);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         if (requestCode == Cons.REQUEST_CODE_STORAGE_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    startTakingScreenshot();
                } else {
                    finishScreenshot();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (sMediaProjection != null) {
            sMediaProjection.unregisterCallback(mediaProjectionStopCallback);
            sMediaProjection = null;
        }

        mProjectionManager = null;
        if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
        mImageReader = null;
        mHandler = null;
        mDisplay = null;
        if (mVirtualDisplay != null) mVirtualDisplay.release();
        mVirtualDisplay = null;


//                        if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
//        mOrientationChangeCallback = null;
        super.onDestroy();
    }

    /****************************************** UI Widget Callbacks *******************************/
    private void startProjection() {
        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_CAPTURE);
    }

    private void stopProjection() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sMediaProjection != null) {
                    sMediaProjection.stop();
                }
            }
        });
    }

    /****************************************** Factoring Virtual Display creation ****************/
    private void createVirtualDisplay() {
        // get width and height
        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;


        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);



    }
}