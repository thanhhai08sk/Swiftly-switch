package org.de_studio.recentappswitcher.base;

import android.content.Context;
import android.content.Intent;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.edgeService.NewServiceView;

/**
 * Created by HaiNguyen on 6/3/17.
 */

public class JavaCodeHere {

    private void methodss(Context context) {
        Intent broadcastIntent = new Intent(Cons.ACTION_TOGGLE_EDGES);
        broadcastIntent.setClassName("package",NewServiceView.class.getName());

    }
}
