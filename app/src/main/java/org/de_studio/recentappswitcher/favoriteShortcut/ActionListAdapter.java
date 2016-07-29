package org.de_studio.recentappswitcher.favoriteShortcut;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by hai on 2/24/2016.
 */
public class ActionListAdapter extends BaseAdapter {
    private static final String LOG_TAG = ActionListAdapter.class.getSimpleName();
    private Context context;
    private String[] stringArray;
    private int mPosition, mode;
    private int mAction;
    private SettingChangeListener listener = null;
    private Realm myRealm;

    public ActionListAdapter(Context context, int position, int mode) {
        super();
        this.context = context;
        stringArray =context.getResources().getStringArray(R.array.setting_shortcut_array);
        mPosition = position;
        this.mode = mode;
        if (mode == FavoriteSettingActivity.MODE_GRID) {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder(this.context)
                    .name("default.realm")
                    .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        } else {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder(this.context)
                    .name("circleFavo.realm")
                    .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        }
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findFirst();
        if (shortcut != null ) {
            if (shortcut.getType() == Shortcut.TYPE_ACTION) {
                mAction = shortcut.getAction();
            }else mAction = -1;

        }

    }

    public void setmPositionAndMode(int position) {
        mPosition = position;
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findFirst();
        if (shortcut != null ) {
            if (shortcut.getType() == Shortcut.TYPE_ACTION) {
                mAction = shortcut.getAction();
            }else mAction = -1;

        }else mAction = -1;
        ActionListAdapter.this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return stringArray.length;
    }

    @Override
    public Object getItem(int position) {
        return stringArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_choose_shortcut_app_list, parent, false);
        }
        final String item = stringArray[position];
        final ImageView icon = (ImageView) view.findViewById(R.id.choose_app_image_view);
        TextView label = (TextView) view.findViewById(R.id.choose_app_title_text_view);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.choose_app_radio_button);
        Shortcut shortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findFirst();
        if (shortcut != null) {
            if ((shortcut.getType() == Shortcut.TYPE_ACTION && mAction != -1 && mAction == Utility.getActionFromLabel(context, item)) ||
                    shortcut.getType() == Shortcut.TYPE_FOLDER && position ==1) {
                radioButton.setChecked(true);
            }else radioButton.setChecked(false);
        }else radioButton.setChecked(false);
        label.setText(item);
        if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_wifi))) {
            icon.setImageResource(R.drawable.ic_wifi);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_bluetooth))) {
            icon.setImageResource(R.drawable.ic_bluetooth);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_rotation))) {
            icon.setImageResource(R.drawable.ic_rotation);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_volume))) {
            icon.setImageResource(R.drawable.ic_volume);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_brightness))) {
            icon.setImageResource(R.drawable.ic_screen_brightness);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_ringer_mode))) {
            icon.setImageResource(R.drawable.ic_sound_normal);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_power_menu))) {
            icon.setImageResource(R.drawable.ic_power_menu);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_home))) {
            icon.setImageResource(R.drawable.ic_home);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_back))) {
            icon.setImageResource(R.drawable.ic_back);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_recent))) {
            icon.setImageResource(R.drawable.ic_recent);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_noti))) {
            icon.setImageResource(R.drawable.ic_notification);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_dial))) {
            icon.setImageResource(R.drawable.ic_dial);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_call_log))) {
            icon.setImageResource(R.drawable.ic_call_log);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_contact))) {
            icon.setImageResource(R.drawable.ic_contact);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_last_app))) {
            icon.setImageResource(R.drawable.ic_last_app);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_flash_light))) {
            icon.setImageResource(R.drawable.ic_flash_light);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_none))) {
            icon.setImageDrawable(null);
        }else if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_folder))) {
            icon.setImageResource(R.drawable.ic_folder);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode != FavoriteSettingActivity.MODE_GRID && stringArray[position].equalsIgnoreCase(context.getString(R.string.setting_shortcut_folder))) {
                    Toast.makeText(context, "Can't add folder to Circle Favorite", Toast.LENGTH_SHORT).show();

                } else {
                    myRealm.beginTransaction();
                    RealmResults<Shortcut> oldShortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findAll();
                    Log.e(LOG_TAG, "mPosition = " + mPosition);
                    oldShortcut.deleteAllFromRealm();
                    Shortcut shortcut = new Shortcut();
                    if (item.equalsIgnoreCase(context.getResources().getString(R.string.setting_shortcut_folder))) {
                        shortcut.setType(Shortcut.TYPE_FOLDER);
                        shortcut.setId(mPosition);
                    } else {
                        shortcut.setType(Shortcut.TYPE_ACTION);
                        shortcut.setId(mPosition);
                        shortcut.setLabel(item);
                        shortcut.setAction(Utility.getActionFromLabel(context, item));
                    }

                    myRealm.copyToRealm(shortcut);
                    myRealm.commitTransaction();
                    mAction = Utility.getActionFromLabel(context,item);
                    ActionListAdapter.this.notifyDataSetChanged();
                    listener.onSettingChange();
                }
                if ((stringArray[position].equalsIgnoreCase(context.getString(R.string.setting_shortcut_rotation)) ||
                        stringArray[position].equalsIgnoreCase(context.getString(R.string.setting_shortcut_brightness))) &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        !Settings.System.canWrite(context)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.write_setting_permission)
                            .setMessage(R.string.write_setting_permission_explain)
                            .setPositiveButton(R.string.go_to_setting, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent notiIntent = new Intent();
                                    notiIntent.setAction(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                    context.startActivity(notiIntent);

                                }
                            });
                    builder.show();
                }


            }
        });

        return view;
    }

    public interface SettingChangeListener{
        void onSettingChange();
    }

    public void registerListener(SettingChangeListener listener) {
        this.listener = listener;
    }

}
