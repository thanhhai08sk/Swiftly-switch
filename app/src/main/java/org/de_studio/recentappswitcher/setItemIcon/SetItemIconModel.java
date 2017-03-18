package org.de_studio.recentappswitcher.setItemIcon;

import android.graphics.Bitmap;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Item;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 3/17/17.
 */

public class SetItemIconModel extends BaseModel {
    Realm realm = Realm.getDefaultInstance();
    String itemId;


    public SetItemIconModel(String itemId) {
        this.itemId = itemId;

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
