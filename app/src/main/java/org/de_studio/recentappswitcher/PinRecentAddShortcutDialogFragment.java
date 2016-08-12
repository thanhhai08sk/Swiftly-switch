package org.de_studio.recentappswitcher;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.de_studio.recentappswitcher.favoriteShortcut.Shortcut;
import org.de_studio.recentappswitcher.service.EdgeGestureService;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by HaiNguyen on 7/1/16.
 */
public class PinRecentAddShortcutDialogFragment extends DialogFragment {

    private static final String TAG = PinRecentAddShortcutDialogFragment.class.getSimpleName();
    private ListView mListView;
    private Realm myRealm;
    private PinRecentAddShortcutAdapter mAdapter;
    private PackageManager packageManager;
    private List<ResolveInfo> resolveInfos;
    private ResolveInfo mResolveInfo;
    private int position;
    private int mPosition;
    public static final String POSITION_KEY = "position";

    public static PinRecentAddShortcutDialogFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        PinRecentAddShortcutDialogFragment fragment = new PinRecentAddShortcutDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_favorite_app_fragment_list_view, container);
        mListView = (ListView) rootView.findViewById(R.id.add_favorite_list_view);
        myRealm = Realm.getInstance(new RealmConfiguration.Builder(getContext())
                .name("pinApp.realm")
                .schemaVersion(EdgeGestureService.CURRENT_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build());


        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        packageManager=getActivity().getPackageManager();
        resolveInfos =  packageManager.queryIntentActivities(shortcutsIntent, 0);


        mAdapter = new PinRecentAddShortcutAdapter(getContext(),resolveInfos);
        mListView.setAdapter(mAdapter);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int size = (int) myRealm.where(Shortcut.class).count();
                if (size < 6) {
                    mResolveInfo = resolveInfos.get(position);
                    ActivityInfo activity = mResolveInfo.activityInfo;
                    ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                    Intent i = new Intent(Intent.ACTION_CREATE_SHORTCUT);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setComponent(name);
                    startActivityForResult(i, 1);
                } else {
                    Toast.makeText(MyApplication.getContext(),getString(R.string.out_of_limit),Toast.LENGTH_SHORT).show();
                }

            }
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1 && resultCode == Activity.RESULT_OK) {
            try {
                int size = (int) myRealm.where(Shortcut.class).count();


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
                            Resources resources = packageManager.getResourcesForApplication(iconResource.packageName);
                            id = resources.getIdentifier(iconResource.resourceName, null, null);
                        } catch (Exception e) {
                            Log.e(TAG, "onActivityResult: Could not load shortcut icon:");
                        }
                    }
                }

                myRealm.beginTransaction();
                RealmResults<Shortcut> oldShortcut = myRealm.where(Shortcut.class).equalTo("id", PinRecentAddShortcutDialogFragment.this.position).findAll();
                oldShortcut.deleteAllFromRealm();

                Shortcut shortcut = new Shortcut();
                shortcut.setType(Shortcut.TYPE_SHORTCUT);
                shortcut.setId(PinRecentAddShortcutDialogFragment.this.position);
                shortcut.setLabel(label);
                shortcut.setPackageName(packageName);
                shortcut.setIntent(stringIntent);

                if (bmp != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    shortcut.setBitmap(stream.toByteArray());
                } else {
                    shortcut.setResId(id);
                }
                myRealm.copyToRealm(shortcut);
                myRealm.commitTransaction();

//                if (size < 6) {
//                    myRealm.beginTransaction();
//                    RealmResults<Shortcut> oldShortcut = myRealm.where(Shortcut.class).equalTo("id", size).findAll();
//                    oldShortcut.deleteAllFromRealm();
//
//                    Shortcut shortcut = new Shortcut();
//                    shortcut.setType(Shortcut.TYPE_SHORTCUT);
//                    shortcut.setId(size);
//                    shortcut.setLabel(label);
//                    shortcut.setPackageName(packageName);
//                    shortcut.setIntent(stringIntent);
//
//                    if (bmp != null) {
//                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                        shortcut.setBitmap(stream.toByteArray());
//                    } else {
//                        shortcut.setResId(id);
//                    }
//                    myRealm.copyToRealm(shortcut);
//                    myRealm.commitTransaction();
//                } else {
//                    Toast.makeText(MyApplication.getContext(),getString(R.string.out_of_limit),Toast.LENGTH_SHORT).show();
//                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onActivityResult: exception when add shortcut");
            }
            dismiss();

        }else
            super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(POSITION_KEY);
        getActivity().stopService(new Intent(getActivity(), EdgeGestureService.class));
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
//        Utility.getFolderThumbnail(myRealm, mPosition, getActivity());
        try {
            getActivity().startService(new Intent(getActivity(), EdgeGestureService.class));
        } catch (NullPointerException e) {
            Log.e(TAG, "Null when get activity from on dismiss");
        }
        super.onDismiss(dialog);
//        ((AddAppToFolderDialogFragment.MyDialogCloseListener) getActivity()).handleDialogClose();
    }
}
