package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.adapter.CardViewAdapter;
import com.androidgeeks.hp.zoyme.adapter.ZoymeProductListAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.AddressDetailModel;
import com.androidgeeks.hp.zoyme.model.CartModel;
import com.androidgeeks.hp.zoyme.model.ProductListingModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.CartDeleteList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ZoyMeCartActivity extends AppCompatActivity implements CartDeleteList {
    private Button address, continuecart, amount;
    private ListView cartList;
    private TextView no_records;
    private TextView price_item, price_value, shippingvalue, totalprice;
    CardViewAdapter mcardViewAdapter;
    LinearLayoutManager mLayoutManager;
    List<CartModel> myCardList;
    private ProgressDialog progressDialog;
    private SharedPreferenceManager sharedPreferenceManager;
    private ImageView toolbar_iv_back;
    String id, productid, colorId, sizeId, strQty;
    private TextView toolbar_tv_title;
    private CardViewAdapter cardViewAdapter;
    int mSelectedPosition;
    int increment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_cart);
        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);

        address = (Button) findViewById(R.id.address);
        no_records = (TextView) findViewById(R.id.no_records);

        continuecart = (Button) findViewById(R.id.continuecart);
        cartList = (ListView) findViewById(R.id.cartList);
        amount = (Button) findViewById(R.id.amount);

        myCardList = new ArrayList<>();

        readyToolBar("MyCart");

        id = getIntent().getStringExtra("id");
        productid = getIntent().getStringExtra("productid");
        colorId = getIntent().getStringExtra("color_id");
        sizeId = getIntent().getStringExtra("size_id");
        strQty = getIntent().getStringExtra("quantity");


        if (id != null) {
            address.setText("Update Address");
        }


        //code to set adapter to populate list
        View footerView = ((LayoutInflater) ZoyMeCartActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footor_layout, null, false);
        cartList.addFooterView(footerView);

        price_item = (TextView) footerView.findViewById(R.id.price_item);
        price_value = (TextView) footerView.findViewById(R.id.price_value);
        shippingvalue = (TextView) footerView.findViewById(R.id.shippingvalue);
        totalprice = (TextView) footerView.findViewById(R.id.totalprice);


        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeCartActivity.this);
        progressDialog = new ProgressDialog(ZoyMeCartActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");


        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZoyMeCartActivity.this, ZoyMeAddressActivity.class);
                startActivity(intent);
                finish();
            }
        });


        if (productid != null) {


            if (Utils.getConnectivityStatusVal(ZoyMeCartActivity.this)) {
                progressDialog = new ProgressDialog(ZoyMeCartActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                makeJsonCartUpdate();

            } else {
                CustomMethods.displayDialog(ZoyMeCartActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
            }

        } else {
            if (Utils.getConnectivityStatusVal(ZoyMeCartActivity.this)) {
                progressDialog.show();
                makeJsonCart();
            } else {
                CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));
            }
        }
        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        continuecart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (id != null) {
                    String amount_value = amount.getText().toString();
                    Intent intent = new Intent(ZoyMeCartActivity.this, ZoyMePaymentActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("amount_value", amount_value);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeCartActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage("Please select address");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeCartActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeCartActivity.this).isFinishing()) {

                        alertDialog.show();
                    }

                }

            }
        });
    }

    private void makeJsonCartUpdateAdd(int position, int quantity) {
        this.mSelectedPosition = position;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", myCardList.get(position).getId());
        params.put("color_id", myCardList.get(position).getColor_id());
        params.put("size_id", myCardList.get(position).getSize_id());
        params.put("quantity", "" + quantity);
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_CARTUPDATE, params, addResponseListener, addResponseError), "tag_cartadd_req");
    }

    Response.Listener<JSONObject> addResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");
                    //  String total_price =response.getString("total_price");
                    JSONObject mJsonObject = null;

                    JSONObject jsonObjectdata = response.getJSONObject("data");
                    String total_price = jsonObjectdata.getString("total_price");
                    String total_quantity = jsonObjectdata.getString("total_quantity");


                    JSONArray jsonArrayproducts = jsonObjectdata.getJSONArray("products");


                    for (int i = 0; i < jsonArrayproducts.length(); i++) {

                        CartModel cartListingModel = myCardList.get(i);
                        mJsonObject = jsonArrayproducts.getJSONObject(i);
                        cartListingModel.setTotal_quantity(mJsonObject.getString("quantity"));
                        cartListingModel.setFinal_price(mJsonObject.getString("final_price"));

                        myCardList.set(i, cartListingModel);
                    }

                    price_item.setText("Price" + "  ( " + total_quantity + " ) " + "items");
                    String rupee = getResources().getString(R.string.Rs);
                    price_value.setText(rupee + " " + total_price);
                    amount.setText(rupee + " " + total_price);
                    cardViewAdapter.notifyDataSetChanged();// Notify the adapter*/


                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeCartActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeCartActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeCartActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    Response.ErrorListener addResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };

    private void makeJsonCartUpdate() {
        myCardList.clear();
        Map<String, String> params = new LinkedHashMap<>();
        params.put("id", productid);
        params.put("color_id", colorId);
        params.put("size_id", sizeId);
        params.put("quantity", strQty);
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_CARTUPDATE, params, cartUpdateResponseListener, cartUpdateResponseError), "tag_cartUpdate_req");
    }


    Response.Listener<JSONObject> cartUpdateResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");
                    JSONObject mJsonObject = null;

                    JSONObject jsonObjectdata = response.getJSONObject("data");
                    String total_price = jsonObjectdata.getString("total_price");
                    String total_quantity = jsonObjectdata.getString("total_quantity");

                    JSONArray jsonArrayproducts = jsonObjectdata.getJSONArray("products");
                    if (jsonObjectdata.getJSONArray("products").length() != 0) {

                        for (int i = 0; i < jsonArrayproducts.length(); i++) {

                            CartModel cartModel = new CartModel();
                            mJsonObject = jsonArrayproducts.getJSONObject(i);
                            cartModel.setId(mJsonObject.getString("id"));
                            cartModel.setName(mJsonObject.getString("name"));
                            cartModel.setTotal_quantity(mJsonObject.getString("quantity"));
                            cartModel.setImage_url(mJsonObject.getString("image_url"));
                            cartModel.setColor_id(mJsonObject.getString("color_id"));
                            cartModel.setColor_name(mJsonObject.getString("color_name"));
                            cartModel.setSize_id(mJsonObject.getString("size_id"));
                            cartModel.setSize_name(mJsonObject.getString("size_name"));
                            cartModel.setFinal_price(mJsonObject.getString("final_price"));

                            myCardList.add(cartModel);
                            no_records.setVisibility(View.GONE);
                        }

                        price_item.setText("Price" + "  ( " + total_quantity + " ) " + "items");
                        String rupee = getResources().getString(R.string.Rs);
                        price_value.setText(rupee + " " + total_price);
                        amount.setText(rupee + " " + total_price);
                        cardViewAdapter = new CardViewAdapter(ZoyMeCartActivity.this, myCardList, ZoyMeCartActivity.this);
                        cartList.setAdapter(cardViewAdapter);
                        cardViewAdapter.notifyDataSetChanged();// Notify the adapter*/


                    } else {
                        no_records.setVisibility(View.VISIBLE);
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeCartActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeCartActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeCartActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };


    private Response.ErrorListener cartUpdateResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());

            if (error.networkResponse != null) {

                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Toast.makeText(ZoyMeCartActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(ZoyMeCartActivity.this, getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void makeJsonCart() {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_CART, params, cartResponseListener, cartResponseError), "cart_req");
    }

    Response.Listener<JSONObject> cartResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");
                    JSONObject mJsonObject = null;

                    JSONObject jsonObjectdata = response.getJSONObject("data");
                    String total_price = jsonObjectdata.getString("total_price");
                    String total_quantity = jsonObjectdata.getString("total_quantity");

                    JSONArray jsonArrayproducts = jsonObjectdata.getJSONArray("products");
                    if (jsonObjectdata.getJSONArray("products").length() != 0) {

                        for (int i = 0; i < jsonArrayproducts.length(); i++) {

                            CartModel cartModel = new CartModel();
                            mJsonObject = jsonArrayproducts.getJSONObject(i);
                            cartModel.setId(mJsonObject.getString("id"));
                            cartModel.setName(mJsonObject.getString("name"));
                            cartModel.setTotal_quantity(mJsonObject.getString("quantity"));
                            cartModel.setImage_url(mJsonObject.getString("image_url"));
                            cartModel.setColor_id(mJsonObject.getString("color_id"));
                            cartModel.setColor_name(mJsonObject.getString("color_name"));
                            cartModel.setSize_id(mJsonObject.getString("size_id"));
                            cartModel.setSize_name(mJsonObject.getString("size_name"));
                            cartModel.setFinal_price(mJsonObject.getString("final_price"));

                            myCardList.add(cartModel);
                            no_records.setVisibility(View.GONE);
                        }
                        price_item.setText("Price" + "  ( " + total_quantity + " ) " + "items");
                        String rupee = getResources().getString(R.string.Rs);
                        price_value.setText(rupee + " " + total_price);
                        amount.setText(rupee + " " + total_price);
                        cardViewAdapter = new CardViewAdapter(ZoyMeCartActivity.this, myCardList, ZoyMeCartActivity.this);
                        cartList.setAdapter(cardViewAdapter);
                        cardViewAdapter.notifyDataSetChanged();// Notify the adapter*/


                    } else {
                        no_records.setVisibility(View.VISIBLE);
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeCartActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeCartActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeCartActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    Response.ErrorListener cartResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };

    private void readyToolBar(String tittle) {

        toolbar_tv_title.setText(tittle);


    }


    @Override
    public void subDeletelistPosition(String mFlag, int mPosition) {
        if (mFlag == "delete_click") {
            makeJsonCartDelete(mPosition);
        }
    }

    @Override
    public void subminuslistPosition(String mFlag, int mPosition, int value) {
        if (mFlag == "plus_click") {
            makeJsonCartUpdateAdd(mPosition, value + increment);
        }
        if (mFlag == "minus_click") {
            makeJsonCartUpdateAdd(mPosition, value - increment);
        }
    }

    private void makeJsonCartDelete(int position) {
        this.mSelectedPosition = position;
        Map<String, String> params = new HashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("id", myCardList.get(position).getId());
        params.put("color_id", myCardList.get(position).getColor_id());
        params.put("size_id", myCardList.get(position).getSize_id());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_CARTDELETE, params, deleteResponseListener, deleteResponseError), "tag_delete_req");

    }

    Response.Listener<JSONObject> deleteResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {


                    String message = response.getString("message");
                    JSONObject jsonObjectdata = response.getJSONObject("data");
                    String total_price = jsonObjectdata.getString("total_price");
                    String total_quantity = jsonObjectdata.getString("total_quantity");
                    CartModel cartListingModel = myCardList.get(mSelectedPosition);

                    price_item.setText("Price" + "  ( " + total_quantity + " ) " + "items");
                    String rupee = getResources().getString(R.string.Rs);
                    price_value.setText(rupee + " " + total_price);
                    amount.setText(rupee + " " + total_price);

                    cardViewAdapter.removeItem(mSelectedPosition);
                    cardViewAdapter.notifyDataSetChanged();
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeCartActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeCartActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeCartActivity.this).isFinishing()) {

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
}
