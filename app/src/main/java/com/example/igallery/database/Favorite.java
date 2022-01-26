package com.example.igallery.database;

public class Favorite {
    int id;
    String path;
    int dateAdded;
    int duration;

    public Favorite() {
    }

    public Favorite(int id, String path, int dateAdded, int duration) {
        this.id = id;
        this.path = path;
        this.dateAdded = dateAdded;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(int dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
