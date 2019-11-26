package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.adapter.ImagesAdapter;
import com.androidgeeks.hp.zoyme.adapter.SpinnerAdaptor;
import com.androidgeeks.hp.zoyme.adapter.ViewPagination;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.BannerModel;
import com.androidgeeks.hp.zoyme.model.ProductDetailModel;
import com.androidgeeks.hp.zoyme.model.SizeModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ImageClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;

public class ZoyMeProductImageActivity extends AppCompatActivity implements ImageClick {
    private String id, tittle;
    private SharedPreferenceManager sharedPreferenceManager;
    ArrayList<BannerModel> bannerarray;
    private ViewPager mypanelproduct;
    ImagesAdapter viewPagination;
    private TextView toolbar_tv_title;
    private ImageView toolbar_iv_back, toolbar_iv_cart;
    private Button addtocart, amount;
    private CircleIndicator indicator;
    String amountvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_image);


        id = getIntent().getStringExtra("id");
        tittle = getIntent().getStringExtra("tittle");

        amountvalue = getIntent().getStringExtra("amountvalue");
        if (id != null) {

        }
        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeProductImageActivity.this);

        mypanelproduct = (ViewPager) findViewById(R.id.mypanelproduct);
        bannerarray = new ArrayList<>();
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_cart = (ImageView) findViewById(R.id.toolbar_iv_cart);
        addtocart = (Button) findViewById(R.id.addtocart);
        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        amount = (Button) findViewById(R.id.amount);

        amount.setText(amountvalue);
        readyToolBar(tittle);


        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZoyMeProductImageActivity.this, ZoyMeCartActivity.class);
                startActivity(intent);
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
                Intent mIntent = new Intent(ZoyMeProductImageActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        /**
         *
         *  product Service
         *  id "3"
         *  Token "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vMTM5LjU5LjExLjI1MC9hcGkvYXV0aC9sb2dpbiIsImlhdCI6MTUxMDMwMTY3MSwiZXhwIjoxNTEwMzA1MjcxLCJuYmYiOjE1MTAzMDE2NzEsImp0aSI6IkJRY09BazZtc2tEZXFmZGsiLCJzdWIiOjIsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.QPe0-k87Jci1sI5LbpaGCL6q9Ws0wBzTMZ9A9MINR0Y"
         */
        if (Utils.getConnectivityStatusVal(ZoyMeProductImageActivity.this)) {
            getProduct();
        } else {
            CustomMethods.displayDialog(ZoyMeProductImageActivity.this, getString(R.string.app_name), getString(R.string.check_connection));

        }
    }

    private void getProduct() {
        Map<String, String> productParams = new LinkedHashMap<>();
        productParams.put("token", sharedPreferenceManager.getToken());
        productParams.put("id", id);
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_PRODUCT, productParams, ProductReponseListener,
                ProductErrorListener), "tag_product_req");

    }

    Response.Listener<JSONObject> ProductReponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            Log.d("response", response.toString());
            //progress_product.setVisibility(View.GONE);
            try {
                if (response.getInt("status") == 200) {
                    JSONObject jsonObject = response.getJSONObject("data");

                    JSONArray jsonArrayImage = jsonObject.getJSONArray("images");
                    for (int j = 0; j < jsonArrayImage.length(); j++) {
                        BannerModel bannermodel = new BannerModel();
                        JSONObject jsonObjimage = jsonArrayImage.getJSONObject(j);


                        bannermodel.setImage(jsonObjimage.getString("image_url"));
                        bannerarray.add(bannermodel);

                    }

                    viewPagination = new ImagesAdapter(ZoyMeProductImageActivity.this, bannerarray, ZoyMeProductImageActivity.this);
                    mypanelproduct.setAdapter(viewPagination);
                    indicator.setViewPager(mypanelproduct);
                    viewPagination.registerDataSetObserver(indicator.getDataSetObserver());


                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeProductImageActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeProductImageActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeProductImageActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    Response.ErrorListener ProductErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("response", error.toString());


        }
    };

    private void readyToolBar(String tittle) {

        toolbar_tv_title.setText(tittle);


    }


    @Override
    public void subImagePosition(String image_click) {

    }

    @Override
    public void subImagePositionValue(String image_click, String position, String Tittle,String Flag) {

    }


}
