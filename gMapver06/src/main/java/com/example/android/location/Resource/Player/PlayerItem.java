package com.example.android.location.Resource.Player;

/**
 * Created by Adisorn on 2/8/2015.
 */
public class PlayerItem {
    private int id;
    private int quantity;

    public PlayerItem(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
