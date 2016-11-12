package org.de_studio.recentappswitcher.favoriteShortcut;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.de_studio.recentappswitcher.MyRealmMigration;
import org.de_studio.recentappswitcher.R;
import org.de_studio.recentappswitcher.Utility;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 6/16/16.
 */
public class ShortcutTabFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String TAG = ShortcutTabFragment.class.getSimpleName();
    private ListView mListView;
    private ShortcutListAdapter mAdapter;
    private int mode,mPosition;
    private Realm myRealm;
    private PackageManager packageManager;
    private List<ResolveInfo> resolveInfos;
    private ResolveInfo mResolveInfo;


    public static ShortcutTabFragment newInstance(int sectionNumber) {
        ShortcutTabFragment fragment = new ShortcutTabFragment();
        Bundle agument = new Bundle();
        agument.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(agument);
        return fragment;
    }
    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }
    public void setMode(int mode) {
        this.mode = mode;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_tab, container, false);
        mListView = (ListView) view.findViewById(R.id.fragment_app_tab_list_view);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        if (mode == FavoriteSettingActivity.MODE_GRID) {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder()
                    .name("default.realm")
                    .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        } else {
            myRealm = Realm.getInstance(new RealmConfiguration.Builder()
                    .name("circleFavo.realm")
                    .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                    .migration(new MyRealmMigration())
                    .build());
        }


//        ((ChooseShortcutActivity)getActivity()).setSettingAdapter(mAdapter);
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);

        packageManager=getActivity().getPackageManager();
        resolveInfos =  packageManager.queryIntentActivities(shortcutsIntent, 0);


        mAdapter = new ShortcutListAdapter(getContext(), mode, resolveInfos);
        mListView.setAdapter(mAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mResolveInfo = resolveInfos.get(position);
                ActivityInfo activity = mResolveInfo.activityInfo;
                ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                Intent i = new Intent(Intent.ACTION_CREATE_SHORTCUT);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.setComponent(name);
                startActivityForResult(i, 1);
            }
        });
        ((ChooseShortcutActivity)getActivity()).setShortcutShortcutAdapter(mAdapter);


        return view;
    }

    public ShortcutListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1 && resultCode == Activity.RESULT_OK) {
            try {
//                Bundle bundle = data.getExtras();
//                for (String key : bundle.keySet()) {
//                    Object value = bundle.get(key);
//                    Log.e(TAG, String.format("%s %s (%s)", key,
//                            value.toString(), value.getClass().getName()));
//                }

                String label = (String) data.getExtras().get(Intent.EXTRA_SHORTCUT_NAME);
                String stringIntent = ((Intent) data.getExtras().get(Intent.EXTRA_SHORTCUT_INTENT)).toUri(0);
                String packageName =  mResolveInfo.activityInfo.packageName;
                int id = 0;

                Bitmap bmp = null;
                Parcelable extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON);
                if (extra != null && extra instanceof Bitmap)
                    bmp = (Bitmap) extra;
                if (bmp == null) {
                    extra = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE);
                    if (extra != null && extra instanceof Intent.ShortcutIconResource) {
                        try {
                            Intent.ShortcutIconResource iconResource = (Intent.ShortcutIconResource) extra;
                            packageName = iconResource.packageName;
                            Resources resources = packageManager.getResourcesForApplication(iconResource.packageName);
                            id = resources.getIdentifier(iconResource.resourceName, null, null);
                            Log.e(TAG, "onActivityResult: get resource " + iconResource.toString() + "\nid = " + id );
                        } catch (Exception e) {
                            Log.e(TAG, "onActivityResult: Could not load shortcut icon:");
                        }
                    }
                }


                myRealm.beginTransaction();
                RealmResults<Shortcut> oldShortcut = myRealm.where(Shortcut.class).equalTo("id",mPosition).findAll();
                Log.e(TAG, "mPosition = " + mPosition);
                oldShortcut.deleteAllFromRealm();
                Shortcut shortcut = new Shortcut();

                shortcut.setType(Shortcut.TYPE_SHORTCUT);
                shortcut.setId(mPosition);
                shortcut.setLabel(label);
                shortcut.setPackageName(packageName);
                shortcut.setIntent(stringIntent);

                if (bmp != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    shortcut.setBitmap(stream.toByteArray());
                } else {
                    Log.e(TAG, "onActivityResult: bitmap null, use resId " + id);
                    shortcut.setResId(id);
                }
                myRealm.copyToRealm(shortcut);
                myRealm.commitTransaction();
                mAdapter.getListener().onAppChange();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onActivityResult: exception when add shortcut");
            }

        }else

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (myRealm != null) {
            myRealm.close();
        }
        super.onDestroy();
    }

    public void setmPositioinToNext() {
        if (mPosition < Utility.getSizeOfFavoriteGrid(getContext())-1) {
            mPosition++;
        }
    }

    public void setmPositionToBack() {
        if (mPosition > 0 && mAdapter != null) {
            mPosition--;
        }
    }
}
