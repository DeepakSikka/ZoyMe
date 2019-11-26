package com.androidgeeks.hp.zoyme.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.model.BannerModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ImageClick;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Deepak Sikka on 11/30/2017.
 */

public class ProductDetailAdapter extends PagerAdapter {
    Activity context;
    LayoutInflater inflater;
    ArrayList<BannerModel> arrayList;
    ImageClick imageClick;

    public ProductDetailAdapter(Activity context, ArrayList<BannerModel> arrayList, ImageClick imageClick) {
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
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgflag;


        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.product_detail_layout, container, false);

        BannerModel bannerModel = arrayList.get(position);
        imgflag = (ImageView) itemView.findViewById(R.id.flag);


        Picasso.with(context).load(bannerModel.getImage()).placeholder(R.mipmap.main_banner_placeholder).fit().noFade().into(imgflag);


        imgflag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageClick.subImagePosition("image_click");

            }
        });
        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);

    }
}