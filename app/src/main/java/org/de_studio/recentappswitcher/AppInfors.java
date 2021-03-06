package org.de_studio.recentappswitcher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by hai on 1/5/2016.
 */
public class AppInfors implements Comparable<AppInfors> {
    public String label;
    public Drawable iconDrawable;
    public Bitmap iconBitmap;
    public String packageName;
    public Intent launchIntent;

    @Override
    public int compareTo(AppInfors another) {

        return label.toLowerCase().compareTo(another.label.toLowerCase());
    }
}
