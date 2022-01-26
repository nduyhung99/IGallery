package com.example.igallery.model;

import android.net.Uri;

import java.io.Serializable;

public class ItemBin implements Serializable {
    String path;
    Uri uri;
    long date;
    boolean selected;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ItemBin() {
    }

    public ItemBin(String path, Uri uri, long date, boolean selected) {
        this.path = path;
        this.uri = uri;
        this.date = date;
        this.selected = selected;
    }
}
