package com.androidgeeks.hp.zoyme.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.activity.ZoyMeHomeActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeProductListActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeSearchActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeSubCategories;
import com.androidgeeks.hp.zoyme.adapter.ViewPaginationButtom;
import com.androidgeeks.hp.zoyme.adapter.ZoyMeBannerAdapter;
import com.androidgeeks.hp.zoyme.adapter.ZoyMeBannerBaseAdapter;
import com.androidgeeks.hp.zoyme.adapter.ZoyMeCategories;
import com.androidgeeks.hp.zoyme.adapter.ViewPagination;
import com.androidgeeks.hp.zoyme.adapter.ZoymeSubCategoriesAdapter;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.BannerModel;
import com.androidgeeks.hp.zoyme.model.CategoryModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.AppUserListDataListener;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ImageClick;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.SubCategoryInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by deepak sikka on 11/11/2017
 */
public class ZoyMeHomeFragment extends Fragment implements AppUserListDataListener, ImageClick, SubCategoryInterface {
    private LinearLayoutManager mHomeLinearLayoutManager, mHomeLinearLayoutManager1,mHomeCategoryLinearLayoutManager;
    private RecyclerView mHomeRecyclerView;
    private ZoyMeCategories mZoyMeCategories;
    private ZoymeSubCategoriesAdapter zoymeCategoriesAdapter;
    private ZoyMeBannerBaseAdapter zoyMeBannerAdapter;

    private RecyclerView rvCategories;
    public static final Integer[] IMAGES1 = {R.drawable.sale_banner1, R.drawable.sale_banner2, R.drawable.sale_banner1, R.drawable.sale_banner2, R.drawable.sale_banner1, R.drawable.sale_banner2, R.drawable.sale_banner1, R.drawable.sale_banner2};
    private TextView search_edt;
    private ViewPager viewpager, mypanelproduct_offers;
    private CircleIndicator indicator;
    private SharedPreferenceManager sharedPreferenceManager;
    private String tag_banner_req = "tag_banner_req";
    private String tag_search_req = "tag_search_req";
    ViewPagination viewPagination;
    ViewPaginationButtom viewPaginationButtom;
    ArrayList<BannerModel> bannerarray;
    ArrayList<BannerModel> bannerarray1;
    ArrayList<BannerModel> bannerarray2;
    private ProgressDialog progressDialog;
    private ListView deal_view;
    private JSONArray mParentJSONArray = new JSONArray();
    private LinearLayout headerLayout;

    public ZoyMeHomeFragment() {
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
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        initView(rootview);
        initToolBar();
        return rootview;
    }

    /**
     * @param rootView
     */
    private void initView(View rootView) {
        CustomMethods.hideSoftKeyBoard(getActivity());

        search_edt = (TextView) rootView.findViewById(R.id.search_edt);
        deal_view = (ListView) rootView.findViewById(R.id.deal_view);


        View view1 = getActivity().getLayoutInflater().inflate(R.layout.listview_header, deal_view, false);
        headerLayout = (LinearLayout) view1.findViewById(R.id.header_layout);
        mHomeRecyclerView = (RecyclerView) view1.findViewById(R.id.recProductListing);
        rvCategories = (RecyclerView) view1.findViewById(R.id.rvCategories);
        viewpager = (ViewPager)view1.findViewById(R.id.mypanelproduct);
        mypanelproduct_offers = (ViewPager) view1.findViewById(R.id.mypanelproduct_offers);
        indicator = (CircleIndicator) view1.findViewById(R.id.indicator);

        deal_view.addHeaderView(headerLayout);

       // deal_view.setNestedScrollingEnabled(false);

        bannerarray = new ArrayList<>();
        bannerarray1 = new ArrayList<>();
        bannerarray2 =new ArrayList<>();


        sharedPreferenceManager = new SharedPreferenceManager(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        mHomeLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mHomeLinearLayoutManager1 = new GridLayoutManager(getActivity(), 2);
        mHomeCategoryLinearLayoutManager =new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);


        mZoyMeCategories = new ZoyMeCategories(getActivity(), ZoyMeHomeFragment.this, mParentJSONArray);
        mHomeRecyclerView.setLayoutManager(mHomeLinearLayoutManager);
        mHomeRecyclerView.setAdapter(mZoyMeCategories);

         deal_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Fragment selectedFragment = null;
                 selectedFragment = new ZoyMeOffersFragment();


                 FragmentTransaction transaction = getFragmentManager().beginTransaction();
                 transaction.replace(R.id.activity_home_container_main, selectedFragment);
                 transaction.commit();
             }
         });



        search_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ZoyMeSearchActivity.class));
            }
        });


/**
 *
 *  Banner Service
 *  Token "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vMTM5LjU5LjExLjI1MC9hcGkvYXV0aC9sb2dpbiIsImlhdCI6MTUxMDMwMTY3MSwiZXhwIjoxNTEwMzA1MjcxLCJuYmYiOjE1MTAzMDE2NzEsImp0aSI6IkJRY09BazZtc2tEZXFmZGsiLCJzdWIiOjIsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.QPe0-k87Jci1sI5LbpaGCL6q9Ws0wBzTMZ9A9MINR0Y"
 */
        if (Utils.getConnectivityStatusVal(getContext())) {
            getBanner();
        } else {
            CustomMethods.displayDialog(getContext(), getString(R.string.app_name), getString(R.string.check_connection));

        }


        /**
         *
         *
         * Token
         */

        if (Utils.getConnectivityStatusVal(getContext())) {
            getBannerOffer();
        } else {
            CustomMethods.displayDialog(getContext(), getString(R.string.app_name), getString(R.string.check_connection));
        }

/**
 *
 *  Category Service
 *  Token "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vMTM5LjU5LjExLjI1MC9hcGkvYXV0aC9sb2dpbiIsImlhdCI6MTUxMDMwMTY3MSwiZXhwIjoxNTEwMzA1MjcxLCJuYmYiOjE1MTAzMDE2NzEsImp0aSI6IkJRY09BazZtc2tEZXFmZGsiLCJzdWIiOjIsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.QPe0-k87Jci1sI5LbpaGCL6q9Ws0wBzTMZ9A9MINR0Y"
 */
        if (Utils.getConnectivityStatusVal(getContext())) {
            progressDialog.show();
            makeCategoryJsonRequest();
        } else {
            CustomMethods.displayDialog(getContext(), getString(R.string.app_name), getString(R.string.check_connection));
        }
    }


    private void getBannerOffer() {
        Map<String, String> sliderParams = new LinkedHashMap<>();
        sliderParams.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_OFFER_BANNNER, sliderParams, BannerOfferReponseListener,
                BannerErrorListener), tag_banner_req);

    }

    Response.Listener<JSONObject> BannerOfferReponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            Log.d("response", response.toString());
            int i;
            try {
                if (response.getInt("status") == 200) {
                    JSONObject jsonObject = response.getJSONObject("data");

                    JSONArray jsonArray = jsonObject.getJSONArray("offerbanners");
                    JSONArray jsonArray1 = jsonObject.getJSONArray("bigbanners");

                    for (i = 0; i < jsonArray.length(); i++) {
                        BannerModel bannermodel = new BannerModel();
                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        bannermodel.setImage(jsonObj.getString("image_url"));
                        bannermodel.setCategory_id(jsonObj.getString("category_id"));
                        bannermodel.setName(jsonObj.getString("name"));
                        bannerarray1.add(bannermodel);
                    }

                    for (i = 0; i < jsonArray1.length(); i++) {
                        BannerModel bannermodel = new BannerModel();
                        JSONObject jsonObj = jsonArray1.getJSONObject(i);

                        bannermodel.setImage(jsonObj.getString("image_url"));
                        bannermodel.setCategory_id(jsonObj.getString("category_id"));
                        bannermodel.setName(jsonObj.getString("name"));
                        bannerarray2.add(bannermodel);
                    }

                    zoyMeBannerAdapter = new ZoyMeBannerBaseAdapter(getActivity(), bannerarray2, ZoyMeHomeFragment.this);
                  //  deal_view.setLayoutManager(mHomeCategoryLinearLayoutManager);
                    deal_view.setAdapter(zoyMeBannerAdapter);
                    zoyMeBannerAdapter.notifyDataSetChanged();// Notify the adapter


                    viewPaginationButtom = new ViewPaginationButtom(getActivity(), bannerarray1, ZoyMeHomeFragment.this);
                    mypanelproduct_offers.setAdapter(viewPaginationButtom);

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


    private void getBanner() {
        Map<String, String> sliderParams = new LinkedHashMap<>();
        sliderParams.put("token", sharedPreferenceManager.getToken());
        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_BANNER, sliderParams, BannerReponseListener,
                BannerErrorListener), tag_banner_req);

    }


    Response.Listener<JSONObject> BannerReponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            Log.d("response", response.toString());
            int i;
            //  progress_banner.setVisibility(View.GONE);
            try {
                if (response.getInt("status") == 200) {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (i = 0; i < jsonArray.length(); i++) {
                        BannerModel bannermodel = new BannerModel();
                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        bannermodel.setImage(jsonObj.getString("image_url"));
                        bannermodel.setCategory_id(jsonObj.getString("category_id"));
                        bannermodel.setName(jsonObj.getString("name"));
                        bannerarray.add(bannermodel);
                    }


                    viewPagination = new ViewPagination(getActivity(), bannerarray, ZoyMeHomeFragment.this);
                    viewpager.setAdapter(viewPagination);
                    // indicator.setViewPager(viewpager);
                    // viewPagination.registerDataSetObserver(indicator.getDataSetObserver());
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

    Response.ErrorListener BannerErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("response", error.toString());


        }
    };


    private void makeCategoryJsonRequest() {
        CustomMethods.hideSoftKeyboard(getActivity());
        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST,
                UrlConstants.ZOY_ME_CATEGORY, params, categoryResponse,
                categoryListener), "category_req");
    }


    private Response.Listener<JSONObject> categoryResponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            Log.d("response", response.toString());
            progressDialog.hide();
            try {

                JSONObject mParentJSONObject = response.getJSONObject("data");
                mParentJSONArray = mParentJSONObject.getJSONArray("category");

                mZoyMeCategories = new ZoyMeCategories(getActivity(), ZoyMeHomeFragment.this, mParentJSONArray);
                mHomeRecyclerView.setAdapter(mZoyMeCategories);

            /*   new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ZoyMeCategories.category_cell_zero.performClick();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000);*/

            } catch (Exception ee) {
                ee.printStackTrace();
            }

        }

    };


    private Response.ErrorListener categoryListener = new Response.ErrorListener() {
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void setAppUserDataPostion(String mFlag, Object mObject, int mPosition) {
    }

    @Override
    public void subCategoryPosition(String mFlag, Object mObject, int mPosition, String mId, String Tittle) {

        Intent mIntent = new Intent(getActivity(), ZoyMeProductListActivity.class);
        mIntent.putExtra("tittle", Tittle);
        mIntent.putExtra("id", mId);
        startActivity(mIntent);


    }

    /**
     * To initialize toolbar
     */


    public void initToolBar() {
        if (getActivity() instanceof ZoyMeHomeActivity) {
            ((ZoyMeHomeActivity) getActivity()).showToolBar();
            ((ZoyMeHomeActivity) getActivity()).setupToolbar(false, getString(R.string.app_name), false, false, true);
        }
        final Toolbar toolBar = ((ZoyMeHomeActivity) getActivity()).getToolbar();
        toolBar.getMenu().clear();
    }


    @Override
    public void subImagePosition(String image_click) {

    }

    @Override
    public void subImagePositionValue(String image_click, String position, String Tittle, String Flag) {

        Intent mIntent = new Intent(getActivity(), ZoyMeProductListActivity.class);
        mIntent.putExtra("tittle", Tittle);
        mIntent.putExtra("id", position);
        mIntent.putExtra("offerbanner", Flag);
        startActivity(mIntent);
    }

    @Override
    public void setAppSubCategoryDataPostion(String mFlag, JSONArray mJsonSubCategory, int mPosition) {
        if (mFlag.equals("home_category_click")) {
            ArrayList<String> mNameArrayList = new ArrayList<String>();
            ArrayList<String> mImageArrayList = new ArrayList<String>();
            ArrayList<Integer> mIdArrayList = new ArrayList<Integer>();
            for (int i = 0; i < mJsonSubCategory.length(); i++) {
                JSONObject jsonDataObject1 = null;
                String mName = null;
                String mImage = null;
                int mid = 0;
                try {
                    jsonDataObject1 = (JSONObject) mJsonSubCategory.get(i);
                    mName = jsonDataObject1.getString("name");
                    mImage = jsonDataObject1.getString("image_url");
                    mid = jsonDataObject1.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mNameArrayList.add(mName);
                mImageArrayList.add(mImage);
                mIdArrayList.add(mid);
            }

            zoymeCategoriesAdapter = new ZoymeSubCategoriesAdapter(getContext(), mNameArrayList, mImageArrayList, mIdArrayList, ZoyMeHomeFragment.this);
            rvCategories.setLayoutManager(mHomeLinearLayoutManager1);
            rvCategories.setAdapter(zoymeCategoriesAdapter);
            zoymeCategoriesAdapter.notifyDataSetChanged();// Notify the adapter

        }
    }
}
