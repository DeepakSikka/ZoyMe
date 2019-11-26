package com.androidgeeks.hp.zoyme.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.androidgeeks.hp.zoyme.R;

public class ZoyMeBuyerActivity extends AppCompatActivity {

    private Button bussiness_detail;
    private Button bank_detail;
    private Button product_detail;
    private Button order_detail;
    private ImageView toolbar_iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoy_me_buyer);
        bussiness_detail = (Button) findViewById(R.id.bussiness_detail);
        bank_detail = (Button) findViewById(R.id.bank_detail);
        product_detail = (Button) findViewById(R.id.product_detail);
        order_detail = (Button) findViewById(R.id.order_detail);
        toolbar_iv_back = (ImageView) findViewById(R.id.toolbar_iv_back);
        toolbar_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        bussiness_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(ZoyMeBuyerActivity.this, ZoyMeBussinesDetailActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        bank_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(ZoyMeBuyerActivity.this, ZoyMeBankDetailActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        product_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(ZoyMeBuyerActivity.this, ZoyMeBuyerProductDetailActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(ZoyMeBuyerActivity.this, ZoyMeBuyerOrderDetailActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

    }
}
