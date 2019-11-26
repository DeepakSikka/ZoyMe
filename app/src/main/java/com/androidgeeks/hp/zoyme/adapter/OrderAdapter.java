package com.androidgeeks.hp.zoyme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.activity.ZoyMeBuyerOrderDetailActivity;
import com.androidgeeks.hp.zoyme.model.AddressDetailModel;
import com.androidgeeks.hp.zoyme.model.OrderModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.OrderSubmitClick;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak sikka on 11/19/2017.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context mcontext;
    List<OrderModel> orderModels;
    OrderSubmitClick orderSubmitClick;

    public OrderAdapter(Context mcontext, List<OrderModel> orderModels, OrderSubmitClick orderSubmitClick) {
        this.mcontext = mcontext;
        this.orderModels = orderModels;
        this.orderSubmitClick = orderSubmitClick;
    }


    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders, parent, false);

        OrderAdapter.ViewHolder holder = new OrderAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {
        final OrderModel orderModel = orderModels.get(position);

        holder.proname.setText(orderModel.getName());
        holder.delivered.setText(orderModel.getStatus_name());

        Picasso.with(mcontext).load(orderModel.getImage_url()).placeholder(R.drawable.banner_bg).fit().noFade().into(holder.order_image);

        if (orderModel.getRating().equalsIgnoreCase("")) {
            holder.ratingbar.setVisibility(View.GONE);

        } else {
            holder.ratingbar.setVisibility(View.VISIBLE);
            holder.ratingbar.setRating(Integer.parseInt(orderModel.getRating()));
        }

        if (orderModel.getStatus_name().equalsIgnoreCase("Delivered")) {
            holder.ratingbar.setVisibility(View.VISIBLE);
            holder.Share_review.setVisibility(View.VISIBLE);
            holder.review_submit.setVisibility(View.VISIBLE);

        }
        if (orderModel.getStatus_name().equalsIgnoreCase("Delivered") && orderModel.getRating().equalsIgnoreCase("")){
            holder.review_submit.setVisibility(View.VISIBLE);
        }else{
            holder.review_submit.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView proname;
        private TextView delivered;
        private ImageView order_image;
        private TextView Share_review;
        private RatingBar ratingbar;
        private Button review_submit;

        public ViewHolder(View view) {
            super(view);

            proname = (TextView) view.findViewById(R.id.proname);
            delivered = (TextView) view.findViewById(R.id.delivered);
            order_image = (ImageView) view.findViewById(R.id.order_image);
            ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);
            Share_review = (TextView) view.findViewById(R.id.Share_review);
            review_submit = (Button) view.findViewById(R.id.review_submit);

            review_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rating = String.valueOf(ratingbar.getRating());
                    orderSubmitClick.subOrderClick("submit_review", orderModels.get(getAdapterPosition()), getAdapterPosition(), rating);

                }
            });

        }
    }
}
