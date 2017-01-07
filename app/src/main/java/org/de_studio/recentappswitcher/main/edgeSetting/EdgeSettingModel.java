package org.de_studio.recentappswitcher.main.edgeSetting;

import android.content.SharedPreferences;
import android.graphics.Color;

import org.de_studio.recentappswitcher.Cons;
import org.de_studio.recentappswitcher.base.BaseModel;
import org.de_studio.recentappswitcher.model.Collection;
import org.de_studio.recentappswitcher.model.Edge;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.subjects.PublishSubject;

/**
 * Created by HaiNguyen on 11/5/16.
 */

public class EdgeSettingModel extends BaseModel {
    Realm realm = Realm.getDefaultInstance();
    String edgeId;
    Edge edge;
    PublishSubject<Edge> gotEdgeSubject = PublishSubject.create();
    SharedPreferences sharedPreferences;

    public EdgeSettingModel(String edgeId, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.edgeId = edgeId;
    }

    public void setup() {
        edge = realm.where(Edge.class).equalTo(Cons.EDGE_ID, edgeId).findFirst();
        if (edge != null) {
            gotEdgeSubject.onNext(edge);
        }
    }


    public Edge getEdge() {
        if (edge == null) {
            edge = realm.where(Edge.class).equalTo(Cons.EDGE_ID, edgeId).findFirst();
        }
        return edge;
    }

    public boolean isEdgeEnabled() {
        switch (edgeId) {
            case "edge1":
                return sharedPreferences.getBoolean(Cons.EDGE_1_ON_KEY, true);
            case "edge2":
                return sharedPreferences.getBoolean(Cons.EDGE_2_ON_KEY, false);
        }
        return false;
    }

    public PublishSubject<Edge> onGotEdge() {
        return gotEdgeSubject;
    }

    public RealmResults<Collection> getRecentSetsList() {
        return realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_RECENT).findAll();
    }

    public RealmResults<Collection> getQuickActionsSetsList() {
        return realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_QUICK_ACTION).findAll();
    }

    public RealmResults<Collection> getCircleFavoriteSetsList() {
        return realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_CIRCLE_FAVORITE).findAll();
    }

    public RealmResults<Collection> getGridFavoriteSetsList() {
        return realm.where(Collection.class).equalTo(Cons.TYPE, Collection.TYPE_GRID_FAVORITE).findAll();
    }

    public void setMode(final int mode) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                edge.mode = mode;
            }
        });
    }

    public void setRecentSet(final String id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection recent = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, id).findFirst();
                if (recent != null) {
                    edge.recent = recent;
                }
            }
        });
    }

    public void setCircleFavoriteSet(final String id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection circle = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, id).findFirst();
                if (circle != null) {
                    edge.circleFav = circle;
                }
            }
        });
    }

    public void setQuickActionsSet(final String id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection quickActions = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, id).findFirst();
                if (quickActions != null) {
                    edge.quickAction = quickActions;
                }
            }
        });
    }

    public void setGridFavoriteSet(final String id) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Collection grid = realm.where(Collection.class).equalTo(Cons.COLLECTION_ID, id).findFirst();
                if (grid != null) {
                    edge.grid = grid;
                }
            }
        });
    }

    public void setShowGuide(final boolean showGuide) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                edge.useGuide = showGuide;
            }
        });
    }

    public int getGuideColor() {
        return Color.BLUE;
    }

    public void setGuideColor(int color) {

    }

    public void setEnable(boolean enable) {
        switch (edgeId) {
            case "edge1":
                sharedPreferences.edit().putBoolean(Cons.EDGE_1_ON_KEY, enable).commit();
                break;
            case "edge2":
                sharedPreferences.edit().putBoolean(Cons.EDGE_2_ON_KEY, enable).commit();
                break;
        }
    }





    @Override
    public void clear() {
        realm.close();
    }
}
