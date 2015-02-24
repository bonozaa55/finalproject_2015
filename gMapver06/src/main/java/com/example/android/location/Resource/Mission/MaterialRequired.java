package com.example.android.location.Resource.Mission;

/**
 * Created by Adisorn on 2/7/2015.
 */
public class MaterialRequired {
    int itemID;
    int quantity;

    public MaterialRequired(int itemID, int quantity) {
        this.itemID = itemID;
        this.quantity = quantity;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
