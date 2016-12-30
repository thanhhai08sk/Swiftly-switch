package org.de_studio.recentappswitcher.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HaiNguyen on 12/30/16.
 */

public class DataInfo extends RealmObject {
    public static final String ID = "dataInfo";
    public boolean recentOk;
    public boolean circleFavoriteOk;
    public boolean gridOk;
    public boolean blackListOk;
    public boolean quickActionOk;
    public boolean edge1Ok;
    public boolean edge2Ok;
    @PrimaryKey
    private String id;

    public DataInfo() {
        id = ID;
    }

    public boolean isRecentOk() {
        return recentOk;
    }

    public boolean everyThingsOk() {
        return recentOk && circleFavoriteOk && gridOk && blackListOk && quickActionOk && edge1Ok && edge2Ok;
    }

    public void setRecentOk(boolean recentOk) {
        this.recentOk = recentOk;
    }

    public boolean isCircleFavoriteOk() {
        return circleFavoriteOk;
    }

    public void setCircleFavoriteOk(boolean circleFavoriteOk) {
        this.circleFavoriteOk = circleFavoriteOk;
    }

    public boolean isGridOk() {
        return gridOk;
    }

    public void setGridOk(boolean gridOk) {
        this.gridOk = gridOk;
    }

    public boolean isBlackListOk() {
        return blackListOk;
    }

    public void setBlackListOk(boolean blackListOk) {
        this.blackListOk = blackListOk;
    }

    public boolean isQuickActionOk() {
        return quickActionOk;
    }

    public void setQuickActionOk(boolean quickActionOk) {
        this.quickActionOk = quickActionOk;
    }

    public boolean isEdge1Ok() {
        return edge1Ok;
    }

    public void setEdge1Ok(boolean edge1Ok) {
        this.edge1Ok = edge1Ok;
    }

    public boolean isEdge2Ok() {
        return edge2Ok;
    }

    public void setEdge2Ok(boolean edge2Ok) {
        this.edge2Ok = edge2Ok;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
