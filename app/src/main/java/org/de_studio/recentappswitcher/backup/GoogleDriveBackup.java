package org.de_studio.recentappswitcher.backup;

import android.app.Activity;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import java.lang.ref.WeakReference;

/**
 * Created by HaiNguyen on 2/21/17.
 */

public class GoogleDriveBackup implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = GoogleDriveBackup.class.getSimpleName();
    @Nullable
    private GoogleApiClient googleApiClient;

    @Nullable
    private WeakReference<Activity> activityRef;


    public void init(@NonNull final Activity activity, GoogleApiClient.ConnectionCallbacks callbacks) {
        this.activityRef = new WeakReference<>(activity);

        googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(callbacks)
                .addOnConnectionFailedListener(this)
                .build();

    }

    public GoogleApiClient getClient(){
        return googleApiClient;
    }

    public void start() {
        if (googleApiClient != null) {
            Log.e(TAG, "start: connect");
            googleApiClient.connect();
        } else {
            throw new IllegalStateException("You should call init before start");
        }
    }

    public void stop() {
        if (googleApiClient != null) {
            Log.e(TAG, "stop: disconnect");
            googleApiClient.disconnect();
        } else {
            throw new IllegalStateException("You should call init before start");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult result) {
        Log.e(TAG, "GoogleApiClient connection failed: " + result.toString());

        if (result.hasResolution() && activityRef != null && activityRef.get() != null) {
            Activity a = activityRef.get();
            // show the localized error dialog.
            try {
                Log.e(TAG, "onConnectionFailed: start resolution");
                result.startResolutionForResult(a, 1);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                GoogleApiAvailability.getInstance().getErrorDialog(a, result.getErrorCode(), 0).show();
            }
        } else {
            Log.d("error", "cannot resolve connection issue");
        }
    }
}
