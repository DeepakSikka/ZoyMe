package com.androidgeeks.hp.zoyme.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;


import com.androidgeeks.hp.zoyme.R;

import java.util.Calendar;

public class BetterSpinner extends AutoCompleteTextView implements AdapterView.OnItemClickListener {

    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    private boolean isPopup;
    private int mPosition = ListView.INVALID_POSITION;

    public BetterSpinner(final Context context) {
        super(context);
        setOnItemClickListener(this);
    }

    public BetterSpinner(final Context arg0, final AttributeSet arg1) {
        super(arg0, arg1);
        final TypedArray ta = arg0.obtainStyledAttributes(arg1, R.styleable.CustomTextView);
        final String fontName = ta.getString(R.styleable.CustomTextView_font_name);

        final Typeface font = Typeface.createFromAsset(arg0.getAssets(), "font/" + fontName);
        setTypeface(font);
        ta.recycle();
        setOnItemClickListener(this);
    }

    public BetterSpinner(final Context arg0, final AttributeSet arg1, final int arg2) {
        super(arg0, arg1, arg2);
        final TypedArray ta = arg0.obtainStyledAttributes(arg1, R.styleable.CustomTextView);
        final String fontName = ta.getString(R.styleable.CustomTextView_font_name);

        final Typeface font = Typeface.createFromAsset(arg0.getAssets(), "font/" + fontName);
        setTypeface(font);
        ta.recycle();
        setOnItemClickListener(this);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(final boolean focused, final int direction,
                                  final Rect previouslyFocusedRect) {
        try {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);

            if (focused) {
                performFiltering("", 0);
                final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
                setKeyListener(null);
                dismissDropDown();
            } else {
                isPopup = false;
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (!isEnabled())
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            }
            case MotionEvent.ACTION_UP: {
                try {
                    final long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;

                    if (clickDuration < MAX_CLICK_DURATION) {
                        if (isPopup) {
                            dismissDropDown();
                            isPopup = false;
                        } else {
                            requestFocus();
                            showDropDown();
                            isPopup = true;
                        }
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
        mPosition = position;
        isPopup = false;
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(final Drawable left, final Drawable top, final Drawable right, final Drawable bottom) {

        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    public int getPosition() {
        return mPosition;
    }

    public void setOnItemClickListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
    }
}
