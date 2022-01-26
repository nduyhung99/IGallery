package com.example.igallery.model;

import java.io.Serializable;

public class Item implements Serializable {
    String pathOfItem;
    long dateAdded;
    long durationVideo;
    boolean itemSelected;
    String id;
    String butket;

    public String getButket() {
        return butket;
    }

    public void setButket(String butket) {
        this.butket = butket;
    }

    public String getPathOfItem() {
        return pathOfItem;
    }

    public void setPathOfItem(String pathOfItem) {
        this.pathOfItem = pathOfItem;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDurationVideo() {
        return durationVideo;
    }

    public void setDurationVideo(long durationVideo) {
        this.durationVideo = durationVideo;
    }

    public boolean isItemSelected() {
        return itemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        this.itemSelected = itemSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Item() {
    }

    public Item(String pathOfItem, long dateAdded, long durationVideo, boolean itemSelected, String id, String butket) {
        this.pathOfItem = pathOfItem;
        this.dateAdded = dateAdded;
        this.durationVideo = durationVideo;
        this.itemSelected = itemSelected;
        this.id = id;
        this.butket = butket;
    }
}
