package com.androidgeeks.hp.zoyme.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.androidgeeks.hp.zoyme.R;


@SuppressLint("AppCompatCustomView")
public class CustomEditText extends EditText {

    private Typeface fontType;

    public CustomEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode())
            return;
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        final String fontName = ta.getString(R.styleable.CustomTextView_font_name);
        final Typeface font = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font)+ fontName);
        setTypeface(font);
        ta.recycle();
    }
}
