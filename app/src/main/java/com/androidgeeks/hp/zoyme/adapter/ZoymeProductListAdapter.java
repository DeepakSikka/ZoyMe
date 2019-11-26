package com.androidgeeks.hp.zoyme.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.model.ProductListingModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ProductList;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Deepak Sikka on 11/11/2017.
 */

public class ZoymeProductListAdapter extends RecyclerView.Adapter<ZoymeProductListAdapter.ViewHolder> {
    private Activity mContext;
    List<ProductListingModel> productListingModels;
    private ProductList productListener;

    public ZoymeProductListAdapter(Activity mContext, List<ProductListingModel> productListingModels, ProductList productListener) {
        this.mContext = mContext;
        this.productListingModels = productListingModels;
        this.productListener = productListener;
    }


    @Override
    public ZoymeProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.product_grid, parent, false);
        ZoymeProductListAdapter.ViewHolder holder = new ZoymeProductListAdapter.ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(ZoymeProductListAdapter.ViewHolder holder, int position) {

        final ProductListingModel productListingModel = productListingModels.get(position);

        if (productListingModel.getImage_url() == null) {
            Picasso.with(mContext).load(R.mipmap.placeholder_product).placeholder(R.mipmap.placeholder_product).fit().centerCrop().into(holder.product_img);

        } else {
            Picasso.with(mContext).load(productListingModel.getImage_url()).placeholder(R.mipmap.placeholder_product).fit().centerCrop().into(holder.product_img);
        }
        holder.product_name.setText(productListingModel.getName());
        holder.product_newprice.setText(productListingModel.getPrice());
        holder.product_newprice.setPaintFlags(holder.product_newprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG |Paint.FAKE_BOLD_TEXT_FLAG);

        holder.ratebar.setRating(Integer.parseInt(productListingModel.getRating()));

        if (productListingModel.getNew_price().equalsIgnoreCase("null") || productListingModel.getNew_price().equalsIgnoreCase("")) {
            holder.new_rupees_icon.setVisibility(View.INVISIBLE);
            holder.product_actualprice.setVisibility(View.INVISIBLE);
            holder.product_newprice.setText(productListingModel.getPrice());
            holder.product_newprice.setPaintFlags(holder.product_newprice.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

        } else {

            holder.new_rupees_icon.setVisibility(View.VISIBLE);
            holder.product_actualprice.setText(productListingModel.getNew_price());
        }

        if (productListingModel.getIs_like().equalsIgnoreCase("null") || Integer.parseInt(productListingModel.getIs_like()) == 0) {
            holder.wishlistclick.setImageResource(R.mipmap.heart_icon);
        } else {
            holder.wishlistclick.setImageResource(R.mipmap.heart_icon_pink);
        }


    }

    @Override
    public int getItemCount() {
        return productListingModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView product_img;
        private TextView product_name, product_newprice, product_actualprice;
        private LinearLayout productList;
        private RatingBar ratebar;
        private ImageView wishlistclick, new_rupees_icon;
        private View new_view;


        public ViewHolder(View itemView) {
            super(itemView);
            getId(itemView);
            productList.setOnClickListener(this);
            wishlistclick.setOnClickListener(this);

        }

        private void getId(View mView) {
            //  new_view=(View)mView.findViewById(R.id.new_view);
            new_rupees_icon = (ImageView) mView.findViewById(R.id.new_rupees_icon);
            product_img = (ImageView) mView.findViewById(R.id.product_img1);
            product_name = (TextView) mView.findViewById(R.id.product_name1);
            productList = (LinearLayout) mView.findViewById(R.id.productList1);
            product_newprice = (TextView) mView.findViewById(R.id.product_newprice);
            product_actualprice = (TextView) mView.findViewById(R.id.product_actualprice);
            wishlistclick = (ImageView) mView.findViewById(R.id.wishlistclick);

            ratebar = (RatingBar) mView.findViewById(R.id.ratebar);
        }

        @Override
        public void onClick(View v) {
            if (v == productList) {
                productListener.subProductPosition("product_click", productListingModels.get(getAdapterPosition()), getAdapterPosition(), productListingModels.get(getAdapterPosition()).getProduct_id(), productListingModels.get(getAdapterPosition()).getName());

            }
            if (v == wishlistclick) {

                productListener.subWishlistPosition("wishlist_click", productListingModels.get(getAdapterPosition()), getAdapterPosition(), productListingModels.get(getAdapterPosition()).getProduct_id(), wishlistclick);

            }
        }
    }

}
