package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.androidgeeks.hp.zoyme.adapter.ZoymeProductListAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.ProductListingModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ProductList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZoyMeProductListActivity extends AppCompatActivity implements ProductList {
    private RecyclerView productList;
    private LinearLayoutManager mProductLinearLayoutManager;
    private String tittle, offerbanner;
    private String id;
    private ZoymeProductListAdapter zoymeProductListAdapter;
    private ProgressDialog progressDialog;
    private TextView tvNoRecords;
    List<ProductListingModel> myProductList;
    private SharedPreferenceManager sharedPreferenceManager;
    private TextView toolbar_tv_title;
    private ImageView toolbar_iv_back;
    private ImageView toolbar_iv_cart;
    private int mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_product_list);
        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);

        productList = (RecyclerView) findViewById(R.id.productList);
        mProductLinearLayoutManager = new GridLayoutManager(ZoyMeProductListActivity.this, 2);
        tvNoRecords = (TextView) findViewById(R.id.fragment_product_tv_no_records);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_cart = (ImageView) findViewById(R.id.toolbar_iv_cart);

        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeProductListActivity.this);
        myProductList = new ArrayList<>();


        id = getIntent().getStringExtra("id");
        tittle = getIntent().getStringExtra("tittle");
        offerbanner = getIntent().getStringExtra("offerbanner");
        readyToolBar(tittle);

        if (offerbanner != null) {
            if (Utils.getConnectivityStatusVal(ZoyMeProductListActivity.this)) {
                progressDialog = new ProgressDialog(ZoyMeProductListActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                makeJsonCategoryDetail(UrlConstants.ZOY_ME_OFFER_BANNNER_SHOW);
            } else {
                CustomMethods.displayDialog(ZoyMeProductListActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
            }
        } else if (offerbanner == null) {

            if (Utils.getConnectivityStatusVal(ZoyMeProductListActivity.this)) {
                progressDialog = new ProgressDialog(ZoyMeProductListActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                makeJsonCategoryDetail(UrlConstants.ZOY_ME_CATEGORY);
            } else {
                CustomMethods.displayDialog(ZoyMeProductListActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
            }

        }

        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar_iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeProductListActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    private void readyToolBar(String tittle) {

        toolbar_tv_title.setText(tittle);


    }

    private void makeJsonCategoryDetail(String url) {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", id);
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, url, params, categoryDetailResponseListener, categoryDetailResponseError), "tag_categoryDetail_req");
    }


    Response.Listener<JSONObject> categoryDetailResponseListener = new Response.Listener<JSONObject>() {
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
                            ProductListingModel mproductListingModel = new ProductListingModel();
                            mJsonObject = jsonArrayProducts.getJSONObject(i);
                            mproductListingModel.setProduct_id(mJsonObject.getString("product_id"));
                            mproductListingModel.setName(mJsonObject.getString("name"));
                            mproductListingModel.setDescription(mJsonObject.getString("description"));
                            mproductListingModel.setImage_url(mJsonObject.getString("image_url"));
                            mproductListingModel.setCategory_id(mJsonObject.getString("category_id"));
                            mproductListingModel.setPrice(mJsonObject.getString("price"));
                            mproductListingModel.setRating(mJsonObject.getString("rating"));
                            mproductListingModel.setNew_price(mJsonObject.getString("new_price"));
                            mproductListingModel.setIs_like(mJsonObject.getString("is_like"));

                            myProductList.add(mproductListingModel);
                            tvNoRecords.setVisibility(View.GONE);
                        }
                        zoymeProductListAdapter = new ZoymeProductListAdapter(ZoyMeProductListActivity.this, myProductList, ZoyMeProductListActivity.this);
                        productList.setLayoutManager(mProductLinearLayoutManager);
                        productList.setAdapter(zoymeProductListAdapter);
                        zoymeProductListAdapter.notifyDataSetChanged();// Notify the adapter*/
                    } else {
                        tvNoRecords.setVisibility(View.VISIBLE);
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeProductListActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeProductListActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeProductListActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    private Response.ErrorListener categoryDetailResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());

            if (error.networkResponse != null) {

                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Toast.makeText(ZoyMeProductListActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(ZoyMeProductListActivity.this, getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void subProductPosition(String mFlag, Object mObject, int mPosition, String mId, String mTittle) {
        Intent mIntent = new Intent(ZoyMeProductListActivity.this, ZoyMeProductDetailActivity.class);
        mIntent.putExtra("tittle", mTittle);
        mIntent.putExtra("id", mId);
        startActivity(mIntent);

    }

    @Override
    public void subWishlistPosition(String mFlag, Object mObject, int mPosition, String mId, ImageView mClick) {


        makeJsonLike(mPosition);


    }

    private void makeJsonLike(int position) {
        this.mSelectedPosition = position;
        Map<String, String> params = new HashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("id", myProductList.get(position).getProduct_id());


        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_WISHLIST, params, likeResponseListener, likeResponseError), "tag_like_req");

    }

    Response.Listener<JSONObject> likeResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {


                    String message = response.getString("message");
                    int is_like = response.getInt("is_like");
                    ProductListingModel productListingModel = myProductList.get(mSelectedPosition);
                    productListingModel.setIs_like(response.getString("is_like"));

                    zoymeProductListAdapter.notifyDataSetChanged();
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeProductListActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeProductListActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeProductListActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    Response.ErrorListener likeResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };


}
