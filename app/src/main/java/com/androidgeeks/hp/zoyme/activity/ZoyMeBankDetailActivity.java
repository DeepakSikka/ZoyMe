package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

public class ZoyMeBankDetailActivity extends AppCompatActivity {
    private EditText edt_account_name;
    private EditText edt_account_number;
    private EditText edt_retype_account_number;
    private EditText edt_ifsc_code;
    private EditText edt_bank_name;
    private EditText edt_state;
    private EditText edt_city;
    private Button cancel, save_btn;
    private ImageView toolbar_iv_back;
    private ProgressDialog progressDialog;
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_bank_detail);
        edt_account_name = (EditText) findViewById(R.id.edt_account_name);
        edt_account_number = (EditText) findViewById(R.id.edt_account_number);
        edt_retype_account_number = (EditText) findViewById(R.id.edt_retype_account_number);
        edt_ifsc_code = (EditText) findViewById(R.id.edt_ifsc_code);
        edt_bank_name = (EditText) findViewById(R.id.edt_bank_name);
        edt_state = (EditText) findViewById(R.id.edt_state);
        edt_city = (EditText) findViewById(R.id.edt_city);
        save_btn = (Button) findViewById(R.id.save_btn);
        cancel = (Button) findViewById(R.id.cancel);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);

        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeBankDetailActivity.this);
        progressDialog = new ProgressDialog(ZoyMeBankDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFormsFields();
            }
        });

        if (Utils.getConnectivityStatusVal(ZoyMeBankDetailActivity.this)) {
            progressDialog.show();
            makeJsonObjBankDetail(UrlConstants.ZOY_ME_BANK);
        } else {
            CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));
        }
    }

    private void validateFormsFields() {

        if (edt_account_name.getText().toString().trim().isEmpty()) {
            CustomMethods.displayDialog(ZoyMeBankDetailActivity.this, getString(R.string.app_name), "Please enter account name");
            edt_account_name.requestFocus();
        } else if ((edt_account_number.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBankDetailActivity.this, getString(R.string.app_name), "Please enter account number");
            edt_account_number.requestFocus();
        } else if ((edt_retype_account_number.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBankDetailActivity.this, getString(R.string.app_name), "Please retype account number");
            edt_retype_account_number.requestFocus();
        } else if ((edt_ifsc_code.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBankDetailActivity.this, getString(R.string.app_name), "Please enter ifsc code");
            edt_ifsc_code.requestFocus();
        } else if ((edt_bank_name.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBankDetailActivity.this, getString(R.string.app_name), "Please enter bank name");
            edt_bank_name.requestFocus();
        } else if ((edt_state.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBankDetailActivity.this, getString(R.string.app_name), "Please enter city");
            edt_state.requestFocus();
        } else if ((edt_state.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeBankDetailActivity.this, getString(R.string.app_name), "Please enter state");
            edt_state.requestFocus();
        } else {
            if (Utils.getConnectivityStatusVal(ZoyMeBankDetailActivity.this)) {
                progressDialog.show();
                makeJsonObjBankDetail(UrlConstants.ZOY_ME_UPDATE_BANK);
            } else {
                CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));
            }
        }


    }

    private void makeJsonObjBankDetail(String url) {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("ac_holder_name", edt_account_name.getText().toString());
        params.put("ac_number", edt_account_number.getText().toString());
        params.put("retype_ac_number", edt_retype_account_number.getText().toString());
        params.put("ifsc_code", edt_ifsc_code.getText().toString());
        params.put("bank_name", edt_bank_name.getText().toString());
        params.put("state", edt_state.getText().toString());
        params.put("city", edt_city.getText().toString());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, url, params, bankResponseListener, bankResponseError), "tag_bank_req");
    }


    Response.Listener<JSONObject> bankResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");
                    JSONObject data = response.getJSONObject("data");

                    edt_account_name.setText(data.getString("ac_holder_name"));
                    edt_account_number.setText(data.getString("ac_number"));
                    edt_ifsc_code.setText(data.getString("ifsc_code"));
                    edt_bank_name.setText(data.getString("bank_name"));
                    edt_state.setText(data.getString("state"));
                    edt_city.setText(data.getString("city"));

                    if (message.equalsIgnoreCase("Successfully Updated")) {
                        final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBankDetailActivity.this).create();
                        alertDialog.setTitle(R.string.app_name);
                        alertDialog.setCancelable(false);

                        alertDialog.setMessage(response.getString("message"));
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBankDetailActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {


                                //     dialog.dismiss();

                            }
                        });
                        if (!((Activity) ZoyMeBankDetailActivity.this).isFinishing()) {

                            alertDialog.show();
                        }
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBankDetailActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBankDetailActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeBankDetailActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    Response.ErrorListener bankResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };


}
