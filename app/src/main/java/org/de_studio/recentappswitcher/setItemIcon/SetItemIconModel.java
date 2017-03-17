package org.de_studio.recentappswitcher.setItemIcon;

import android.graphics.Bitmap;
import android.text.TextUtils;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Item;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 3/17/17.
 */

public class SetItemIconModel extends BaseModel {
    Realm realm = Realm.getDefaultInstance();
    HashMap<String, String> drawableMap;
    SortedMap<String, String> sortedDrawableMap = new TreeMap<>();
    String itemId;


    public SetItemIconModel(HashMap<String, String> drawableMap, String itemId) {
        this.drawableMap = drawableMap;
        this.itemId = itemId;

    }


    SortedMap<String, String> getDrawables(String queary) {
        Set<String> keys = drawableMap.keySet();
        if (TextUtils.isEmpty(queary)) {
            for (String key : keys) {
                sortedDrawableMap.put(key, drawableMap.get(key));
            }

        } else {
            for (String key : keys) {
                if (key.contains(queary.toLowerCase())) {
                    sortedDrawableMap.put(key, drawableMap.get(key));
                }
            }
        }
        return sortedDrawableMap;
    }

    public void setItemBitmap(final Bitmap bitmap) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
                if (item != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    item.iconBitmap = stream.toByteArray();
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void clear() {
        realm.close();
    }
}
