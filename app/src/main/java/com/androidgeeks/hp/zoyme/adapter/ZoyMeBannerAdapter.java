package com.androidgeeks.hp.zoyme.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.model.BannerModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.AppUserListDataListener;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ImageClick;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Deepak Sikka on 1/4/2018.
 */

public class ZoyMeBannerAdapter extends RecyclerView.Adapter<ZoyMeBannerAdapter.ViewHolder> {


    Activity context;
    LayoutInflater inflater;
    ArrayList<BannerModel> arrayList;
    ImageClick imageClick;
    private AppUserListDataListener dataListener;

    public ZoyMeBannerAdapter(Activity context, ArrayList<BannerModel> arrayList, AppUserListDataListener dataListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.dataListener = dataListener;
    }

    @Override
    public ZoyMeBannerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_vertical, parent, false);
        ZoyMeBannerAdapter.ViewHolder holder = new ZoyMeBannerAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ZoyMeBannerAdapter.ViewHolder holder, int position) {
        BannerModel bannerModel = arrayList.get(position);
        Picasso.with(context).load(bannerModel.getImage()).placeholder(R.mipmap.deals).into(holder.imageView1);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView1;
        private FrameLayout subcategoryclick;

        public ViewHolder(View itemView) {
            super(itemView);
            getId(itemView);
            subcategoryclick.setOnClickListener(this);

        }

        private void getId(View mView) {

            imageView1 = (ImageView) mView.findViewById(R.id.imageView1);

            subcategoryclick = (FrameLayout) mView.findViewById(R.id.subcategoryclick);

        }

        @Override
        public void onClick(View v) {
            if (v == subcategoryclick) {
                dataListener.subCategoryPosition("home_category_click", null, getAdapterPosition(), "" + arrayList.get(getAdapterPosition()), arrayList.get(getAdapterPosition()).getName());


            }
        }
    }

}
