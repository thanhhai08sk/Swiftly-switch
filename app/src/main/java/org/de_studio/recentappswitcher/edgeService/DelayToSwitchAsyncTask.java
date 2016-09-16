package org.de_studio.recentappswitcher.edgeService;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by HaiNguyen on 9/16/16.
 */
public class DelayToSwitchAsyncTask extends AsyncTask<Integer, Void, Void> {
    private static final String TAG = DelayToSwitchAsyncTask.class.getSimpleName();
    private boolean isSleepEnough = false;
    private int holdTime;
    private EdgeServicePresenter presenter;
    private int iconToSwitch;

    public DelayToSwitchAsyncTask(int holdTime, EdgeServicePresenter presenter) {
        this.holdTime = holdTime;
        this.presenter = presenter;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        iconToSwitch = params[0];
        isSleepEnough = false;
        try {
            Thread.sleep(holdTime);
            isSleepEnough = true;
        } catch (InterruptedException e) {
            Log.e(TAG, "interrupt sleeping");
        }

        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        isSleepEnough = false;
        super.onCancelled(aVoid);

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (isSleepEnough ) {
            presenter.onSwitch(iconToSwitch);
        }

        super.onPostExecute(aVoid);
    }

    public void clear() {
        presenter = null;
    }


}

