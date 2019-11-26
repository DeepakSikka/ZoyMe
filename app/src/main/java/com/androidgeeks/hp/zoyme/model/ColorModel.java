package com.androidgeeks.hp.zoyme.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Deepak Sikka on 11/28/2017.
 */

public class ColorModel implements Parcelable {
    private String id;
    private String name;
    private String code;
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



    public ColorModel() {
    }


    protected ColorModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        code = in.readString();
    }

    public static final Creator<ColorModel> CREATOR = new Creator<ColorModel>() {
        @Override
        public ColorModel createFromParcel(Parcel in) {
            return new ColorModel(in);
        }

        @Override
        public ColorModel[] newArray(int size) {
            return new ColorModel[size];
        }
    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(code);
    }
}

