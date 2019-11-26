package com.androidgeeks.hp.zoyme.model;

/**
 * Created by Deepak SIkka on 10/27/2017.
 */

public class ZoyMe_Model {
    private int image;
    private String name;

    public ZoyMe_Model(int image, String name) {
        this.image = image;
        this.name = name;
    }


    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}
