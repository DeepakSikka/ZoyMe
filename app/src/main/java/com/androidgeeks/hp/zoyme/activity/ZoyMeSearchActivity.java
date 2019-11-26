package com.androidgeeks.hp.zoyme.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidgeeks.hp.zoyme.AppController;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.adapter.SearchListAdaptor;
import com.androidgeeks.hp.zoyme.helper.CustomRequest;
import com.androidgeeks.hp.zoyme.model.SearchModel;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.UrlConstants;
import com.androidgeeks.hp.zoyme.utils.Utils;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.SearchClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ZoyMeSearchActivity extends AppCompatActivity implements SearchClick {
    private EditText mSearch;
    private RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    private SharedPreferenceManager sharedPreferenceManager;
    ArrayList<SearchModel> myDataList;
    private TextView no_record_tv;
    SearchListAdaptor searchListAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_search);


        mSearch = (EditText) findViewById(R.id.edtvw);
        mRecyclerView = (RecyclerView) findViewById(R.id.recvw);
        no_record_tv = (TextView) findViewById(R.id.no_record_tv);

        mLayoutManager = new LinearLayoutManager(ZoyMeSearchActivity.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ZoyMeSearchActivity.this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setLayoutManager(mLayoutManager);

        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeSearchActivity.this);

        myDataList = new ArrayList<>();

        searchListAdaptor = new SearchListAdaptor(this, myDataList, ZoyMeSearchActivity.this);
        mRecyclerView.setAdapter(searchListAdaptor);


        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            private Timer timer = new Timer();
            private final long DELAY = 500;

            @Override
            public void afterTextChanged(Editable editable) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {

                                if (Utils.getConnectivityStatusVal(ZoyMeSearchActivity.this)) {

                                    myDataList.clear();
                                    if (mSearch.getText().toString().trim().equalsIgnoreCase("")) {
                                        // makeJsonRequest(UrlContant.SELFIELIST, "");
                                        myDataList.clear();
                                    } else {
                                        makeJsonRequest(UrlConstants.ZOY_ME_SEARCH, "" + mSearch.getText().toString().trim());
                                    }

                                } else {
                                    CustomMethods.displayDialog(ZoyMeSearchActivity.this, getString(R.string.app_name), getString(R.string.check_connection));
                                }
                            }
                        },
                        DELAY
                );
            }
        });
    }


    private void makeJsonRequest(String mUrl, String mSearchWord) {
        myDataList.clear();
        Map<String, String> searchParam = new LinkedHashMap<>();
        searchParam.put("search_value", mSearchWord);
        searchParam.put("token", sharedPreferenceManager.getToken());

        AppController.getInstance().addToRequestQueue(new CustomRequest(Request.Method.POST, mUrl, searchParam, SearchReponse,
                SearchError), "tag_search_req");


    }


    Response.Listener<JSONObject> SearchReponse = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            searchListAdaptor.notifyDataSetChanged();
            Log.d("response", response.toString());
            try {

                String message = response.getString("message");
                if (response.getInt("status") == 200) {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        SearchModel searchmodel = new SearchModel();
                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        searchmodel.setId(jsonObj.getString("id"));
                        searchmodel.setName(jsonObj.getString("name"));
                        searchmodel.setDescription(jsonObj.getString("description"));
                        searchmodel.setPrice(jsonObj.getString("price"));
                        searchmodel.setQuantity(jsonObj.getString("quantity"));
                        searchmodel.setCategory_id(jsonObj.getString("category_id"));


                        myDataList.add(searchmodel);


                    }
                    searchListAdaptor.notifyDataSetChanged();
                } else {
                    Toast.makeText(ZoyMeSearchActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();

                }
                if (myDataList.size() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    no_record_tv.setVisibility(View.GONE);

                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    no_record_tv.setVisibility(View.VISIBLE);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    Response.ErrorListener SearchError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("response", error.toString());


        }
    };

    @Override
    public void subSearchPosition(String mFlag, Object mObject, int mPosition, String mId, String mTittle) {

        Intent mIntent = new Intent(ZoyMeSearchActivity.this, ZoyMeProductListActivity.class);
        mIntent.putExtra("tittle", mTittle);
        mIntent.putExtra("id", mId);
        startActivity(mIntent);
    }
}
