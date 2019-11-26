package com.androidgeeks.hp.zoyme.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.model.CartModel;
import com.androidgeeks.hp.zoyme.model.SearchModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.CartDeleteList;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ProductList;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Deepak Sikka on 11/17/2017.
 */

public class CardViewAdapter extends BaseAdapter {
    private Activity activity;
    private List<CartModel> cartModels;
    LayoutInflater inflater;
    private CartDeleteList cartDeleteList;

    public CardViewAdapter(Activity activity, List<CartModel> cartModels, CartDeleteList cartDeleteList) {
        this.activity = activity;
        this.cartModels = cartModels;
        this.cartDeleteList = cartDeleteList;
    }

    public void removeItem(int position) {
        cartModels.remove(position);
    }

    @Override
    public int getCount() {
        return cartModels.size();
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
        TextView product_name_cart, product_size_cart, product_color_cart, product_price_cart, normal_et;
        ImageView cart_image;
        LinearLayout delete_cart;
        LinearLayout minus, plus;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.cart_adapter, parent, false);

        product_price_cart = (TextView) itemView.findViewById(R.id.product_price_cart);
        product_color_cart = (TextView) itemView.findViewById(R.id.product_color_cart);
        product_name_cart = (TextView) itemView.findViewById(R.id.product_name_cart);
        product_size_cart = (TextView) itemView.findViewById(R.id.product_size_cart);
        cart_image = (ImageView) itemView.findViewById(R.id.cart_image);
        delete_cart = (LinearLayout) itemView.findViewById(R.id.delete_cart);
        normal_et = (TextView) itemView.findViewById(R.id.normal_et);
        plus = (LinearLayout) itemView.findViewById(R.id.plus);
        minus = (LinearLayout) itemView.findViewById(R.id.minus);


        CartModel cartModeldata = cartModels.get(position);

        Picasso.with(activity).load(cartModeldata.getImage_url()).placeholder(R.drawable.banner_bg).fit().noFade().into(cart_image);

        product_name_cart.setText(cartModeldata.getName());
        if (cartModeldata.getSize_name().equals("")) {
            product_size_cart.setVisibility(View.GONE);
        } else {
            product_size_cart.setText("Size" + "-" + cartModeldata.getSize_name());

        }
        if (cartModeldata.getColor_name().equals("")) {
            product_color_cart.setVisibility(View.GONE);
        } else {
            product_color_cart.setText("Color" + "-" + cartModeldata.getColor_name());

        }
        String rupee = activity.getResources().getString(R.string.Rs);
        product_price_cart.setText(rupee + " " + cartModeldata.getFinal_price());
        normal_et.setText(cartModeldata.getTotal_quantity());

        delete_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartDeleteList.subDeletelistPosition("delete_click", position);

            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartDeleteList.subminuslistPosition("plus_click", position, Integer.parseInt(cartModels.get(position).getTotal_quantity()));
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartDeleteList.subminuslistPosition("minus_click", position, Integer.parseInt(cartModels.get(position).getTotal_quantity()));
            }
        });

        return itemView;
    }


}
