package com.androidgeeks.hp.zoyme.zoymeinterfaces;

import java.util.Objects;

/**
 * Created by Deepak Sikka on 11/10/2017.
 */

public interface AppUserListDataListener {

    public void setAppUserDataPostion(String mFlag, Object mObject, int mPosition);
    public void subCategoryPosition(String mFlag,Object mObject,int mPosition,String mId,String mTittle);
}
