package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.de_studio.recentappswitcher.IconPackManager;
import org.de_studio.recentappswitcher.MainActivity;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.service.EdgeGestureService;
import org.de_studio.recentappswitcher.service.EdgeSettingDialogFragment;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 5/21/16.
 */
public class CircleFavoriteAdapter extends BaseAdapter {
    private Context mContext;
    private Realm circleFavoRealm;
    private Shortcut shortcut;
    private Drawable defaultDrawable;
    private PackageManager packageManager;
    private SharedPreferences sharedPreferences;
    private IconPackManager.IconPack iconPack;
    private static final String LOG_TAG = CircleFavoriteAdapter.class.getSimpleName();
    private int dragPosition;

    public CircleFavoriteAdapter(Context context) {
        super();
        this.mContext = context;
        circleFavoRealm = Realm.getInstance(new RealmConfiguration.Builder(mContext).name("circleFavo.realm").build());
        sharedPreferences = mContext.getSharedPreferences(MainActivity.DEFAULT_SHAREDPREFERENCE, 0);
        String iconPackPacka = sharedPreferences.getString(EdgeSettingDialogFragment.ICON_PACK_PACKAGE_NAME_KEY, "none");
        packageManager = context.getPackageManager();
        if (!iconPackPacka.equals("none")) {
            IconPackManager iconPackManager = new IconPackManager();
            iconPackManager.setContext(mContext);
            iconPack = iconPackManager.getInstance(iconPackPacka);
        }
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_circle_favorite, parent, false);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.item_icon);
        TextView label = (TextView) view.findViewById(R.id.item_label);
        shortcut = circleFavoRealm.where(Shortcut.class).equalTo("id",position).findFirst();
        CharSequence title= "";
        try {
            title = shortcut.getLabel();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "null when get label");
        }
//        try {
//            title = packageManager.getApplicationLabel(packageManager.getApplicationInfo(shortcut.getPackageName(), 0));
//        } catch (PackageManager.NameNotFoundException e) {
//            remove(position);
//            notifyDataSetChanged();
//            Log.e(LOG_TAG, "NamenotFound when get label");
//            return view;
//        } catch (NullPointerException e) {
//            Log.e(LOG_TAG, "Nullpoint when get label " + e);
//        }

        if (shortcut == null) {
            icon.setImageResource(R.drawable.ic_add_circle_outline_white_48dp);
            icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
        } else {
            if (shortcut.getType() == Shortcut.TYPE_APP) {
                icon.setColorFilter(null);
                try {
                    Drawable defaultDrawable = mContext.getPackageManager().getApplicationIcon(shortcut.getPackageName());
                    if (iconPack!=null) {
                        icon.setImageDrawable(iconPack.getDrawableIconForPackage(shortcut.getPackageName(), defaultDrawable));
                    } else {
                        icon.setImageDrawable(defaultDrawable);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(LOG_TAG, "NameNotFound " + e);
                }
            }else if (shortcut.getType() == Shortcut.TYPE_SETTING) {
                switch (shortcut.getAction()) {
                    case Shortcut.ACTION_WIFI:
                        icon.setImageResource(R.drawable.ic_action_wifi_on);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color. black));
                        break;
                    case Shortcut.ACTION_BLUETOOTH:
                        icon.setImageResource(R.drawable.ic_action_bluetooth_on);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_ROTATION:
                        icon.setImageResource(R.drawable.ic_action_rotate_on);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_POWER_MENU:
                        icon.setImageResource(R.drawable.ic_action_power_menu);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_HOME:
                        icon.setImageResource(R.drawable.ic_icon_home);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_BACK:
                        icon.setImageResource(R.drawable.ic_icon_back);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_NOTI:
                        icon.setImageResource(R.drawable.ic_icon_noti);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_LAST_APP:
                        icon.setImageResource(R.drawable.ic_icon_last_app);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_CALL_LOGS:
                        icon.setImageResource(R.drawable.ic_icon_call_log);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_DIAL:
                        icon.setImageResource(R.drawable.ic_icon_dial);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_CONTACT:
                        icon.setImageResource(R.drawable.ic_icon_contact);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_RECENT:
                        icon.setImageResource(R.drawable.ic_action_recent2);
                        icon.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                        break;
                    case Shortcut.ACTION_NONE:
                        icon.setImageDrawable(null);
                }
            }

        }


//        if (shortcut != null) {
//            try {
//                defaultDrawable = mContext.getPackageManager().getApplicationIcon(shortcut.getPackageName());
//                if (iconPack != null) {
//
//                    icon.setImageDrawable(iconPack.getDrawableIconForPackage(shortcut.getPackageName(), defaultDrawable));
//                } else {
//                    icon.setImageDrawable(defaultDrawable);
//
//                }
//            } catch (PackageManager.NameNotFoundException e) {
//                Log.e(LOG_TAG, "NameNotFound when set icon " + e);
//            } catch (NullPointerException e) {
//                Log.e(LOG_TAG, "Nullpoint when set icon " + e);
//            }
//            label.setText(title);
//        }
        label.setText(title);
        Log.e(LOG_TAG, "label = " + title);


        view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundResource(R.color.grey);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackground(null);
                        break;
                    case DragEvent.ACTION_DROP:
                        changePosition(dragPosition,position);
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackground(null);
                        notifyDataSetChanged();

                    default:
                        break;
                }
                return true;
            }
        });
        view.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        shortcut = circleFavoRealm.where(Shortcut.class).equalTo("id",position).findFirst();
        return null;
    }
    public void remove(int id) {
        Log.e(LOG_TAG, "remove " + id);
        circleFavoRealm.beginTransaction();
        circleFavoRealm.where(Shortcut.class).equalTo("id",id).findFirst().removeFromRealm();
        RealmResults<Shortcut> results = circleFavoRealm.where(Shortcut.class).findAll();
        results.sort("id", true);
        for (int i = 0; i < results.size(); i++) {
            Log.e(LOG_TAG, "id = " + results.get(i).getId());
            if (results.get(i).getId() >= id) {
                Log.e(LOG_TAG, "when i = " + i + "result id = " + results.get(i).getId());
                Shortcut shortcut = results.get(i);
                int oldId = shortcut.getId();
                shortcut.setId(oldId - 1);
            }

//                            results.get(i).setId(results.get(i).getId() - 1);
        }
        circleFavoRealm.commitTransaction();
        notifyDataSetChanged();
        mContext.stopService(new Intent(mContext, EdgeGestureService.class));
        mContext.startService(new Intent(mContext, EdgeGestureService.class));
    }

    public void setDragPosition(int position) {
        this.dragPosition = position;
    }
    public void changePosition(int dragPosition, int dropPosition) {
        Shortcut dropTemp = circleFavoRealm.where(Shortcut.class).equalTo("id", dropPosition).findFirst();
        Shortcut dragTemp = circleFavoRealm.where(Shortcut.class).equalTo("id", dragPosition).findFirst();
        Shortcut shortcut5000 = circleFavoRealm.where(Shortcut.class).equalTo("id",5000).findFirst();
        circleFavoRealm.beginTransaction();
        if (shortcut5000 != null) {
            shortcut5000.removeFromRealm();
        }

        try {
            dropTemp.setId(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            dragTemp.setId(dropPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            dropTemp.setId(dragPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }

        circleFavoRealm.commitTransaction();
        notifyDataSetChanged();
    }

    public void removeDragItem() {
        circleFavoRealm.beginTransaction();
        Shortcut shortcut = circleFavoRealm.where(Shortcut.class).equalTo("id", dragPosition).findFirst();
        if (shortcut != null) {
            shortcut.setType(Shortcut.TYPE_SETTING);
            shortcut.setAction(Shortcut.ACTION_NONE);
            shortcut.setLabel("");
        } else {
            Shortcut shortcut1 = new Shortcut();
            shortcut1.setType(Shortcut.TYPE_SETTING);
            shortcut1.setAction(Shortcut.ACTION_NONE);
            shortcut1.setId(dragPosition);
            circleFavoRealm.copyToRealm(shortcut1);

        }

        circleFavoRealm.commitTransaction();
        notifyDataSetChanged();
    }
}
