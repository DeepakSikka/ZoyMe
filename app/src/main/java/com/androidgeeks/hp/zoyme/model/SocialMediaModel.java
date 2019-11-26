package com.androidgeeks.hp.zoyme.model;

import android.os.Parcel;
import android.os.Parcelable;


public class SocialMediaModel implements Parcelable {
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String dob;
    private String userId;
    private String profilePic;

    public SocialMediaModel() {
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    protected SocialMediaModel(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        gender = in.readString();
        dob = in.readString();
        userId = in.readString();
        profilePic = in.readString();
    }

    public static final Creator<SocialMediaModel> CREATOR = new Creator<SocialMediaModel>() {
        @Override
        public SocialMediaModel createFromParcel(Parcel in) {
            return new SocialMediaModel(in);
        }

        @Override
        public SocialMediaModel[] newArray(int size) {
            return new SocialMediaModel[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(gender);
        parcel.writeString(dob);
        parcel.writeString(userId);
        parcel.writeString(profilePic);
    }
}
