package com.androidgeeks.hp.zoyme.helper;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.androidgeeks.hp.zoyme.model.SocialMediaModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.SocialMediaGetDataInterface;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FBLoginActivity {
    private static final String TAG = "ZoyMeLoginActivity";
    private String id, first_name,last_name, gender, email, birthday;
    private Activity activity;
    private SocialMediaGetDataInterface socialMediaGetDataInterface;

    public void inItFacebook(final Activity activity, CallbackManager callbackManager, final SocialMediaGetDataInterface socialMediaGetDataInterface) {
        this.activity = activity;
        this.socialMediaGetDataInterface=socialMediaGetDataInterface;

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("onSuccess");

                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        Log.i("accessToken", accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object,
                                                            GraphResponse response) {

                                        Log.i(TAG, response.toString());
                                        try {
                                            id = object.getString("id");
                                            try {
                                                URL profile_pic = new URL(
                                                        "https://graph.facebook.com/" + id + "/picture?type=large");
                                                Log.i(TAG, profile_pic + "");

                                               String name = object.getString("name");
                                              first_name= name.substring(0, name.indexOf(" "));
                                                last_name= name.substring( name.indexOf(" ")+1,name.length());

                                                if (object.has("email")) {
                                                    email = object.getString("email");
                                                } else {
                                                    email = "";
                                                }
                                                if (object.getString("gender").equalsIgnoreCase("male")) {
                                                    gender = "male";
                                                } else {
                                                    gender = "female";
                                                }
                                                if (object.has("birthday")) {
                                                   birthday= changeDOB(object.getString("birthday"));
                                                }
                                                else{
                                                    birthday="";
                                                }
                                                SocialMediaModel socialMediaModel=new SocialMediaModel();
                                                socialMediaModel.setFirstName(first_name);
                                                socialMediaModel.setLastName(last_name);
                                                socialMediaModel.setEmail(email);
                                                socialMediaModel.setGender(gender);
                                                socialMediaModel.setDob(birthday);
                                                socialMediaModel.setUserId(id);
                                                socialMediaModel.setProfilePic(profile_pic.toString());

                                                socialMediaGetDataInterface.saveSocialMediaData(socialMediaModel);

                                                LoginManager.getInstance().logOut();


                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields",
                                "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {

                        FacebookSdk.sdkInitialize(activity.getApplicationContext());
                        LoginManager.getInstance().logOut();
                        Toast.makeText(activity, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        FacebookSdk.sdkInitialize(activity.getApplicationContext());
                        LoginManager.getInstance().logOut();
                        Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }


                });

    }


    private String changeDOB(String dob) {
        SimpleDateFormat sdfSource = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null;
        try {
            date = sdfSource.parse(dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdfDestination = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdfDestination.format(date);
        return strDate;




    }


    public void printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            // getting application package name, as defined in manifest
            String packageName = context.getApplicationContext()
                    .getPackageName();

            // Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext()
                    .getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e(TAG, key);
            }
        } catch (NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }
}
