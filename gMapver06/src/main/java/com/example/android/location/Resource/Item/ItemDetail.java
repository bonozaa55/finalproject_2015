package com.example.android.location.Resource.Item;

/**
 * Created by Adisorn on 2/8/2015.
 */
public class ItemDetail {
    int id;
    String name;
    String detail;
    int iconResource;

    public ItemDetail(int id, String name, String detail, int iconResource) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.iconResource = iconResource;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
