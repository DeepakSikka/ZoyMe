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
import com.androidgeeks.hp.zoyme.adapter.OrderAdapter;
import com.androidgeeks.hp.zoyme.adapter.ReviewAdapter;
import com.androidgeeks.hp.zoyme.adapter.ZoymeProductListAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.ProductListingModel;
import com.androidgeeks.hp.zoyme.model.ReviewModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.androidgeeks.hp.zoyme.AppController.getContext;

public class ZoyMeReviewActivity extends AppCompatActivity {

    private RecyclerView reviewList;
    private TextView fragment_review_tv_no_records;
    private TextView toolbar_tv_title;
    private ImageView toolbar_iv_back, toolbar_iv_cart;
    SharedPreferenceManager sharedPreferenceManager;
    private ProgressDialog progressDialog;
    List<ReviewModel> reviewModels;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_review);

        reviewList = (RecyclerView) findViewById(R.id.reviewList);
        fragment_review_tv_no_records = (TextView) findViewById(R.id.fragment_review_tv_no_records);
        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_cart = (ImageView) findViewById(R.id.toolbar_iv_cart);
        readyToolBar("My Review");

        reviewList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        reviewList.setLayoutManager(layoutManager);


        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeReviewActivity.this);
        reviewModels = new ArrayList<>();

        if (Utils.getConnectivityStatusVal(ZoyMeReviewActivity.this)) {
            progressDialog = new ProgressDialog(ZoyMeReviewActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            makeJsonReview();
        } else {
            CustomMethods.displayDialog(ZoyMeReviewActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
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
                Intent mIntent = new Intent(ZoyMeReviewActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

    }

    private void makeJsonReview() {

        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_REVIEW, params, reviewResponseListener, reviewResponseError), "tag_review_req");
    }

    Response.Listener<JSONObject> reviewResponseListener = new Response.Listener<JSONObject>() {
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
                            ReviewModel mreviewModel = new ReviewModel();
                            mJsonObject = jsonArrayProducts.getJSONObject(i);
                            mreviewModel.setId(mJsonObject.getString("id"));
                            mreviewModel.setName(mJsonObject.getString("name"));
                            mreviewModel.setRating(mJsonObject.getString("rating"));
                            mreviewModel.setProduct_id(mJsonObject.getString("product_id"));
                            mreviewModel.setImage_url(mJsonObject.getString("image_url"));


                            reviewModels.add(mreviewModel);
                            fragment_review_tv_no_records.setVisibility(View.GONE);
                        }
                        reviewAdapter = new ReviewAdapter(ZoyMeReviewActivity.this, reviewModels);
                        reviewList.setAdapter(reviewAdapter);
                        reviewAdapter.notifyDataSetChanged();// Notify the adapter*/


                    } else {
                        fragment_review_tv_no_records.setVisibility(View.VISIBLE);
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeReviewActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeReviewActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeReviewActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };


    private Response.ErrorListener reviewResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());

            if (error.networkResponse != null) {

                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Toast.makeText(ZoyMeReviewActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(ZoyMeReviewActivity.this, getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void readyToolBar(String tittle) {

        toolbar_tv_title.setText(tittle);


    }

}
