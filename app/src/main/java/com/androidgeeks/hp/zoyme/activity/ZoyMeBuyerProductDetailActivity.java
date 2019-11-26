package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.adapter.AddressAdapter;
import com.androidgeeks.hp.zoyme.adapter.BuyerProductAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.AddressDetailModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.AddressListClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.androidgeeks.hp.zoyme.AppController.getContext;

public class ZoyMeBuyerProductDetailActivity extends AppCompatActivity implements AddressListClick {
    private TextView add_products;
    private RecyclerView buyerproductList;
    private TextView fragment_buyer_tv_no_records;
    private SharedPreferenceManager sharedPreferenceManager;
    private ProgressDialog progressDialog;
    private ImageView toolbar_iv_back;
    List<AddressDetailModel> myAddressList;
    BuyerProductAdapter buyerProductAdapter;
    int mDeletedposition;
    private int mSelectedPosition;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_buyer_product_detail);
        add_products = (TextView) findViewById(R.id.add_products);
        buyerproductList = (RecyclerView) findViewById(R.id.buyerproductList);
        fragment_buyer_tv_no_records = (TextView) findViewById(R.id.fragment_buyer_tv_no_records);

        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        buyerproductList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        buyerproductList.setLayoutManager(layoutManager);

        myAddressList = new ArrayList<>();

        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //id = getIntent().getStringExtra("id");

        add_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginIntent = new Intent(ZoyMeBuyerProductDetailActivity.this, ZoyMeBuyerProductCreateActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeBuyerProductDetailActivity.this);
        if (Utils.getConnectivityStatusVal(ZoyMeBuyerProductDetailActivity.this)) {
            progressDialog = new ProgressDialog(ZoyMeBuyerProductDetailActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            makeJsonBuyerProductDetail();
        } else {
            CustomMethods.displayDialog(ZoyMeBuyerProductDetailActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
        }




        buyerproductList.addOnItemTouchListener(new ZoyMeAddressActivity.RecyclerTouchListener(ZoyMeBuyerProductDetailActivity.this, buyerproductList, new ZoyMeAddressActivity.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                id = myAddressList.get(position).getId();



            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }


    private void makeJsonBuyerProductDetail() {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_SELLER_PRODUCT, params, addressDetailResponseListener, addressDetailResponseError), "tag_sellerProduct_req");
    }


    Response.Listener<JSONObject> addressDetailResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");
                    JSONObject mJsonObject = null;
                    JSONArray jsonArrayAddress = response.getJSONArray("data");
                    if (response.getJSONArray("data").length() != 0) {

                        for (int i = 0; i < jsonArrayAddress.length(); i++) {
                            AddressDetailModel maddressModel = new AddressDetailModel();
                            mJsonObject = jsonArrayAddress.getJSONObject(i);
                            maddressModel.setId(mJsonObject.getString("id"));
                            maddressModel.setName(mJsonObject.getString("name"));
                            maddressModel.setFlat(mJsonObject.getString("price"));
                            maddressModel.setLocality(mJsonObject.getString("image_url"));
                            maddressModel.setCity(mJsonObject.getString("status_name"));


                            myAddressList.add(maddressModel);
                            fragment_buyer_tv_no_records.setVisibility(View.GONE);
                        }

                        buyerProductAdapter = new BuyerProductAdapter(ZoyMeBuyerProductDetailActivity.this, myAddressList, ZoyMeBuyerProductDetailActivity.this);
                        buyerproductList.setAdapter(buyerProductAdapter);
                        buyerProductAdapter.notifyDataSetChanged();// Notify the adapter*/
                    } else {
                        fragment_buyer_tv_no_records.setVisibility(View.VISIBLE);
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBuyerProductDetailActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBuyerProductDetailActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeBuyerProductDetailActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    private Response.ErrorListener addressDetailResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());

            if (error.networkResponse != null) {

                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Toast.makeText(ZoyMeBuyerProductDetailActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(ZoyMeBuyerProductDetailActivity.this, getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void setAppDeletePostion(String mFlag, Object mObject, int mPosition) {
        mDeletedposition = mPosition;

        final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBuyerProductDetailActivity.this).create();
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setCancelable(false);

        alertDialog.setMessage("Are you sure you want to deactivate this product ?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBuyerProductDetailActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                makeJsonDelete(mDeletedposition);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, ZoyMeBuyerProductDetailActivity.this.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (!((Activity) ZoyMeBuyerProductDetailActivity.this).isFinishing()) {

            alertDialog.show();
        }
    }

    private void makeJsonDelete(int position) {
        this.mSelectedPosition = position;
        Map<String, String> params = new HashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("id", myAddressList.get(position).getId());


        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_PRODUCT_DELETE, params, deleteResponseListener, deleteResponseError), "tag_delete_req");

    }

    Response.Listener<JSONObject> deleteResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());


            try {
                progressDialog.hide();

                String message = response.getString("message");
                JSONObject mJsonObject = null;
                JSONArray jsonArrayAddress = response.getJSONArray("data");
                if (response.getJSONArray("data").length() != 0) {

                    for (int i = 0; i < jsonArrayAddress.length(); i++) {
                        AddressDetailModel addressDetailModel = myAddressList.get(mSelectedPosition);
                        mJsonObject = jsonArrayAddress.getJSONObject(i);
                        addressDetailModel.setCity(mJsonObject.getString("status_name"));

                        myAddressList.set(mSelectedPosition, addressDetailModel);
                    }
                    buyerProductAdapter.notifyDataSetChanged();// Notify the adapter*/


                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBuyerProductDetailActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBuyerProductDetailActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeBuyerProductDetailActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    Response.ErrorListener deleteResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };

    @Override
    public void setAppUpdatePostion(String mFlag, Object mObject, int mPosition) {
        Intent mIntent = new Intent(ZoyMeBuyerProductDetailActivity.this, ZoyMeBuyerProductCreateActivity.class);
        mIntent.putExtra("id", myAddressList.get(mPosition).getId());
        mIntent.putExtra("edit_buyer","Edit");
        startActivity(mIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }


}
