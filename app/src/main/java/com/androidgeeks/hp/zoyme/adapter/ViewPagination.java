package com.androidgeeks.hp.zoyme.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.activity.ZoyMeProductDetailActivity;
import com.androidgeeks.hp.zoyme.model.BannerModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ImageClick;
import com.squareup.picasso.Picasso;


import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by deepak Sikka on 11/09/17.
 */
public class ViewPagination extends PagerAdapter {
    Activity context;
    LayoutInflater inflater;
    ArrayList<BannerModel> arrayList;
    ImageClick imageClick;

    public ViewPagination(Activity context, ArrayList<BannerModel> arrayList, ImageClick imageClick) {
        this.context = context;
        this.arrayList = arrayList;
        this.imageClick = imageClick;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView imgflag;


        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.view_layout, container, false);

        BannerModel bannerModel = arrayList.get(position);
        imgflag = (ImageView) itemView.findViewById(R.id.flag);


        Picasso.with(context).load(bannerModel.getImage()).placeholder(R.mipmap.main_banner_placeholder).fit().noFade().into(imgflag);


        imgflag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageClick.subImagePositionValue("image_click", arrayList.get(position).getCategory_id(),arrayList.get(position).getName(),"productDetail");
            }
        });
        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        //((ViewPager) container).removeView((CardView) object);

    }

    @Override
    public float getPageWidth(int position) {
        if (position == 0) {
            return (0.8f);
        } else {
            return (0.8f);
        }
    }
}