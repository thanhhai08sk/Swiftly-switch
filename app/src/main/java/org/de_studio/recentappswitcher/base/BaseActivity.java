package org.de_studio.recentappswitcher.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.utils.RetainFragment;
import org.de_studio.recentappswitcher.utils.inAppPurchase.IabBroadcastReceiver;
import org.de_studio.recentappswitcher.utils.inAppPurchase.IabHelper;
import org.de_studio.recentappswitcher.utils.inAppPurchase.IabResult;
import org.de_studio.recentappswitcher.utils.inAppPurchase.Inventory;
import org.de_studio.recentappswitcher.utils.inAppPurchase.Purchase;

import javax.inject.Inject;

import butterknife.ButterKnife;

import static org.de_studio.recentappswitcher.Cons.SKU_PRO;

/**
 * Created by HaiNguyen on 11/11/16.
 */

public abstract class BaseActivity<T, P extends BasePresenter> extends AppCompatActivity implements PresenterView, IabBroadcastReceiver.IabBroadcastListener {

    private static final String TAG = BaseActivity.class.getSimpleName();
    static final int RC_REQUEST = 10001;
    protected RetainFragment<T> retainFragment;
    String tag = getClass().getCanonicalName();
    protected boolean destroyedBySystem;

    IabHelper mHelper;
    IInAppBillingService mService;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };
    IabBroadcastReceiver mBroadcastReceiver;

    @Inject
    protected P presenter;

    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject();
        retainFragment = RetainFragment.findOrCreate(getSupportFragmentManager(), tag);
        getDataFromRetainFragment();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        if (presenter != null) {
            presenter.onViewAttach(this);
        }
    }

    protected abstract void inject();


    @LayoutRes
    protected abstract int getLayoutId();

    @CallSuper
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        destroyedBySystem = true;
    }

    @CallSuper
    @Override
    protected void onResume() {
        super.onResume();
        destroyedBySystem = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null || !mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.onViewDetach();
        }
        if (destroyedBySystem) onDestroyBySystem();
        else onDestroyByUser();

        if (mService != null) {
            unbindService(mServiceConn);
        }
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
        super.onDestroy();
    }

    public T getData() {
        return retainFragment.data;
    }

    public void setData(T data) {
        retainFragment.data = data;
    }

    public void onDestroyByUser() {
        retainFragment.remove(getSupportFragmentManager());
        retainFragment.data = null;
        retainFragment = null;
    }

    public abstract void getDataFromRetainFragment();

    public abstract void onDestroyBySystem();

    public void setUpCheckingPurchase() {
        bindInAppBillingService();
        mHelper = new IabHelper(this, Cons.BASE_64_ENCODED_PUBLIC_KEY);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                if (mHelper == null) return;

                mBroadcastReceiver = new IabBroadcastReceiver(BaseActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });

    }

    public void buyPro() {
        String payload = "";
        if (mHelper == null) {
            mHelper = new IabHelper(this, Cons.BASE_64_ENCODED_PUBLIC_KEY);
        }

        try {
            mHelper.launchPurchaseFlow(this, SKU_PRO, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.e(TAG, "buyPro: error");
        }
    }

    private void bindInAppBillingService() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }
            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_PRO)) {
                alert(getString(R.string.thanks_for_upgrading_to_pro));
                SharedPreferences shared = getSharedPreferences(Cons.SHARED_PREFERENCE_NAME, 0);
                shared.edit().putBoolean(Cons.PRO_PURCHASED_KEY, true).commit();
                Utility.rebootApp(getApplicationContext());
            }
        }
    };
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.e(TAG, "Query inventory finished.");
            if (mHelper == null) return;
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            Log.e(TAG, "Query inventory was successful.");
            Purchase premiumPurchase = inventory.getPurchase(SKU_PRO);
            boolean mIsPremium = (premiumPurchase != null);
            Log.e(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            getShared().edit().putBoolean(Cons.PRO_PURCHASED_KEY, mIsPremium).commit();
        }
    };

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void receivedBroadcast() {
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    public SharedPreferences getShared() {
        return getSharedPreferences(Cons.SHARED_PREFERENCE_NAME, 0);
    }

}
