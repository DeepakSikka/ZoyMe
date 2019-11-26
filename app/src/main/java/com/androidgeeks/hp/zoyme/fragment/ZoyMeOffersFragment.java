package com.androidgeeks.hp.zoyme.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.activity.ZoyMeHomeActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeProductDetailActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeProductListActivity;
import com.androidgeeks.hp.zoyme.adapter.ZoymeOfferListAdapter;
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

public class ZoyMeOffersFragment extends Fragment implements ProductList {
    private RecyclerView productList;
    private LinearLayoutManager mProductLinearLayoutManager;
    private ZoymeOfferListAdapter zoymeOfferListAdapter;
    private ProgressDialog progressDialog;
    private TextView tvNoRecords;
    List<ProductListingModel> myProductList;
    private SharedPreferenceManager sharedPreferenceManager;
    private int mSelectedPosition;

    public ZoyMeOffersFragment() {
        // Required empty public constructor
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
        View rootview = inflater.inflate(R.layout.fragment_offers, container, false);
        initView(rootview);
        return rootview;
    }


    private void initView(View rootview) {
        productList = (RecyclerView) rootview.findViewById(R.id.productList);
        mProductLinearLayoutManager = new GridLayoutManager(getActivity(), 2);
        tvNoRecords = (TextView) rootview.findViewById(R.id.fragment_product_tv_no_records);
        sharedPreferenceManager = new SharedPreferenceManager(getContext());
        myProductList = new ArrayList<>();


        initToolBar();


        if (Utils.getConnectivityStatusVal(getContext())) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            makeJsonOffer();
        } else {
            CustomMethods.displayDialog(getContext(), getString(R.string.app_name), getString(R.string.check_connection));
        }


    }


    private void makeJsonOffer() {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_OFFER, params, offerResponseListener, offerDetailResponseError), "tag_offerDetail_req");
    }

    Response.Listener<JSONObject> offerResponseListener = new Response.Listener<JSONObject>() {
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
                        zoymeOfferListAdapter = new ZoymeOfferListAdapter(getActivity(), myProductList, ZoyMeOffersFragment.this);
                        productList.setLayoutManager(mProductLinearLayoutManager);
                        productList.setAdapter(zoymeOfferListAdapter);
                        zoymeOfferListAdapter.notifyDataSetChanged();// Notify the adapter*/
                    } else {
                        tvNoRecords.setVisibility(View.VISIBLE);
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
    private Response.ErrorListener offerDetailResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());

            if (error.networkResponse != null) {

                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Toast.makeText(getActivity(), jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * To initialize toolbar
     */
    private void initToolBar() {
        if (getActivity() instanceof ZoyMeHomeActivity) {
            ((ZoyMeHomeActivity) getActivity()).showToolBar();
            ((ZoyMeHomeActivity) getActivity()).setupToolbar(true, getString(R.string.offers), true, true, true);
        }
        final Toolbar toolBar = ((ZoyMeHomeActivity) getActivity()).getToolbar();
        toolBar.getMenu().clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void subProductPosition(String mFlag, Object mObject, int mPosition, String mId, String mTittle) {


        Intent mIntent = new Intent(getActivity(), ZoyMeProductDetailActivity.class);
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


                    zoymeOfferListAdapter.notifyDataSetChanged();
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
    Response.ErrorListener likeResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };

}
