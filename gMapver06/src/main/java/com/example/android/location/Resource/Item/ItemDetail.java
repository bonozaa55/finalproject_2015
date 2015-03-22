package com.example.android.location.Resource.Item;

/**
 * Created by Adisorn on 2/8/2015.
 */
public class ItemDetail {
    int id;
    String name;
    String detail;
    int iconResource;
    int atkDMG;
    int defDMG;
    int lv=0;
    int initialCost;

    public ItemDetail(int id, String name, String detail, int iconResource) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.iconResource = iconResource;
    }

    public ItemDetail(int id, String name, String detail, int iconResource, int atkDMG, int defDMG,int initialCost) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.iconResource = iconResource;
        this.atkDMG = atkDMG;
        this.defDMG = defDMG;
        this.initialCost=initialCost;

    }

    public void setInitialCost(int initialCost) {
        this.initialCost = initialCost;
    }

    public int getInitialCost() {
        return initialCost;
    }

    public int getAtkDMG() {
        return atkDMG;
    }

    public void setAtkDMG(int atkDMG) {
        this.atkDMG = atkDMG;
    }

    public int getDefDMG() {
        return defDMG;
    }

    public void setDefDMG(int defDMG) {
        this.defDMG = defDMG;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
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
