package com.androidgeeks.hp.zoyme.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.adapter.ZoymeSubCategoriesAdapter;
import com.androidgeeks.hp.zoyme.fragment.ZoyMeHomeFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ZoyMeSubCategories extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_sub_categories);




    }
}
