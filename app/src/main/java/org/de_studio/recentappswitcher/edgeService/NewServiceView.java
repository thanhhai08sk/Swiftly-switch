package org.de_studio.recentappswitcher.edgeService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by HaiNguyen on 12/23/16.
 */

public class NewServiceView extends Service implements NewServicePresenter.View {





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void clear() {

    }
}
