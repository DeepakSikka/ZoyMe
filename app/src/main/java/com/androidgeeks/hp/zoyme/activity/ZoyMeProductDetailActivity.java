package com.androidgeeks.hp.zoyme.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.adapter.ImagesAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.BannerModel;
import com.androidgeeks.hp.zoyme.model.ColorModel;
import com.androidgeeks.hp.zoyme.model.ProductDetailModel;
import com.androidgeeks.hp.zoyme.model.SizeModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.BetterSpinner;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ImageClick;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator;


public class ZoyMeProductDetailActivity extends AppCompatActivity implements ImageClick {
    private static final String TAG = "ZoyMeProductDetailActivity";
    private ViewPager mypanelproduct;
    private TextView product_name;
    private TextView product_description;
    private String id, tittle;
    // private ProgressBar progress_product;
    private SharedPreferenceManager sharedPreferenceManager;
    ArrayList<ProductDetailModel> mproductDetailModels;
    private TextView toolbar_tv_title;
    ArrayList<BannerModel> bannerarray;
    ImagesAdapter viewPagination;
    private CircleIndicator indicator;
    private RatingBar rating_product;
    private Button amount, buyno;
    private BetterSpinner fragment_qty;
    private BetterSpinner fragment_size;
    private BetterSpinner fragment_color;
    private RelativeLayout size_layout, color_layout;
    private ImageButton facebook_share, twitter_share;
    private String strQty;
    private ArrayList<SizeModel> sizeModels;
    ArrayList<String> sizeList;
    ArrayList<String> colorlist;
    ArrayList<ColorModel> colorModels;
    private String sizeId = "", colorId = "";
    private FrameLayout viewpager_layout;
    private ImageView toolbar_iv_back, toolbar_iv_cart;
    private ProgressDialog progressDialog;
    int quantity_validator;
    private ArrayList<String> dynamicQuantity;
    JSONArray jsonArraySizes;
    JSONArray jsonArrayColor;
    private boolean isColorSelected = false;
    private boolean isSizeSelected = false;
    private RelativeLayout relative_top;
    String postDetail;
    private CallbackManager callbackManager;
    ShareDialog shareDialog;
    private static final int REQ_START_SHARE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_product_detail);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize facebook SDK.
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Create a callbackManager to handle the login responses.
        callbackManager = CallbackManager.Factory.create();

        shareDialog = new ShareDialog(this);




        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeProductDetailActivity.this);
        product_name = (TextView) findViewById(R.id.product_name);
        product_description = (TextView) findViewById(R.id.product_description);
        mypanelproduct = (ViewPager) findViewById(R.id.mypanelproduct);
        relative_top = (RelativeLayout) findViewById(R.id.relative_top);

        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        rating_product = (RatingBar) findViewById(R.id.rating_product);
        buyno = (Button) findViewById(R.id.buyno);
        amount = (Button) findViewById(R.id.amount);

        size_layout = (RelativeLayout) findViewById(R.id.size_layout);
        color_layout = (RelativeLayout) findViewById(R.id.color_layout);

        fragment_qty = (BetterSpinner) findViewById(R.id.fragment_qty);
        fragment_size = (BetterSpinner) findViewById(R.id.fragment_size);
        fragment_color = (BetterSpinner) findViewById(R.id.fragment_color);
        facebook_share = (ImageButton) findViewById(R.id.facebook_share);
        twitter_share = (ImageButton) findViewById(R.id.twitter_share);

        viewpager_layout = (FrameLayout) findViewById(R.id.viewpager_layout);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_cart = (ImageView) findViewById(R.id.toolbar_iv_cart);

        mproductDetailModels = new ArrayList<>();
        bannerarray = new ArrayList<>();

        colorModels = new ArrayList<ColorModel>();
        colorlist = new ArrayList<String>();
        sizeModels = new ArrayList<SizeModel>();
        sizeList = new ArrayList<String>();
        dynamicQuantity = new ArrayList<String>();

        id = getIntent().getExtras().getString("id");
        tittle = getIntent().getExtras().getString("tittle");
        readyToolBar(tittle);



        fragment_qty.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                strQty = getResources().getStringArray(R.array.qty)[i];

            }
        });
        fragment_size.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSizeSelected = true;
                sizeId = sizeModels.get(position).getId();
            }
        });

        fragment_color.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isColorSelected = true;
                colorId = colorModels.get(position).getId();
            }
        });

        /**
         *
         *  product Service
         *  id "3"
         *  Token "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vMTM5LjU5LjExLjI1MC9hcGkvYXV0aC9sb2dpbiIsImlhdCI6MTUxMDMwMTY3MSwiZXhwIjoxNTEwMzA1MjcxLCJuYmYiOjE1MTAzMDE2NzEsImp0aSI6IkJRY09BazZtc2tEZXFmZGsiLCJzdWIiOjIsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.QPe0-k87Jci1sI5LbpaGCL6q9Ws0wBzTMZ9A9MINR0Y"
         */
        if (Utils.getConnectivityStatusVal(ZoyMeProductDetailActivity.this)) {
            getProduct();
        } else {
            CustomMethods.displayDialog(ZoyMeProductDetailActivity.this, getString(R.string.app_name), getString(R.string.check_connection));

        }


        buyno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mSizeString = sizeList.get(0);
                String mColorString = colorlist.get(0);
                if (mSizeString.equalsIgnoreCase("no size") || ((!mSizeString.equalsIgnoreCase("no size")) && isSizeSelected == true)) {
                    if (mColorString.equalsIgnoreCase("no color") || ((!mColorString.equalsIgnoreCase("no color")) && isColorSelected == true)) {
                        if (strQty != null) {

                            Intent intent = new Intent(ZoyMeProductDetailActivity.this, ZoyMeCartActivity.class);
                            intent.putExtra("productid", id);
                            intent.putExtra("color_id", colorId);
                            intent.putExtra("size_id", sizeId);
                            intent.putExtra("quantity", strQty);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                        } else {
                            Toast.makeText(ZoyMeProductDetailActivity.this, "Please Select Quantity", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ZoyMeProductDetailActivity.this, "Please Select Color", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ZoyMeProductDetailActivity.this, "Please Select Size", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toolbar_iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeProductDetailActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


        viewpager_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeProductDetailActivity.this, ZoyMeProductImageActivity.class);
                startActivity(mIntent);
            }
        });


        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSdk.sdkInitialize(getApplicationContext());
                ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                        .setContentTitle(product_name.getText().toString())
                        .setContentDescription(product_name.getText().toString())
                        .setContentUrl(Uri.parse(bannerarray.get(0).getImage()))
                        .setImageUrl(Uri.parse(bannerarray.get(0).getImage()))
                        .build();

                ShareDialog.show(ZoyMeProductDetailActivity.this,shareLinkContent);

            }
        });

        twitter_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Uri uri = Uri.parse(bannerarray.get(0).getImage());
                PlusShare.Builder share = new PlusShare.Builder(ZoyMeProductDetailActivity.this);
                share.setText(product_name.getText().toString());
                share.setContentUrl(uri);
                share.setType("text/plain");
                startActivityForResult(share.getIntent(), REQ_START_SHARE);
            }
        });


        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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
            try {
                if (response.getInt("status") == 200) {
                    colorlist = new ArrayList<>();
                    JSONObject jsonObject = response.getJSONObject("data");

                    ProductDetailModel productDetailModel = new ProductDetailModel();

                    productDetailModel.setName(jsonObject.getString("name"));
                    productDetailModel.setDescription(jsonObject.getString("description"));
                    productDetailModel.setId(jsonObject.getString("id"));
                    productDetailModel.setQuantity(jsonObject.getString("quantity"));
                    productDetailModel.setPrice(jsonObject.getString("price"));
                    productDetailModel.setRating(jsonObject.getString("rating"));

                    product_name.setText(jsonObject.getString("name"));
                    product_description.setText(jsonObject.getString("description"));
                    rating_product.setRating(Integer.parseInt(jsonObject.getString("rating")));

                    quantity_validator = Integer.parseInt(jsonObject.getString("quantity"));

                    if (jsonObject.getString("new_price").equals("") || jsonObject.getString("new_price").equals("null")) {
                        String rupee = getResources().getString(R.string.Rs);
                        amount.setText(rupee + " " + jsonObject.getString("price"));
                    } else {
                        String rupee = getResources().getString(R.string.Rs);
                        amount.setText(rupee + " " + jsonObject.getString("new_price"));
                    }
                    JSONArray jsonArrayImage = jsonObject.getJSONArray("images");
                    mproductDetailModels.add(productDetailModel);

                    for (int j = 0; j < jsonArrayImage.length(); j++) {
                        BannerModel bannermodel = new BannerModel();
                        JSONObject jsonObjimage = jsonArrayImage.getJSONObject(j);


                        bannermodel.setImage(jsonObjimage.getString("image_url"));
                        bannerarray.add(bannermodel);

                    }


                    jsonArrayColor = jsonObject.getJSONArray("colors");
                    if (jsonObject.getJSONArray("colors").length() != 0) {

                        for (int i = 0; i < jsonArrayColor.length(); i++) {
                            JSONObject resultJson = (JSONObject) jsonArrayColor.get(i);

                            ColorModel colorpop = new ColorModel();
                            colorpop.setId(resultJson.getString("id"));
                            colorpop.setName(resultJson.getString("name"));
                            colorpop.setCode(resultJson.getString("code"));

                            colorModels.add(colorpop);

                            colorlist.add(resultJson.getString("name"));

                        }

                        fragment_color.setAdapter(new ArrayAdapter<String>(ZoyMeProductDetailActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                colorlist));


                    } else {
                        colorlist.add("no color");
                        color_layout.setVisibility(View.GONE);
                    }

                    jsonArraySizes = jsonObject.getJSONArray("sizes");
                    if (jsonObject.getJSONArray("sizes").length() != 0) {
                        for (int i = 0; i < jsonArraySizes.length(); i++) {
                            JSONObject resultJson = (JSONObject) jsonArraySizes.get(i);

                            SizeModel sizepop = new SizeModel();
                            sizepop.setId(resultJson.getString("id"));
                            sizepop.setName(resultJson.getString("name"));

                            sizeModels.add(sizepop);

                            sizeList.add(resultJson.getString("name"));

                        }
                        fragment_size.setAdapter(new ArrayAdapter<String>(ZoyMeProductDetailActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                sizeList));
                    } else {
                        sizeList.add("no size");
                        size_layout.setVisibility(View.GONE);
                    }

                    if (quantity_validator < 10) {
                        for (int i = 1; i <= quantity_validator; i++) {
                            dynamicQuantity.add("" + i);
                        }
                        fragment_qty.setAdapter(new ArrayAdapter<String>(ZoyMeProductDetailActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                dynamicQuantity));
                    } else {
                        final ArrayAdapter qtyAdapter = new ArrayAdapter<>(ZoyMeProductDetailActivity.this, R.layout.spinnertext, getResources().getStringArray(R.array.qty));
                        fragment_qty.setAdapter(qtyAdapter);
                    }

                    viewPagination = new ImagesAdapter(ZoyMeProductDetailActivity.this, bannerarray, ZoyMeProductDetailActivity.this);
                    mypanelproduct.setAdapter(viewPagination);
                    indicator.setViewPager(mypanelproduct);
                    viewPagination.registerDataSetObserver(indicator.getDataSetObserver());


                } else {
                    relative_top.setVisibility(View.GONE);
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeProductDetailActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    if (!((Activity) ZoyMeProductDetailActivity.this).isFinishing()) {

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
        String amountvalue = amount.getText().toString();
        Intent intent = new Intent(ZoyMeProductDetailActivity.this, ZoyMeProductImageActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("amountvalue", amountvalue);
        intent.putExtra("tittle", tittle);
        startActivity(intent);

    }

    @Override
    public void subImagePositionValue(String image_click, String position, String Tittle,String Flag) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Call callbackManager.onActivityResult to pass login result to the LoginManager via callbackManager.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            // Write some code to do some operations when you shared content successfully.
        }

        @Override
        public void onCancel() {
            // Write some code to do some operations when you cancel sharing content.
        }

        @Override
        public void onError(FacebookException error) {
            // Write some code to do some operations when some error occurs while sharing content.
        }
    };




}
