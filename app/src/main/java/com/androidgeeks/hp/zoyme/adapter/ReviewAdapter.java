package com.androidgeeks.hp.zoyme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.model.OrderModel;
import com.androidgeeks.hp.zoyme.model.ReviewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak Sikka on 11/19/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private Context context;
    List<ReviewModel> reviewModels;


    public ReviewAdapter(Context context, List<ReviewModel> reviewModels) {
        this.context = context;
        this.reviewModels = reviewModels;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reviews, parent, false);

        ReviewAdapter.ViewHolder holder = new ReviewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        final ReviewModel reviewModel = reviewModels.get(position);

        holder.product_name.setText(reviewModel.getName());
        holder.ratingbar.setRating(Integer.parseInt(reviewModel.getRating()));
        Picasso.with(context).load(reviewModel.getImage_url()).placeholder(R.drawable.banner_bg).fit().noFade().into(holder.review_image);

    }

    @Override
    public int getItemCount() {
        return reviewModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView product_name;
        private ImageView review_image;
        private RatingBar ratingbar;

        public ViewHolder(View view) {
            super(view);
            product_name = (TextView) view.findViewById(R.id.product_name);
            review_image = (ImageView) view.findViewById(R.id.review_image);
            ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);


        }
    }
}

