package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.AddressDetailModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZoyMeAddAddressActivity extends AppCompatActivity {
    private TextView toolbar_tv_title, location;
    private LinearLayout current_location_layout;
    private EditText flat_no, state, phone;
    private EditText area, city, pincode, name, alternate_phone;
    private Button save;
    private ProgressDialog progressDialog;
    private SharedPreferenceManager sharedPreferenceManager;
    private ImageView toolbar_iv_back, toolbar_iv_cart;
    String id, mposition, addressid, edit_address;
    List<AddressDetailModel> myAddressList;
    Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_add_address);
        update = (Button) findViewById(R.id.update);

        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);
        current_location_layout = (LinearLayout) findViewById(R.id.current_location_layout);
        location = (TextView) findViewById(R.id.location);
        flat_no = (EditText) findViewById(R.id.flat_no);
        area = (EditText) findViewById(R.id.area);
        city = (EditText) findViewById(R.id.city);
        state = (EditText) findViewById(R.id.state);
        pincode = (EditText) findViewById(R.id.pincode);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        alternate_phone = (EditText) findViewById(R.id.alternate_phone);
        save = (Button) findViewById(R.id.save);

        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_cart = (ImageView) findViewById(R.id.toolbar_iv_cart);


        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeAddAddressActivity.this);

        myAddressList = new ArrayList<>();
        /*mPosition = getIntent().getExtras().getInt("mPosition");*/

        // Toast.makeText(this, myAddressList.get(mPosition).getName(), Toast.LENGTH_SHORT).show();


        addressid = getIntent().getStringExtra("id");
        edit_address = getIntent().getStringExtra("edit_address");
        readyToolBar(edit_address);

        if (addressid != null) {
            flat_no.setText(sharedPreferenceManager.getFlat());
            area.setText(sharedPreferenceManager.getLocality());
            city.setText(sharedPreferenceManager.getCity());
            state.setText(sharedPreferenceManager.getState());
            pincode.setText(sharedPreferenceManager.getPincode());
            name.setText(sharedPreferenceManager.getName());
            phone.setText(sharedPreferenceManager.getPhone());
            alternate_phone.setText(sharedPreferenceManager.getAlternatephone());

            update.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    progressDialog = new ProgressDialog(ZoyMeAddAddressActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Please wait...");
                    makeJsonUpdateAddress();

                }
            });


        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(ZoyMeAddAddressActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Please wait...");
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
                Intent mIntent = new Intent(ZoyMeAddAddressActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


    }

    private void readyToolBar(String tittle) {

        toolbar_tv_title.setText(tittle);


    }


    private void validateFormsFields() {

        if (flat_no.getText().toString().trim().isEmpty()) {
            CustomMethods.displayDialog(ZoyMeAddAddressActivity.this, getString(R.string.app_name), "Please Enter Flat Number");
            flat_no.requestFocus();
        } else if (area.getText().toString().trim().isEmpty()) {
            CustomMethods.displayDialog(ZoyMeAddAddressActivity.this, getString(R.string.app_name), "Please Enter Area or Street");
            area.requestFocus();
        } else if (city.getText().toString().trim().isEmpty()) {
            CustomMethods.displayDialog(ZoyMeAddAddressActivity.this, getString(R.string.app_name), "Please Enter City");
            city.requestFocus();
        } else if (state.getText().toString().trim().isEmpty()) {
            CustomMethods.displayDialog(ZoyMeAddAddressActivity.this, getString(R.string.app_name), "Please Enter State");
            state.requestFocus();
        } else if ((pincode.getText().toString().trim().length() != 6)) {
            CustomMethods.displayDialog(ZoyMeAddAddressActivity.this, getString(R.string.app_name), "Please Enter Pincode");
            pincode.requestFocus();
        } else if ((name.getText().toString().trim().isEmpty())) {
            CustomMethods.displayDialog(ZoyMeAddAddressActivity.this, getString(R.string.app_name), "Please Enter User Name");
            name.requestFocus();
        } else if (phone.getText().toString().trim().isEmpty()) {
            CustomMethods.displayDialog(ZoyMeAddAddressActivity.this, getString(R.string.app_name), "Please Enter Mobile Number");
            phone.requestFocus();
        } else if (phone.length() != 10) {
            CustomMethods.displayDialog(ZoyMeAddAddressActivity.this, getString(R.string.app_name), "Please enter your 10 digit Mobile No.");
            phone.requestFocus();
        } else {
            if (Utils.getConnectivityStatusVal(ZoyMeAddAddressActivity.this)) {
                progressDialog.show();
                makeJsonCreateAddress();
            } else {
                CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));
            }
        }


    }

    private void makeJsonUpdateAddress() {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("flat", flat_no.getText().toString().trim());
        params.put("locality", area.getText().toString().trim());
        params.put("city", city.getText().toString().trim());
        params.put("state", state.getText().toString().trim());
        params.put("pincode", pincode.getText().toString().trim());
        params.put("name", name.getText().toString().trim());
        params.put("phone", phone.getText().toString().trim());
        params.put("alternate_phone", alternate_phone.getText().toString().trim());
        params.put("id", addressid);
        //  params.put("id", sharedPreferenceManager.getUserid());


        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_UPDATE, params, createResponseListener, createResponseError), "tag_create_address_req");
    }

    private void makeJsonCreateAddress() {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("flat", flat_no.getText().toString().trim());
        params.put("locality", area.getText().toString().trim());
        params.put("city", city.getText().toString().trim());
        params.put("state", state.getText().toString().trim());
        params.put("pincode", pincode.getText().toString().trim());
        params.put("name", name.getText().toString().trim());
        params.put("phone", phone.getText().toString().trim());
        params.put("alternate_phone", alternate_phone.getText().toString().trim());


        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_ADDRESS_CREATE, params, createResponseListener, createResponseError), "tag_create_address_req");
    }


    Response.Listener<JSONObject> createResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");

                    JSONObject jsonObject = response.getJSONObject("data");

                    mposition = jsonObject.getString("id");

                    sharedPreferenceManager.setFlat(jsonObject.getString("flat"));
                    sharedPreferenceManager.setLocality(jsonObject.getString("locality"));
                    sharedPreferenceManager.setCity(jsonObject.getString("city"));
                    sharedPreferenceManager.setState(jsonObject.getString("state"));
                    sharedPreferenceManager.setPincode(jsonObject.getString("pincode"));
                    sharedPreferenceManager.setName(jsonObject.getString("name"));
                    sharedPreferenceManager.setPhone(jsonObject.getString("phone"));
                    sharedPreferenceManager.setAlternatephone(jsonObject.getString("alternate_phone"));
                    sharedPreferenceManager.setUserid(jsonObject.getString("user_id"));
                    sharedPreferenceManager.setId(jsonObject.getString("id"));


                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeAddAddressActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(message);

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeAddAddressActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            Intent loginIntent = new Intent(ZoyMeAddAddressActivity.this, ZoyMeCartActivity.class);
                            loginIntent.putExtra("id", mposition);
                            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginIntent);
                            finish();
                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeAddAddressActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeAddAddressActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeAddAddressActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeAddAddressActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    Response.ErrorListener createResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };


}
