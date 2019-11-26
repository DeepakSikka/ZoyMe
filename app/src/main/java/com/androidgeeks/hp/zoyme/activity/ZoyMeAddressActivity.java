package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import com.androidgeeks.hp.zoyme.adapter.OrderAdapter;
import com.androidgeeks.hp.zoyme.adapter.ZoymeProductListAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.AddressDetailModel;
import com.androidgeeks.hp.zoyme.model.ProductListingModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.AddressListClick;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ProductList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.androidgeeks.hp.zoyme.AppController.getContext;

public class ZoyMeAddressActivity extends AppCompatActivity implements AddressListClick {
    private TextView toolbar_tv_title;
    private RecyclerView addressList;
    private TextView fragment_address_tv_no_records;
    private TextView add_address;
    private ProgressDialog progressDialog;
    private ImageView toolbar_iv_back, toolbar_iv_cart;
    private SharedPreferenceManager sharedPreferenceManager;
    List<AddressDetailModel> myAddressList;
    AddressAdapter addressAdapter;
    private int mSelectedPosition;
    int mDeletedposition;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_address);

        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);
        addressList = (RecyclerView) findViewById(R.id.addressList);
        fragment_address_tv_no_records = (TextView) findViewById(R.id.fragment_address_tv_no_records);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_cart = (ImageView) findViewById(R.id.toolbar_iv_cart);
        add_address = (TextView) findViewById(R.id.add_address);

        myAddressList = new ArrayList<>();

        addressList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        addressList.setLayoutManager(layoutManager);
        readyToolBar("My Address");

        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeAddressActivity.this);
        if (Utils.getConnectivityStatusVal(ZoyMeAddressActivity.this)) {
            progressDialog = new ProgressDialog(ZoyMeAddressActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            makeJsonAddressDetail();
        } else {
            CustomMethods.displayDialog(ZoyMeAddressActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
        }


        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeAddressActivity.this, ZoyMeAddAddressActivity.class);
                mIntent.putExtra("edit_address","Add New Address");
                startActivity(mIntent);
               // finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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
                Intent mIntent = new Intent(ZoyMeAddressActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


        addressList.addOnItemTouchListener(new RecyclerTouchListener(ZoyMeAddressActivity.this, addressList, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                id = myAddressList.get(position).getId();
                Intent sessionDetailIntent = new Intent(ZoyMeAddressActivity.this, ZoyMeCartActivity.class);
                sessionDetailIntent.putExtra("id", id);
                startActivity(sessionDetailIntent);
                finish();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }


    private void makeJsonAddressDetail() {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_ADDRESS, params, addressDetailResponseListener, addressDetailResponseError), "tag_categoryDetail_req");
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
                            maddressModel.setFlat(mJsonObject.getString("flat"));
                            maddressModel.setLocality(mJsonObject.getString("locality"));
                            maddressModel.setCity(mJsonObject.getString("city"));
                            maddressModel.setState(mJsonObject.getString("state"));
                            maddressModel.setPincode(mJsonObject.getString("pincode"));
                            maddressModel.setPhone(mJsonObject.getString("phone"));
                            maddressModel.setAlternate_phone(mJsonObject.getString("alternate_phone"));

                            myAddressList.add(maddressModel);
                            fragment_address_tv_no_records.setVisibility(View.GONE);
                        }

                        addressAdapter = new AddressAdapter(ZoyMeAddressActivity.this, myAddressList, ZoyMeAddressActivity.this);
                        addressList.setAdapter(addressAdapter);
                        addressAdapter.notifyDataSetChanged();// Notify the adapter*/
                    } else {
                        fragment_address_tv_no_records.setVisibility(View.VISIBLE);
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeAddressActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeAddressActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeAddressActivity.this).isFinishing()) {

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
                    Toast.makeText(ZoyMeAddressActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(ZoyMeAddressActivity.this, getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void readyToolBar(String tittle) {

        toolbar_tv_title.setText(tittle);


    }


    @Override
    public void setAppDeletePostion(String mFlag, Object mObject, int mPosition) {

        mDeletedposition = mPosition;

        final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeAddressActivity.this).create();
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setCancelable(false);

        alertDialog.setMessage("Are you sure you want to delete this address.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeAddressActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                makeJsonDelete(mDeletedposition);
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, ZoyMeAddressActivity.this.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (!((Activity) ZoyMeAddressActivity.this).isFinishing()) {

            alertDialog.show();
        }


    }
    @Override
    public void setAppUpdatePostion(String mFlag, Object mObject, int mPosition) {
        Intent mIntent = new Intent(ZoyMeAddressActivity.this, ZoyMeAddAddressActivity.class);
        mIntent.putExtra("id",id);
        mIntent.putExtra("edit_address","Edit Address");
        startActivity(mIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }


    private void makeJsonDelete(int position) {
        this.mSelectedPosition = position;
        Map<String, String> params = new HashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("id", myAddressList.get(position).getId());


        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_DELETE, params, deleteResponseListener, deleteResponseError), "tag_delete_req");

    }

    Response.Listener<JSONObject> deleteResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());


            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {


                    String message = response.getString("message");
                    AddressDetailModel addressDetailModel = myAddressList.get(mSelectedPosition);

                    addressAdapter.removeItem(mDeletedposition);


                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeAddressActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeAddressActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeAddressActivity.this).isFinishing()) {

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


    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements
            RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context,
                                     final RecyclerView recyclerView,
                                     final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapUp(MotionEvent e) {
                            return true;
                        }

                        @Override
                        public void onLongPress(MotionEvent e) {
                            View child = recyclerView.findChildViewUnder(
                                    e.getX(), e.getY());
                            if (child != null && clickListener != null) {
                                clickListener.onLongClick(child,
                                        recyclerView.getChildPosition(child));
                            }
                        }
                    });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null
                    && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }


}
