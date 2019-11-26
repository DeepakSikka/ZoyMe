package com.androidgeeks.hp.zoyme.activity;

/*
Created By Deepak SIkka on 25/10/2017

 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.helper.FBLoginActivity;
import com.androidgeeks.hp.zoyme.model.SocialMediaModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.FieldValidationHelper;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.SocialMediaGetDataInterface;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class ZoyMeRegistrationActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, SocialMediaGetDataInterface {
    private EditText username_et, email_et;
    private EditText mobile_et, password_et, confirm_password_et;
    private ImageButton login_btn;
    private TextView signIn_tv;
    private ImageView fb_login_bt, twitter_login_bt, google_login_bt;
    private SharedPreferenceManager sharedPreferenceManager;
    private ProgressDialog progressDialog;
    private String tag_registration_req = "tag_registration_req";

    /* Any number for uniquely distinguish your request */
    public static final int WEBVIEW_REQUEST_CODE = 202;

    private String consumerKey = "r3zNu0oWPIpnZvB34nCXEYSCx";
    private String consumerSecret = "sls4iRJffVN9TDd06NC7dMshufjhwHxfoOpj0O5dY4qxYuflbA";
    private static Twitter twitter;
    private static RequestToken requestToken;

    /* Google  */
    private GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 121;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_registration);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        initLayout();
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        new FBLoginActivity().inItFacebook(ZoyMeRegistrationActivity.this, callbackManager, ZoyMeRegistrationActivity.this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    /**
     * Init layout xml layout
     */
    private void initLayout() {
        username_et = (EditText) findViewById(R.id.username_et);
        email_et = (EditText) findViewById(R.id.email_et);
        password_et = (EditText) findViewById(R.id.password_et);
        mobile_et = (EditText) findViewById(R.id.mobile_et);
        confirm_password_et = (EditText) findViewById(R.id.confirm_password_et);
        login_btn = (ImageButton) findViewById(R.id.login_btn);
        signIn_tv = (TextView) findViewById(R.id.signIn_tv);
        google_login_bt = (ImageView) findViewById(R.id.google_login_bt);
        fb_login_bt = (ImageView) findViewById(R.id.fb_login_bt);
        twitter_login_bt = (ImageView) findViewById(R.id.twitter_login_bt);

        login_btn.setOnClickListener(this);
        fb_login_bt.setOnClickListener(this);
        google_login_bt.setOnClickListener(this);
        twitter_login_bt.setOnClickListener(this);
        signIn_tv.setOnClickListener(this);


        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeRegistrationActivity.this);
        progressDialog = new ProgressDialog(ZoyMeRegistrationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }

    /**
     * @param v
     * @login_btn
     * @fb_login_bt
     * @google_login_bt
     * @twitter_login_bt
     * @signIn_tv
     */
    @Override
    public void onClick(View v) {
        if (Utils.getConnectivityStatusVal(ZoyMeRegistrationActivity.this)) {
            if (v == login_btn) {

                validateFormsFields();

            } else if (v == fb_login_bt) {
                Toast.makeText(this, "facebook", Toast.LENGTH_SHORT).show();
                LoginManager.getInstance().logInWithReadPermissions(ZoyMeRegistrationActivity.this, Arrays.asList("user_photos", "email", "user_birthday",
                        "public_profile"));
            } else if (v == google_login_bt) {
                signIn();
            } else if (v == twitter_login_bt) {
                loginAsTwitter();
            } else if (v == signIn_tv) {
                Intent registerIntent = new Intent(ZoyMeRegistrationActivity.this, ZoyMeLoginActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(registerIntent);
                finish();


            }
        } else {
            CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));
        }

    }


    private void validateFormsFields() {
        String pass = password_et.getText().toString();
        String cpass = confirm_password_et.getText().toString();
        if (!FieldValidationHelper.isValidName(username_et.getText().toString().trim())) {
            username_et.requestFocus();
            username_et.setError("Please enter valid username");
        } else if (!FieldValidationHelper.isValidEmail(email_et.getText().toString().trim())) {
            email_et.requestFocus();
            email_et.setError("Please enter valid email address");
        } else if (!FieldValidationHelper.isValidPhoneNumber(mobile_et.getText().toString().trim())) {
            mobile_et.requestFocus();
            mobile_et.setError("Please enter valid phone number");
        } else if ((password_et.getText().toString().trim().length() < 6)) {
            password_et.requestFocus();
            password_et.setError("Password must be greater than 6 digit");

        } else if (!FieldValidationHelper.isPasswordMatching(pass, cpass)) {
            confirm_password_et.requestFocus();
            confirm_password_et.setError("Password Not matching");
        } else {

            if (Utils.getConnectivityStatusVal(ZoyMeRegistrationActivity.this)) {

                HashMap<String, String> params = new HashMap<>();
                params.put("username", username_et.getText().toString().trim());
                params.put("password", password_et.getText().toString().trim());
                params.put("cpassword", confirm_password_et.getText().toString().trim());
                params.put("email", email_et.getText().toString().trim());
                params.put("mobile", mobile_et.getText().toString().trim());


                progressDialog.show();
                makeJsonObjReq(params);
            } else {

                CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));

            }
        }
    }

    private void makeSocialMediaLoginRequest(String firstName, String lastName, String email, String gender, String userId, String imageuri, String loginType, String dob) {

        //  hideSoftKeyboard();
        progressDialog = new ProgressDialog(ZoyMeRegistrationActivity.this);
        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, String> params = new LinkedHashMap<>();
        params.put("islogin", loginType);
        params.put("email", email);
        params.put("first_name", firstName);
        params.put("last_name", lastName);


        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST,
                UrlConstants.ZOY_ME_LOGIN, params, loginResponseListener,
                loginResponseError), "tag_user_social_login_req");
    }

    Response.Listener<JSONObject> loginResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    sharedPreferenceManager.setUserId(response.getString("user_id"));
                    sharedPreferenceManager.setToken(response.getString("token"));
                    sharedPreferenceManager.setRememberMe(true);
                    sharedPreferenceManager.setFirstName(response.getString("first_name"));
                    sharedPreferenceManager.setLastName(response.getString("last_name"));
                    sharedPreferenceManager.setMobile(response.getString("mobile"));

                    String message = response.getString("message");

                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeRegistrationActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(message);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeRegistrationActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            Intent loginIntent = new Intent(ZoyMeRegistrationActivity.this, ZoyMeHomeActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                            finish();
                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeRegistrationActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeRegistrationActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeRegistrationActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeRegistrationActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    Response.ErrorListener loginResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };

    private void makeJsonObjReq(HashMap<String, String> params) {


        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_REGISTRATION, params, LoginReponseListener,
                LoginErrorListener), tag_registration_req);

    }

    Response.Listener<JSONObject> LoginReponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            Log.d("response", response.toString());
            try {
                progressDialog.hide();

                if (response.getInt("status") == 201) {

                    sharedPreferenceManager.setUserId(response.getString("user_id"));

                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeRegistrationActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeRegistrationActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            Intent loginIntent = new Intent(ZoyMeRegistrationActivity.this, ZoyMeLoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                            finish();
                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeRegistrationActivity.this).isFinishing()) {

                        alertDialog.show();
                    }


                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeRegistrationActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeRegistrationActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeRegistrationActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }
            } catch (JSONException e) {

                e.printStackTrace();
                Log.i("Volley Error", e.toString());

            }


        }
    };
    Response.ErrorListener LoginErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("error", error.toString());


        }
    };

    private void signIn() {

        readyGoogleSignIn();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // ****************** Google Login Start************************


    private void readyGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 201) {
            // HomeScreen.login=0;
            SocialMediaModel socialMediaModel = data
                    .getParcelableExtra("socialModel");
            if (socialMediaModel != null) {
                String firstName = socialMediaModel.getFirstName();
                String lastName = socialMediaModel.getLastName();
                String email = socialMediaModel.getEmail();
                String gender = socialMediaModel.getGender();
                String userId = socialMediaModel.getUserId();
                String imageuri = socialMediaModel.getProfilePic();
                String dob = socialMediaModel.getDob();

                makeSocialMediaLoginRequest(firstName, lastName, email, gender, userId, imageuri, "google", dob);

            }


        }
        if (resultCode == 202) {
            String verifier = data.getExtras().getString(getString(R.string.twitter_oauth_verifier));
            try {
                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

                long userID = accessToken.getUserId();
                final User user = twitter.showUser(userID);
                String username = user.getName();
                user.getProfileImageURL();

                String first_name = username.substring(0, username.indexOf(" "));
                String last_name = username.substring(username.indexOf(" ") + 1, username.length());

                makeSocialMediaLoginRequest(first_name, last_name, "", "f", userID + "", user.getProfileImageURL(), "twitter", "");

            } catch (Exception e) {
                Log.e("Twitter Login Failed", e.getMessage());
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);

            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }

        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("GOOGLE", "handleSignInResult:" + result.isSuccess());
        signOut();
        mGoogleApiClient.stopAutoManage(ZoyMeRegistrationActivity.this);
        mGoogleApiClient.disconnect();
        if (result.isSuccess()) {
            String pic = "";
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            Uri personPhotoUrl = acct.getPhotoUrl();
            String firstName = acct.getGivenName();
            String lastName = acct.getFamilyName();
            // int gender = acct.get;
            String userId = acct.getId();
            String email = acct.getEmail();

            //  signOut();

            if (personPhotoUrl != null) {
                pic = personPhotoUrl.toString();
            }

            makeSocialMediaLoginRequest(firstName, lastName, email, "f", userId, pic, "google", "");


        } else {

            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });

    }

    private void loginAsTwitter() {
        final ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setDebugEnabled(true);
        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);


        final Configuration configuration = builder.build();
        final TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();

        try {
            requestToken = twitter.getOAuthRequestToken(new UrlConstants().callbackUrl);
            /**
             *  Loading twitter login page on webview for authorization
             *  Once authorized, results are received at onActivityResult
             *  */
            final Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
            startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

        } catch (TwitterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Twitter_Exception", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("TAG", "Permission: " + permissions[0] + "was " + grantResults[0]);


            signIn();

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(ZoyMeRegistrationActivity.this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveSocialMediaData(SocialMediaModel socialMediaModel) {
        if (socialMediaModel.getEmail().equalsIgnoreCase("")) {
            Toast.makeText(ZoyMeRegistrationActivity.this, "email not getting", Toast.LENGTH_SHORT).show();
        } else {
            makeSocialMediaLoginRequest(socialMediaModel.getFirstName(), socialMediaModel.getLastName(), socialMediaModel.getEmail(), socialMediaModel.getGender(),
                    socialMediaModel.getUserId(), socialMediaModel.getProfilePic(), "facebook", socialMediaModel.getDob());

        }
    }


}
