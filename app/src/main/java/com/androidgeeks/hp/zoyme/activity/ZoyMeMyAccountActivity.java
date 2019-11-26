package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ZoyMeMyAccountActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private SharedPreferenceManager sharedPreferenceManager;
    private ImageView order_layout;
    private ImageView review_layout;
    private ImageView address_layout;
    private ImageView setting_layout;
    private Button logout_layout;
    private TextView user;
    private TextView mobile;
    private ImageView toolbar_iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_my_account);


        user = (TextView) findViewById(R.id.user);
        mobile = (TextView) findViewById(R.id.mobile);

        order_layout = (ImageView) findViewById(R.id.order_layout);
        review_layout = (ImageView) findViewById(R.id.review_layout);
        address_layout = (ImageView) findViewById(R.id.address_layout);
        setting_layout = (ImageView) findViewById(R.id.setting_layout);
        logout_layout = (Button) findViewById(R.id.logout_layout);

        toolbar_iv_back =(ImageView)findViewById(R.id.toolbar_iv_back);

        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeMyAccountActivity.this);
        user.setText(sharedPreferenceManager.getFirstName() + " " + sharedPreferenceManager.getLastName());
        mobile.setText(sharedPreferenceManager.getMobile());
        progressDialog = new ProgressDialog(ZoyMeMyAccountActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");


        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeMyAccountActivity.this, ZoyMeSettingActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeMyAccountActivity.this, ZoyMeAddressActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }
        });
        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.getConnectivityStatusVal(ZoyMeMyAccountActivity.this)) {
                    progressDialog.show();
                    makeJsonObjLogout();
                } else {
                    CustomMethods.displayDialog(ZoyMeMyAccountActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
                }
            }
        });


        order_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeMyAccountActivity.this, ZoyMeOrderActivity.class);
                startActivity(mIntent);
              overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        review_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeMyAccountActivity.this, ZoyMeReviewActivity.class);
                startActivity(mIntent);
              overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    private void makeJsonObjLogout() {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_LOGOUT, params, logoutResponseListener, logoutResponseError), "tag_logout_req");
    }

    Response.Listener<JSONObject> logoutResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {

                    String message = response.getString("message");

                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeMyAccountActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(message);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeMyAccountActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            Intent intent = new Intent(ZoyMeMyAccountActivity.this, ZoyMeSplashScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            sharedPreferenceManager.setRememberMe(false);
                            new SharedPreferenceManager(ZoyMeMyAccountActivity.this).clearall();

                        }
                    });
                    if (!((Activity) ZoyMeMyAccountActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeMyAccountActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeMyAccountActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeMyAccountActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };


    Response.ErrorListener logoutResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };
}
