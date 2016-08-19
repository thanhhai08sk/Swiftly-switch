package org.de_studio.recentappswitcher.edgeService;

import android.util.Log;

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 8/19/16.
 */
public class edgeServiceModel {
    Set<String> blackListSet;
    Realm pinRealm;
    String launcherPackagename;
    Shortcut[] savedRecentShortcut, pinnedShortcut;
    Set<Shortcut> pinnedSet;
    String lastAppPackageName;
    boolean isFreeAndOutOfTrial;


    private static final String TAG = edgeServiceModel.class.getSimpleName();

    public edgeServiceModel(Set<String> blackListSet, Realm pinRealm, String launcherPackagename, boolean isFreeAndOutOfTrial) {
        this.blackListSet = blackListSet;
        this.pinRealm = pinRealm;
        this.launcherPackagename = launcherPackagename;
        this.isFreeAndOutOfTrial = isFreeAndOutOfTrial;
        setPinnedShortcut();
    }

    public Shortcut[] getRecentList(ArrayList<String> tempPackageName) {
        Shortcut[] recentShortcut;
        boolean inHome = false;
        if (tempPackageName.size() > 0) {
            if (tempPackageName.contains(launcherPackagename) && tempPackageName.get(0).equalsIgnoreCase(launcherPackagename)) {
                inHome = true;
            }
            tempPackageName.remove(0);
            if (!inHome && tempPackageName.contains(launcherPackagename)) {
                tempPackageName.remove(launcherPackagename);
            }
        }

        if (tempPackageName.size() < 6 && savedRecentShortcut != null) {
            for (int i = 0; i < savedRecentShortcut.length; i++) {
                if (!tempPackageName.contains(savedRecentShortcut[i].getPackageName()) && tempPackageName.size() < 6) {
                    tempPackageName.add(savedRecentShortcut[i].getPackageName());
                }
            }
        }

        if (tempPackageName.size() >= 1) {
            lastAppPackageName = tempPackageName.get(0);
        }
        for (Shortcut t : pinnedSet) {
            if (tempPackageName.contains(t.getPackageName())) {
                tempPackageName.remove(t.getPackageName());
            }
        }
        if (6 - tempPackageName.size() - pinnedShortcut.length > 0) {
            recentShortcut = new Shortcut[tempPackageName.size() + pinnedShortcut.length];
        } else {
            recentShortcut = new Shortcut[6];
        }
        int n = 0; //count for pin
        int m = 0; //count for temp
        Shortcut tempShortcut;
        for (int t = 0; t <recentShortcut.length; t++) {
            Log.e(TAG, "onTouch:  n = " + n + "\nm = " + m + "\nt = "+ t);
//                                Log.e(TAG, "onTouch: pin.n.id = " + pinnedShortcut[n].getId());
            if (pinnedShortcut.length > n && pinnedShortcut[n].getId() == t) {
                recentShortcut[t] = pinnedShortcut[n];
                n++;
            } else if (m < tempPackageName.size()) {
                tempShortcut = new Shortcut();
                tempShortcut.setType(Shortcut.TYPE_APP);
                tempShortcut.setPackageName(tempPackageName.get(m));
                m++;
                recentShortcut[t] = tempShortcut;
            } else {
                recentShortcut[t] = pinnedShortcut[n];
                n++;
            }
        }
        return recentShortcut;
    }


    public void setPinnedShortcut() {
        RealmResults<Shortcut> results1 =
                pinRealm.where(Shortcut.class).findAll().sort("id");
        if (isFreeAndOutOfTrial) {
            results1 = null;
        }
        int i = 0;
        if (results1 == null) {
            pinnedShortcut = new Shortcut[0];
        } else {
            pinnedShortcut = new Shortcut[results1.size()];
            for (Shortcut shortcut : results1) {
                Log.e(TAG, "result = " + shortcut.getPackageName());
                pinnedShortcut[i] = shortcut;
                i++;
            }
        }
        pinnedSet = new HashSet<Shortcut>(Arrays.asList(pinnedShortcut));
    }
}
