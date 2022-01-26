package com.example.igallery.model;

import java.io.Serializable;
import java.util.List;

public class ItemPhoto implements Serializable {
    String title;
    List<Item> listItem;
    String countItem;

    public String getCountItem() {
        return countItem;
    }

    public void setCountItem(String countItem) {
        this.countItem = countItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Item> getListItem() {
        return listItem;
    }

    public void setListItem(List<Item> listItem) {
        this.listItem = listItem;
    }

    public ItemPhoto() {
    }

    public ItemPhoto(String title, List<Item> listItem, String countItem) {
        this.title = title;
        this.listItem = listItem;
        this.countItem = countItem;
    }
}
