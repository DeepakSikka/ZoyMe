package com.androidgeeks.hp.zoyme.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.activity.ZoyMeHomeActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeOrderActivity;
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


public class ZoyMeOrderFragment extends Fragment implements OrderSubmitClick {

    private RecyclerView orderList;
    private TextView fragment_order_tv_no_records;
    private ArrayList countries;
    private SharedPreferenceManager sharedPreferenceManager;
    List<OrderModel> orderModels;
    private OrderAdapter orderAdapter;
    private ProgressDialog progressDialog;
    private int mSelectedPosition;

    public ZoyMeOrderFragment() {
        // Required empty public constructor
    }


    public static ZoyMeOrderFragment newInstance() {

        Bundle args = new Bundle();

        ZoyMeOrderFragment fragment = new ZoyMeOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_order, container, false);

        initToolBar();
        initView(rootview);
        return rootview;
    }

    /**
     * @param rootview
     */
    private void initView(View rootview) {
        orderList = (RecyclerView) rootview.findViewById(R.id.orderList);
        fragment_order_tv_no_records = (TextView) rootview.findViewById(R.id.fragment_order_tv_no_records);

        orderList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        orderList.setLayoutManager(layoutManager);
        sharedPreferenceManager = new SharedPreferenceManager(getContext());
        orderModels = new ArrayList<>();


        if (Utils.getConnectivityStatusVal(getContext())) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            makeJsonOrderDetail();
        } else {
            CustomMethods.displayDialog(getContext(), getString(R.string.app_name), getString(R.string.check_connection));
        }

    }

    private void makeJsonOrderDetail() {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_ORDER, params, orderDetailResponseListener, orderDetailResponseError), "tag_orderDetail_req");
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
                            fragment_order_tv_no_records.setVisibility(View.GONE);
                        }
                        orderAdapter = new OrderAdapter(getContext(), orderModels, ZoyMeOrderFragment.this);
                        orderList.setAdapter(orderAdapter);
                        orderAdapter.notifyDataSetChanged();// Notify the adapter*/
                    } else {
                        fragment_order_tv_no_records.setVisibility(View.VISIBLE);
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) getContext()).isFinishing()) {

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
                    Toast.makeText(getContext(), jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    /**
     * To initialize toolbar
     */


    private void initToolBar() {
        if (getActivity() instanceof ZoyMeHomeActivity) {
            ((ZoyMeHomeActivity) getActivity()).showToolBar();
            ((ZoyMeHomeActivity) getActivity()).setupToolbar(true, getString(R.string.My_Orders), true, true, true);
        }
        final Toolbar toolBar = ((ZoyMeHomeActivity) getActivity()).getToolbar();
        toolBar.getMenu().clear();
    }


    @Override
    public void subOrderClick(String mFlag, Object mObject, int mPosition, String ratingValue) {

        makeJsonReviewCreate(mPosition, ratingValue);
    }


    private void makeJsonReviewCreate(int position, String ratingValue) {
        this.mSelectedPosition = position;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        params.put("star", ratingValue);
        params.put("product_id", orderModels.get(position).getProduct_id());
        params.put("order_id", orderModels.get(position).getId());

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_REVIEW_CREATE, params, reviewCreateResponseListener, orderDetailResponseError), "tag_reviewCreate_req");
    }

    Response.Listener<JSONObject> reviewCreateResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {
                    String message = response.getString("message");

                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) getContext()).isFinishing()) {

                        alertDialog.show();
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) getContext()).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

}