package com.androidgeeks.hp.zoyme.activity;


/*
Created By Deepak SIkka on 25/10/2017

 */

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.preferenceManager.SharedPreferenceManager;
import com.androidgeeks.hp.zoyme.utils.CustomMethods;
import com.androidgeeks.hp.zoyme.utils.Utils;

public class ZoyMeSplashScreenActivity extends AppCompatActivity {
    private SharedPreferenceManager sharedPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        sharedPreferenceManager = new SharedPreferenceManager(ZoyMeSplashScreenActivity.this);


        if (Utils.getConnectivityStatusVal(ZoyMeSplashScreenActivity.this)) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!(sharedPreferenceManager.getRememberMe())) {

                        Intent loginIntent = new Intent(ZoyMeSplashScreenActivity.this, ZoyMeLoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else {

                        Intent loginIntent = new Intent(ZoyMeSplashScreenActivity.this, ZoyMeHomeActivity.class);
                        startActivity(loginIntent);
                        finish();
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                    }

                }
            }, 3000);
        } else {
            CustomMethods.displayDialog(this, getString(R.string.app_name), getString(R.string.check_connection));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
