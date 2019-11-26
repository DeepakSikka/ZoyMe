package com.androidgeeks.hp.zoyme.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;

/**
 * Created by Deepak Sikka on 12/22/2017.
 */


public class CustomTextView extends TextView {
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "font/Roboto-BlackItalic.ttf"));

    }
}
