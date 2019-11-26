package com.androidgeeks.hp.zoyme.preferenceManager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Deepak Sikka on 10/25/2017.
 */

public class SharedPreferenceManager {
    private static final String SUGGEST_ME_PREFERENCES_FILE_NAME = "suggest_me_preferences";
    private static final String REMEMBER_ME = "remember_me";
    private static final String Zoy_ME_PREFERENCES_FILE_NAME = "zoy_me_preferences";
    SharedPreferences sharedPreferences;
    Context context;
    private static final String USER_ID = "user_id";
    private static final String TOKEN = "token";
    private String res = "";
    private static final String FLAT = "flat";
    private static final String LOCALITY = "locality";
    private static final String CITY = "city";
    private static final String STATE = "state";
    private static final String PINCODE = "pincode";
    private static final String NAME = "name";
    private static final String PHONE = "phone";
    private static final String ALTERNATEPHONE = "alternate_phone";
    private static final String USERID = "user_id";
    private static final String ID = "id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String MOBILE = "mobile";
    private static final String DOB = "dob";
    private static final String DESCRIPTION = "description";
    private static final String QUANTITY = "quantity";
    private static final String PRICE = "price";
    private static final String CATEGORYNAME = "category_name";

    public SharedPreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Zoy_ME_PREFERENCES_FILE_NAME, context.MODE_PRIVATE);
    }

    public void setCategoryname(String categoryname) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CATEGORYNAME, categoryname);
        editor.commit();
    }

    public String getCategoryname() {
        String res = "";
        res = sharedPreferences.getString(CATEGORYNAME, "");
        return res;
    }

    public void setPrice(String price) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PRICE, price);
        editor.commit();
    }

    public String getPrice() {
        String res = "";
        res = sharedPreferences.getString(PRICE, "");
        return res;
    }

    public void setQuantity(String quantity) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(QUANTITY, quantity);
        editor.commit();
    }

    public String getQuantity() {
        String res = "";
        res = sharedPreferences.getString(QUANTITY, "");
        return res;
    }


    public void setDescription(String description) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DESCRIPTION, description);
        editor.commit();
    }

    public String getDescription() {
        String res = "";
        res = sharedPreferences.getString(DESCRIPTION, "");
        return res;
    }

    public void setDob(String dob) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DOB, dob);
        editor.commit();
    }

    public String getDob() {
        String res = "";
        res = sharedPreferences.getString(DOB, "");
        return res;
    }

    public void setFirstName(String firstName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FIRST_NAME, firstName);
        editor.commit();
    }

    public String getFirstName() {
        String res = "";
        res = sharedPreferences.getString(FIRST_NAME, "");
        return res;
    }

    public void setLastName(String lastName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_NAME, lastName);
        editor.commit();
    }

    public String getLastName() {
        String res = "";
        res = sharedPreferences.getString(LAST_NAME, "");
        return res;
    }

    public void setMobile(String mobile) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MOBILE, mobile);
        editor.commit();
    }

    public String getMobile() {
        String res = "";
        res = sharedPreferences.getString(MOBILE, "");
        return res;
    }

    public void setId(String id) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ID, id);
        editor.commit();
    }

    public String getId() {
        String res = "";
        res = sharedPreferences.getString(ID, "");
        return res;
    }


    public void setUserid(String userid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERID, userid);
        editor.commit();
    }

    public String getUserid() {
        String res = "";
        res = sharedPreferences.getString(USERID, "");
        return res;
    }

    public void setAlternatephone(String alternatephone) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ALTERNATEPHONE, alternatephone);
        editor.commit();
    }

    public String getAlternatephone() {
        String res = "";
        res = sharedPreferences.getString(ALTERNATEPHONE, "");
        return res;
    }

    public void setPhone(String phone) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PHONE, phone);
        editor.commit();
    }

    public String getPhone() {
        String res = "";
        res = sharedPreferences.getString(PHONE, "");
        return res;
    }

    public void setName(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.commit();
    }

    public String getName() {
        String res = "";
        res = sharedPreferences.getString(NAME, "");
        return res;
    }


    public void setPincode(String pincode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PINCODE, pincode);
        editor.commit();
    }

    public String getPincode() {
        String res = "";
        res = sharedPreferences.getString(PINCODE, "");
        return res;
    }


    public void setState(String state) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STATE, state);
        editor.commit();
    }

    public String getState() {
        String res = "";
        res = sharedPreferences.getString(STATE, "");
        return res;
    }

    public void setCity(String city) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CITY, city);
        editor.commit();
    }

    public String getCity() {
        String res = "";
        res = sharedPreferences.getString(CITY, "");
        return res;
    }


    public void setLocality(String locality) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCALITY, locality);
        editor.commit();
    }

    public String getLocality() {
        String res = "";
        res = sharedPreferences.getString(LOCALITY, "");
        return res;
    }

    public void setFlat(String flat) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FLAT, flat);
        editor.commit();
    }

    public String getFlat() {
        String res = "";
        res = sharedPreferences.getString(FLAT, "");
        return res;
    }

    public void setRememberMe(boolean val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(REMEMBER_ME, val);
        editor.commit();
    }

    public boolean getRememberMe() {
        boolean res = false;
        res = sharedPreferences.getBoolean(REMEMBER_ME, false);
        return res;
    }


    public void setUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public String getUserId() {
        res = "";
        res = sharedPreferences.getString(USER_ID, "");
        return res;
    }

    public void setToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        String res = "";
        res = sharedPreferences.getString(TOKEN, "");
        return res;
    }

    public void clearall() {

        sharedPreferences = context.getSharedPreferences(SUGGEST_ME_PREFERENCES_FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

    }

}
