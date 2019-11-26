package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.FieldValidationHelper;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZoyMeChangePasswordActivity extends AppCompatActivity {
    private EditText old_password_et, new_password_et, confirm_password_et;
    private Button submit_detail;
    private ProgressDialog progressDialog;
    SharedPreferenceManager sharedPreferenceManager;
    private TextView toolbar_tv_title;
    private ImageView toolbar_iv_back, toolbar_iv_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_change_password);

        old_password_et = (EditText) findViewById(R.id.old_password_et);
        new_password_et = (EditText) findViewById(R.id.new_password_et);
        confirm_password_et = (EditText) findViewById(R.id.confirm_password_et);
        submit_detail = (Button) findViewById(R.id.submit_detail);


        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_cart = (ImageView) findViewById(R.id.toolbar_iv_cart);

        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);
        readyToolBar("Change Password");

        progressDialog = new ProgressDialog(ZoyMeChangePasswordActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeChangePasswordActivity.this);
        submit_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFormsFields();
            }
        });

        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar_iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeChangePasswordActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


    }

    private void validateFormsFields() {
        String pass = new_password_et.getText().toString();
        String cpass = confirm_password_et.getText().toString();
        if ((new_password_et.getText().toString().trim().length() < 6)) {
            new_password_et.requestFocus();
            new_password_et.setError("Password must be greater than 6 digit");

        } else if (!FieldValidationHelper.isPasswordMatching(pass, cpass)) {
            confirm_password_et.requestFocus();
            confirm_password_et.setError("Password Not matching");
        } else {

            if (Utils.getConnectivityStatusVal(ZoyMeChangePasswordActivity.this)) {
                progressDialog.show();
                makeJsonObjChangePassword();
            } else {

                CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));

            }
        }
    }

    private void makeJsonObjChangePassword() {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("old_password", old_password_et.getText().toString());
        params.put("password", new_password_et.getText().toString());
        params.put("c_password", confirm_password_et.getText().toString());

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_PASSWORD_UPDATE, params, passwordResponseListener, passwordResponseError), "tag_password_req");
    }

    Response.Listener<JSONObject> passwordResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {

                    String message = response.getString("message");


                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeChangePasswordActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(message);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeChangePasswordActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Intent loginIntent = new Intent(ZoyMeChangePasswordActivity.this, ZoyMeLoginActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                            finish();

                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeChangePasswordActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeChangePasswordActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeChangePasswordActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeChangePasswordActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    Response.ErrorListener passwordResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };

    private void readyToolBar(String tittle) {

        toolbar_tv_title.setText(tittle);


    }

}
