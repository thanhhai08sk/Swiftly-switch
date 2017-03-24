package org.de_studio.recentappswitcher.setItemIcon;

import android.graphics.Bitmap;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Item;
import org.de_studio.recentappswitcher.model.Slot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.realm.Realm;

/**
 * Created by HaiNguyen on 3/17/17.
 */

public class SetItemIconModel extends BaseModel {
    Realm realm = Realm.getDefaultInstance();
    String itemId;
    String folderId;
    int itemState;


    public SetItemIconModel(String itemId, String folderId, int itemState) {
        this.itemId = itemId;
        this.itemState = itemState;
        this.folderId = folderId;
    }



    public void setItemBitmap(final Bitmap bitmap) {


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (itemId != null) {
                    Item item = realm.where(Item.class).equalTo(Cons.ITEM_ID, itemId).findFirst();
                    if (item != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        switch (itemState) {
                            case 2:
                                item.iconBitmap2 = stream.toByteArray();
                                break;
                            case 3:
                                item.iconBitmap3 = stream.toByteArray();
                                break;
                            default:
                                if (item.type.equals(Item.TYPE_DEVICE_SHORTCUT) && item.originalIconBitmap == null) {
                                    item.originalIconBitmap = item.iconBitmap;
                                }
                                item.iconBitmap = stream.toByteArray();
                        }
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (folderId != null) {
                    Slot folder = realm.where(Slot.class).equalTo(Cons.SLOT_ID, folderId).findFirst();
                    if (folder != null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        folder.iconBitmap = stream.toByteArray();
                        folder.useIconSetByUser = true;
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
