package com.androidgeeks.hp.zoyme.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.model.BannerModel;
import com.androidgeeks.hp.zoyme.model.CartModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.AppUserListDataListener;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ImageClick;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hp on 1/11/2018.
 */

public class ZoyMeBannerBaseAdapter extends BaseAdapter {


    Activity context;
    LayoutInflater inflater;
    ArrayList<BannerModel> arrayList;
    ImageClick imageClick;
    private AppUserListDataListener dataListener;

    public ZoyMeBannerBaseAdapter(Activity context, ArrayList<BannerModel> arrayList, AppUserListDataListener dataListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.dataListener = dataListener;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ImageView imageView1;
        FrameLayout subcategoryclick;


        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.banner_vertical, parent, false);


        imageView1 = (ImageView) itemView.findViewById(R.id.imageView1);

        subcategoryclick = (FrameLayout) itemView.findViewById(R.id.subcategoryclick);

        BannerModel bannerModel = arrayList.get(position);
        Picasso.with(context).load(bannerModel.getImage()).placeholder(R.mipmap.deals).into(imageView1);

        subcategoryclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataListener.subCategoryPosition("home_category_click", null, position, "" + arrayList.get(position), arrayList.get(position).getName());

            }
        });
        return itemView;
    }


}
