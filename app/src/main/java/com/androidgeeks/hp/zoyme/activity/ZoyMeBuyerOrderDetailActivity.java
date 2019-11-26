package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.androidgeeks.hp.zoyme.adapter.OrderAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.OrderModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.OrderSubmitClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.androidgeeks.hp.zoyme.AppController.getContext;

public class ZoyMeBuyerOrderDetailActivity extends AppCompatActivity implements OrderSubmitClick {
    private RecyclerView orderproductList;
    private TextView fragment_buyer_tv_no_records;
    private ImageView toolbar_iv_back;
    private SharedPreferenceManager sharedPreferenceManager;
    private ProgressDialog progressDialog;
    List<OrderModel> orderModels;
    private OrderAdapter orderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_buyer_order_detail);
        orderproductList = (RecyclerView) findViewById(R.id.orderproductList);
        fragment_buyer_tv_no_records =(TextView)findViewById(R.id.fragment_buyer_tv_no_records);
        toolbar_iv_back =(ImageView)findViewById(R.id.toolbar_iv_back);
        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeBuyerOrderDetailActivity.this);
        orderModels = new ArrayList<>();
        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        orderproductList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        orderproductList.setLayoutManager(layoutManager);

        if (Utils.getConnectivityStatusVal(ZoyMeBuyerOrderDetailActivity.this)) {
            progressDialog = new ProgressDialog(ZoyMeBuyerOrderDetailActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            makeJsonSellerOrderDetail();
        } else {
            CustomMethods.displayDialog(ZoyMeBuyerOrderDetailActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
        }
    }
    private void makeJsonSellerOrderDetail() {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_SELLER_ORDER, params, orderDetailResponseListener, orderDetailResponseError), "tag_orderDetail_req");
    }

    Response.Listener<JSONObject> orderDetailResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");
                    JSONObject mJsonObject = null;
                    JSONArray jsonArrayProducts = response.getJSONArray("data");
                    if (response.getJSONArray("data").length() != 0) {

                        for (int i = 0; i < jsonArrayProducts.length(); i++) {
                            OrderModel morderModel = new OrderModel();
                            mJsonObject = jsonArrayProducts.getJSONObject(i);
                            morderModel.setId(mJsonObject.getString("id"));
                            morderModel.setName(mJsonObject.getString("name"));
                            morderModel.setImage_url(mJsonObject.getString("image_url"));
                            morderModel.setStatus_name(mJsonObject.getString("status_name"));
                            morderModel.setRating(mJsonObject.getString("rating"));
                            morderModel.setProduct_id(mJsonObject.getString("product_id"));

                            orderModels.add(morderModel);
                            fragment_buyer_tv_no_records.setVisibility(View.GONE);
                        }
                        orderAdapter = new OrderAdapter(ZoyMeBuyerOrderDetailActivity.this, orderModels, ZoyMeBuyerOrderDetailActivity.this);
                        orderproductList.setAdapter(orderAdapter);
                        orderAdapter.notifyDataSetChanged();// Notify the adapter*/
                    } else {
                        fragment_buyer_tv_no_records.setVisibility(View.VISIBLE);
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeBuyerOrderDetailActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeBuyerOrderDetailActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeBuyerOrderDetailActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    private Response.ErrorListener orderDetailResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());

            if (error.networkResponse != null) {

                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Toast.makeText(ZoyMeBuyerOrderDetailActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(ZoyMeBuyerOrderDetailActivity.this, getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void subOrderClick(String mFlag, Object mObject, int mPosition, String ratingalue) {

    }
}
