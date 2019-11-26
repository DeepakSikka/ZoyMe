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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.activity.ZoyMeAddressActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeHomeActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeLoginActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeOrderActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeReviewActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeSettingActivity;
import com.androidgeeks.hp.zoyme.activity.ZoyMeSplashScreenActivity;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ZoyMeMoreFragment extends Fragment {

    private ProgressDialog progressDialog;
    private SharedPreferenceManager sharedPreferenceManager;
    private ImageView order_layout;
    private ImageView review_layout;
    private ImageView address_layout;
    private ImageView setting_layout;
    private Button logout_layout;
    private TextView user;
    private TextView mobile;

    public ZoyMeMoreFragment() {
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
        View rootview = inflater.inflate(R.layout.fragment_more, container, false);
        initToolBar();
        initView(rootview);
        return rootview;
    }

    /**
     * @param rootview
     */
    private void initView(View rootview) {
        user = (TextView) rootview.findViewById(R.id.user);
        mobile = (TextView) rootview.findViewById(R.id.mobile);

        order_layout = (ImageView) rootview.findViewById(R.id.order_layout);
        review_layout = (ImageView) rootview.findViewById(R.id.review_layout);
        address_layout = (ImageView) rootview.findViewById(R.id.address_layout);
        setting_layout = (ImageView) rootview.findViewById(R.id.setting_layout);
        logout_layout = (Button) rootview.findViewById(R.id.logout_layout);


        sharedPreferenceManager = new SharedPreferenceManager(getContext());
        user.setText(sharedPreferenceManager.getFirstName() + " " + sharedPreferenceManager.getLastName());
        mobile.setText(sharedPreferenceManager.getMobile());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");


        setting_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getContext(), ZoyMeSettingActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
        address_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getContext(), ZoyMeAddressActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }
        });
        logout_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.getConnectivityStatusVal(getContext())) {
                    progressDialog.show();
                    makeJsonObjLogout();
                } else {
                    CustomMethods.displayDialog(getContext(), getString(R.string.app_name), getString(R.string.check_connection));
                }
            }
        });


        order_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getContext(), ZoyMeOrderActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        review_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getContext(), ZoyMeReviewActivity.class);
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
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

                    final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle(R.string.app_name);
                    alertDialog.setCancelable(false);

                    alertDialog.setMessage(message);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            Intent intent = new Intent(getActivity(), ZoyMeSplashScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();
                            sharedPreferenceManager.setRememberMe(false);
                            new SharedPreferenceManager(getActivity()).clearall();

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


    Response.ErrorListener logoutResponseError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Log.d("response", error.toString());


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
            ((ZoyMeHomeActivity) getActivity()).setupToolbar(true, getString(R.string.My_Account), true, true, true);
        }
        final Toolbar toolBar = ((ZoyMeHomeActivity) getActivity()).getToolbar();
        toolBar.getMenu().clear();
    }


}
