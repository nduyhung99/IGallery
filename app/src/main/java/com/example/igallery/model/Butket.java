package com.example.igallery.model;

import java.io.Serializable;

public class Butket implements Serializable{
    private int type;
    private String name;
    private String firstImageContainedPath;
    private int totalItem;
    private long dateAdded;
    private long duration;
    private String id;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstImageContainedPath() {
        return firstImageContainedPath;
    }

    public void setFirstImageContainedPath(String firstImageContainedPath) {
        this.firstImageContainedPath = firstImageContainedPath;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Butket() {
    }

    public Butket(int type, String name, String firstImageContainedPath, int totalItem, long dateAdded, long duration, String id) {
        this.type = type;
        this.name = name;
        this.firstImageContainedPath = firstImageContainedPath;
        this.totalItem = totalItem;
        this.dateAdded = dateAdded;
        this.duration = duration;
        this.id = id;
    }
}
