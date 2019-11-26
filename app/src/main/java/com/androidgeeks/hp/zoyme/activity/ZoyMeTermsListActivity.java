package com.androidgeeks.hp.zoyme.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;

public class ZoyMeTermsListActivity extends AppCompatActivity {
    private ImageView toolbar_iv_back;
    private ImageView toolbar_iv_cart;
    private TextView toolbar_tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_terms_list);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_cart = (ImageView) findViewById(R.id.toolbar_iv_cart);

        toolbar_tv_title = (TextView) findViewById(R.id.toolbar_tv_title);


        toolbar_tv_title.setText("Term of use");

        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar_iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(ZoyMeTermsListActivity.this, ZoyMeCartActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }
}
