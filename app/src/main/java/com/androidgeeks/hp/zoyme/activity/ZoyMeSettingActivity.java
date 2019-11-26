package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZoyMeSettingActivity extends AppCompatActivity {

    private TextView datepicker, cpassword;
    private EditText first_name;
    private EditText last_name, mobile_et_setting;
    private Button submit_detail;
    private LinearLayout change_layout;
    private TextView toolbar_tv_title;
    private ImageView toolbar_iv_back, toolbar_iv_cart;
    SharedPreferenceManager sharedPreferenceManager;
    private ProgressDialog progressDialog;
    private String strDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_setting);

        cpassword = (TextView) findViewById(R.id.cpassword);
        cpassword.setPaintFlags(cpassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        datepicker = (TextView) findViewById(R.id.datepicker);
        first_name = (EditText) findViewById(R.id.first_name);
        submit_detail = (Button) findViewById(R.id.submit_detail);
        last_name = (EditText) findViewById(R.id.last_name);
        mobile_et_setting = (EditText) findViewById(R.id.mobile_et_setting);


        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeSettingActivity.this);
        first_name.setText(sharedPreferenceManager.getFirstName());
        last_name.setText(sharedPreferenceManager.getLastName());
        datepicker.setText(sharedPreferenceManager.getDob());

        mobile_et_setting.setText(sharedPreferenceManager.getMobile());
        progressDialog = new ProgressDialog(ZoyMeSettingActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_cart = (ImageView) findViewById(R.id.toolbar_iv_cart);

        change_layout = (LinearLayout) findViewById(R.id.change_layout);
        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);
        readyToolBar("Setting");
        datepicker.setOnClickListener(new View.OnClickListener() {


            final Calendar c = Calendar.getInstance();
            final int myr = c.get(Calendar.YEAR);
            final int mmonth = c.get(Calendar.MONTH);
            final int mdate = c.get(Calendar.DATE);

            @Override
            public void onClick(View view) {
                CustomMethods.hideSoftKeyBoard(ZoyMeSettingActivity.this, view);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(ZoyMeSettingActivity.this, onDateSetListener, myr, mmonth, mdate);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }

            final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    final int month = monthOfYear + 1;
                    datepicker.setText(String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(dayOfMonth));
                    // strDob = (String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(dayOfMonth));
                }
            };

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
                Intent mIntent = new Intent(ZoyMeSettingActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        cpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeSettingActivity.this, ZoyMeChangePasswordActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        submit_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.getConnectivityStatusVal(ZoyMeSettingActivity.this)) {
                    progressDialog.show();

                    makeJsonUpdate();

                } else {
                    CustomMethods.displayDialog(ZoyMeSettingActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
                }
            }
        });

    }

    private void makeJsonUpdate() {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("first_name", first_name.getText().toString());
        params.put("last_name", last_name.getText().toString());
        params.put("dob", datepicker.getText().toString());
        params.put("mobile", mobile_et_setting.getText().toString());

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_USER_UPDATE, params, updateResponseListener, updateResponseError), "tag_update_req");
    }


    Response.Listener<JSONObject> updateResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {

                    String message = response.getString("message");
                    JSONObject jsonObjectdata = response.getJSONObject("data");
                    sharedPreferenceManager.setFirstName(jsonObjectdata.getString("first_name"));
                    sharedPreferenceManager.setLastName(jsonObjectdata.getString("last_name"));
                    sharedPreferenceManager.setMobile(jsonObjectdata.getString("mobile"));
                    sharedPreferenceManager.setDob(jsonObjectdata.getString("dob"));

                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeSettingActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(message);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeSettingActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeSettingActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeSettingActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeSettingActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeSettingActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    Response.ErrorListener updateResponseError = new Response.ErrorListener() {
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
