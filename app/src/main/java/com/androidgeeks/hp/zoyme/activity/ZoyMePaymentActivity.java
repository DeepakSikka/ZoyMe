package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.adapter.CardViewAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.CartModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ZoyMePaymentActivity extends AppCompatActivity {

    private Button checkout;
    private CardView cod_click;
    private CardView payu_money_click;
    private ImageView cod_image, payu_image;
    String addressid;
    private ProgressDialog progressDialog;
    SharedPreferenceManager sharedPreferenceManager;
    String type, amount_value;
    private ImageView toolbar_iv_back;
    private TextView toolbar_tv_title;
    private TextView price_value;
    private Button amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_payment);

        checkout = (Button) findViewById(R.id.checkout);
        cod_click = (CardView) findViewById(R.id.cod_click);
        payu_money_click = (CardView) findViewById(R.id.payu_money_click);
        cod_image = (ImageView) findViewById(R.id.cod_image);
        payu_image = (ImageView) findViewById(R.id.payu_image);
        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        price_value = (TextView) findViewById(R.id.price_value);
        amount = (Button) findViewById(R.id.amount);

        sharedPreferenceManager = new SharedPreferenceManager(ZoyMePaymentActivity.this);

        addressid = getIntent().getStringExtra("id");
        amount_value = getIntent().getStringExtra("amount_value");
        amount.setText(amount_value);
        price_value.setText(amount_value);
        readyToolBar("Payment");
        cod_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cod_image.setImageResource(R.mipmap.payment_radio_selected);
                payu_image.setImageResource(R.mipmap.payment_radio);
                type = "cod";
            }
        });


        payu_money_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payu_image.setImageResource(R.mipmap.payment_radio_selected);
                cod_image.setImageResource(R.mipmap.payment_radio);
                type = "payumoney";
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (type != null) {
                    if (Utils.getConnectivityStatusVal(ZoyMePaymentActivity.this)) {
                        progressDialog = new ProgressDialog(ZoyMePaymentActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        makeJsonOrderCreate();
                    } else {
                        CustomMethods.displayDialog(ZoyMePaymentActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
                    }

                } else {
                    CustomMethods.displayDialog(ZoyMePaymentActivity.this, getString(R.string.app_name), ("Please select payment type"));

                }
            }
        });

        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void makeJsonOrderCreate() {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", addressid);
        params.put("type", type);
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_ORDER_CREATE, params, orderCreateResponseListener, orderCreateResponseError), "tag_cartUpdate_req");
    }

    Response.Listener<JSONObject> orderCreateResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");

                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMePaymentActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMePaymentActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            Intent loginIntent = new Intent(ZoyMePaymentActivity.this, ZoyMeHomeActivity.class);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(loginIntent);
                            finish();
                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMePaymentActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMePaymentActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMePaymentActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMePaymentActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    private Response.ErrorListener orderCreateResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());

            if (error.networkResponse != null) {

                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Toast.makeText(ZoyMePaymentActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(ZoyMePaymentActivity.this, getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void readyToolBar(String tittle) {

        toolbar_tv_title.setText(tittle);


    }

}
