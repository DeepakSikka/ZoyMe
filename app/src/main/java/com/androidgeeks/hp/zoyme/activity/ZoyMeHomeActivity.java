package com.androidgeeks.hp.zoyme.activity;

/*
Created By Deepak SIkka on 25/10/2017 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;

import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.adapter.ExpandableListAdapter;
import com.androidgeeks.hp.zoyme.fragment.ZoyMeOrderFragment;
import com.androidgeeks.hp.zoyme.fragment.ZoyMeHomeFragment;
import com.androidgeeks.hp.zoyme.fragment.ZoyMeMoreFragment;
import com.androidgeeks.hp.zoyme.fragment.ZoyMeOffersFragment;
import com.androidgeeks.hp.zoyme.fragment.ZoyMeWishListFragment;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Container for all fragments, initialize toolbar and navigation drawer components
 */

public class ZoyMeHomeActivity extends AppCompatActivity {
    public Toolbar toolbar;
    public DrawerLayout drawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle drawerToggle;
    private TextView tvTitle;
    private ImageView ivcart, ivNotification;
    private ImageView ivback;
    private ImageView ivmenu;
    ZoyMeHomeFragment homeFragment;
    BottomNavigationView bottomNavigationView;
    BottomNavigationMenuView menuView;
    private String fragment_title;
    boolean isActivityRestarting;
    boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog progressDialog;
    SharedPreferenceManager sharedPreferenceManager;
    List<String> fakeList;
    List<String> fakeList_id;
    HashMap<String, List<String>> listDataChild_id;
    private HashMap<String, List<String>> listDataChild;
    ArrayList<HashMap<String, String>> nameList = new ArrayList<HashMap<String, String>>();
    public static String TAG_NAME = "name";
    public static String TAG_Image = "image_url";
    public static String TAG_ID = "header_id";
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    public static String child_name = "", child_id = "";
    private LinearLayout footerLayout;
    private LinearLayout headerLayout;
    private LinearLayout logout, mybuyer, mycart, myaccount, termsList, privacyList;
    private ImageView home_indicator;
    private ListView expendable_list_footer;
    ArrayList<String> staticList;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_home);
        initToolbar();


        initNavigationDrawer();
        setupToolbar(false, getString(R.string.app_name), true, false, true);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeHomeActivity.this);

        staticList = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        listDataChild_id = new HashMap<String, List<String>>();
        fakeList = new ArrayList<String>();
        fakeList_id = new ArrayList<String>();


        expListView = (ExpandableListView) findViewById(R.id.nav_list);


        View view1 = getLayoutInflater().inflate(R.layout.drawer_header, expListView, false);
        headerLayout = (LinearLayout) view1.findViewById(R.id.header_layout);
        home_indicator =(ImageView)view1.findViewById(R.id.home_indicator);

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }
        });
        expListView.addHeaderView(headerLayout);

        View view = getLayoutInflater().inflate(R.layout.footer_view, expListView, false);
        footerLayout = (LinearLayout) view.findViewById(R.id.footer_layout);

        mycart = (LinearLayout) view.findViewById(R.id.mycart);
        mybuyer = (LinearLayout) view.findViewById(R.id.mybuyer);
        myaccount = (LinearLayout) view.findViewById(R.id.myaccount);
        logout = (LinearLayout) view.findViewById(R.id.logout);
        termsList = (LinearLayout) view.findViewById(R.id.termsList);
        privacyList = (LinearLayout) view.findViewById(R.id.privacyList);

        expListView.addFooterView(footerLayout);

        homeFragment = new ZoyMeHomeFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_home_container_main, homeFragment);
        transaction.commit();


        termsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZoyMeHomeActivity.this, ZoyMeTermsListActivity.class);
                startActivity(intent);
            }
        });

        privacyList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZoyMeHomeActivity.this, ZoyMePrivacyListActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.getConnectivityStatusVal(ZoyMeHomeActivity.this)) {
                    progressDialog.show();
                    makeJsonObjLogout();
                } else {
                    CustomMethods.displayDialog(ZoyMeHomeActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
                }
            }
        });
        myaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZoyMeHomeActivity.this, ZoyMeMyAccountActivity.class);
                startActivity(intent);
            }
        });

        mycart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZoyMeHomeActivity.this, ZoyMeCartActivity.class);
                startActivity(intent);
            }
        });
        mybuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZoyMeHomeActivity.this, ZoyMeBuyerActivity.class);
                startActivity(intent);
            }
        });
        fragment_title = "Home";
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener()

                {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.zoyme_home:
                                selectedFragment = new ZoyMeHomeFragment();
                                fragment_title = "Home";
                                break;
                            case R.id.zoyme_user:
                                selectedFragment = new ZoyMeWishListFragment();
                                fragment_title = "User";
                                break;
                            case R.id.zoyme_offers:
                                selectedFragment = new ZoyMeOffersFragment();
                                fragment_title = "Offers";
                                break;
                            case R.id.zoyme_cart:
                                selectedFragment = new ZoyMeOrderFragment();
                                fragment_title = "Cart";
                                break;
                            case R.id.zoyme_more:
                                selectedFragment = new ZoyMeMoreFragment();
                                fragment_title = "More";
                                break;
                        }
                        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.activity_home_container_main, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });


        try

        {

            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");

            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {

                BottomNavigationItemView item =
                        (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                //To update view, set the checked value again
                item.setChecked(item.getItemData().isChecked());
            }


        } catch (
                NoSuchFieldException e)

        {
            e.printStackTrace();

        } catch (
                IllegalAccessException e)

        {
            e.printStackTrace();

        } catch (
                SecurityException e)

        {
            e.printStackTrace();
        }

        /**
         *
         *  Category Service
         *  Token "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOi8vMTM5LjU5LjExLjI1MC9hcGkvYXV0aC9sb2dpbiIsImlhdCI6MTUxMDMwMTY3MSwiZXhwIjoxNTEwMzA1MjcxLCJuYmYiOjE1MTAzMDE2NzEsImp0aSI6IkJRY09BazZtc2tEZXFmZGsiLCJzdWIiOjIsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.QPe0-k87Jci1sI5LbpaGCL6q9Ws0wBzTMZ9A9MINR0Y"
         */
        if (Utils.getConnectivityStatusVal(ZoyMeHomeActivity.this)) {
            progressDialog = new ProgressDialog(ZoyMeHomeActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");

            makeCategoryJsonRequest();
        } else {
            CustomMethods.displayDialog(ZoyMeHomeActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
        }


        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousItem = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousItem) {
                    expListView.collapseGroup(previousItem);
                }
                previousItem = groupPosition;
            }
        });


        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub


                String key_id =
                        listDataChild_id.get(nameList.get(groupPosition).get(TAG_NAME)).get(
                                childPosition);
                ZoyMeHomeActivity.child_name =
                        listDataChild.get(nameList.get(groupPosition).get(TAG_NAME)).get(
                                childPosition);
                ZoyMeHomeActivity.child_id =
                        listDataChild_id.get(nameList.get(groupPosition).get(TAG_NAME)).get(
                                childPosition);


                Intent in = new Intent(ZoyMeHomeActivity.this, ZoyMeProductListActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                in.putExtra("id", key_id);
                in.putExtra("tittle", child_name);

                startActivity(in);
                return false;
            }
        });

    }

    private void makeJsonObjLogout() {


        Map<String, String> params = new LinkedHashMap<>();
        params.put("token", sharedPreferenceManager.getToken());

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, UrlConstants.ZOY_ME_LOGOUT, params, logoutResponseListener, logoutResponseError), "tag_logout_req");
    }

    Response.Listener<JSONObject> logoutResponseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.d("response", response.toString());
            try {
                progressDialog.hide();
                if (response.getInt("status") == 200) {

                    String message = response.getString("message");

                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeHomeActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(message);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeHomeActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            Intent intent = new Intent(ZoyMeHomeActivity.this, ZoyMeSplashScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            sharedPreferenceManager.setRememberMe(false);
                            new SharedPreferenceManager(ZoyMeHomeActivity.this).clearall();

                        }
                    });
                    if (!((Activity) ZoyMeHomeActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                } else {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ZoyMeHomeActivity.this).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(response.getString("message"));
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, ZoyMeHomeActivity.this.getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            //     dialog.dismiss();

                        }
                    });
                    if (!((Activity) ZoyMeHomeActivity.this).isFinishing()) {

                        alertDialog.show();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };


    Response.ErrorListener logoutResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


        }
    };

    /**
     * To initialize toolbar
     */

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.toolbar_tv_title);
        ivNotification = (ImageView) toolbar.findViewById(R.id.toolbar_iv_cart);
        ivcart = (ImageView) toolbar.findViewById(R.id.toolbar_iv_cart);
        ivback = (ImageView) toolbar.findViewById(R.id.toolbar_iv_back);
        ivmenu = (ImageView) toolbar.findViewById(R.id.toolbar_iv_menu);
    }

    /**
     * setupToolbar:-This method sets up custom tool bar with title and back button where ever needed
     *
     * @param showBack:-         For displaying back button
     * @param title:-            toolbar title
     * @param showNotification:- to display notification_icon icon
     * @param showCart:-         to display Cart icon
     * @param bgColor:           to set toolbar backgroung color
     */
    public void setupToolbar(final boolean showBack, final String title, final boolean showNotification, final boolean showCart, final boolean bgColor) {
        setUpDrawerToggle(drawerLayout);
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerToggle.setHomeAsUpIndicator(null);
        if (bgColor) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        if (showBack) {
            setDrawerLock(true);

            ivback.setVisibility(View.VISIBLE);
            ivmenu.setVisibility(View.GONE);

        } else {
            setDrawerLock(false);

            ivmenu.setVisibility(View.VISIBLE);
            ivback.setVisibility(View.GONE);
        }

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeHomeActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        ivmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNavigationView != null) {
                    setupDrawerContent(mNavigationView);
                }
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        tvTitle.setText(title);


        drawerToggle.syncState();
    }

    private void setUpDrawerToggle(DrawerLayout drawerLayout) {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.open_drawer,  /* "open drawer" description */
                R.string.close_drawer  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(final View view) {
                super.onDrawerClosed(view);
                drawerToggle.syncState();
            }

            @Override
            public void onDrawerSlide(final View drawerView, final float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                drawerToggle.syncState();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(final View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerToggle.syncState();
            }

        };
        drawerLayout.setDrawerListener(drawerToggle);


    }

    private void makeCategoryJsonRequest() {
        CustomMethods.hideSoftKeyboard(ZoyMeHomeActivity.this);
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
                JSONArray mParentJSONArray = mParentJSONObject.getJSONArray("category");

                JSONArray jsonResultArray1 = null;

                for (int i = 0; i < mParentJSONArray.length(); i++) {

                    JSONObject jsonObject = mParentJSONArray.getJSONObject(i);

                    HashMap<String, String> category = new HashMap<String, String>();
                    category.put(TAG_NAME, jsonObject.getString("name"));
                    category.put(TAG_Image, jsonObject.getString("image_url"));
                    category.put(TAG_ID, jsonObject.getString("id"));
                    nameList.add(category);


                    jsonResultArray1 = jsonObject.getJSONArray("subcategory");

                    List<String> arrayDatList = new ArrayList<String>();
                    List<String> arrayDatList_id = new ArrayList<String>();

                    for (int j = 0; j < jsonResultArray1.length(); j++) {

                        JSONObject jso = jsonResultArray1.getJSONObject(j);
                        arrayDatList.add(jso.getString("name"));
                        arrayDatList_id.add(jso.getString("id"));
                    }
                    fakeList = arrayDatList;
                    fakeList_id = arrayDatList_id;
                    listDataChild.put(nameList.get(i).get(TAG_NAME), fakeList);
                    listDataChild_id.put(nameList.get(i).get(TAG_NAME), fakeList_id);

                    listAdapter = new ExpandableListAdapter(ZoyMeHomeActivity.this, nameList, listDataChild, listDataChild_id);

                    expListView.setAdapter(listAdapter);


                }
            } catch (Exception e) {

                Log.e("Exception", e.toString());
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
                    Toast.makeText(ZoyMeHomeActivity.this, jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                Toast.makeText(ZoyMeHomeActivity.this, getString(R.string.no_network_error), Toast.LENGTH_SHORT).show();
            }
        }
    };


    /**
     * To check whether navigation drawer is open
     *
     * @return true or false
     */
    private boolean isNavDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    /**
     * To close navigation drawer
     */
    private void closeNavDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setDrawerLock(final boolean isLock) {
        if (isLock) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    private void setupDrawerContent(final NavigationView navigationView) {


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        if (mNavigationView != null) {
                            setupDrawerContent(mNavigationView);
                        }

                        drawerLayout.closeDrawers();  // CLOSE DRAWER
                        return true;


                    }
                });
    }


    public void showToolBar() {
        toolbar.setVisibility(View.VISIBLE);
    }


    public Toolbar getToolbar() {
        return toolbar;
    }


    private void initNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);


        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }


    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.activity_home_container_main) instanceof ZoyMeOffersFragment) {
            final Intent intent = new Intent(this, ZoyMeHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else if (back_pressed + 3000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(ZoyMeHomeActivity.this, "Press one more time to exit", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();

        }

    }

}
