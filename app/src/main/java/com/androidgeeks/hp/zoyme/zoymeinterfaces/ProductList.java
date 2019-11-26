package com.androidgeeks.hp.zoyme.zoymeinterfaces;

import android.widget.ImageView;

/**
 * Created by Deepak Sikka on 11/13/2017.
 */

public interface ProductList {
    public void subProductPosition(String mFlag,Object mObject,int mPosition,String mId,String mTittle);
    public void subWishlistPosition(String mFlag,Object mObject,int mPosition,String mId,ImageView click);

}
