package com.androidgeeks.hp.zoyme.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.model.AddressDetailModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.AddressListClick;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Deepak Sikka on 12/15/2017.
 */

public class BuyerProductAdapter extends RecyclerView.Adapter<BuyerProductAdapter.ViewHolder> {

    private Activity mContext;
    List<AddressDetailModel> addressDetailModels;
    private AddressListClick addressListener;


    public BuyerProductAdapter(Activity mContext, List<AddressDetailModel> addressDetailModels, AddressListClick addressListener) {
        this.mContext = mContext;
        this.addressDetailModels = addressDetailModels;
        this.addressListener = addressListener;

    }

    @Override
    public BuyerProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.buyerproductlist, parent, false);
        BuyerProductAdapter.ViewHolder holder = new BuyerProductAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BuyerProductAdapter.ViewHolder holder, int position) {

        final AddressDetailModel addressAdapter = addressDetailModels.get(position);

        holder.product_name_buyer.setText(addressAdapter.getName());
        String rupee = mContext.getResources().getString(R.string.Rs);
        holder.product_price_buyer.setText(rupee + " " + addressAdapter.getFlat());
        try {
            Picasso.with(mContext).load(addressAdapter.getLocality()).placeholder(R.drawable.banner_bg).fit().noFade().into(holder.buyer_image);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        holder.product_status_buyer.setText(addressAdapter.getCity());

    }

    @Override
    public int getItemCount() {
        return addressDetailModels.size();
    }

    public void removeItem(int position) {

        addressDetailModels.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, addressDetailModels.size());

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView product_name_buyer, product_price_buyer, product_status_buyer;
        private ImageView buyer_image;
        private LinearLayout delete_buyer;
        private LinearLayout edit_buyer;

        public ViewHolder(View itemView) {
            super(itemView);
            getId(itemView);
            delete_buyer.setOnClickListener(this);
            edit_buyer.setOnClickListener(this);
        }

        private void getId(View mView) {
            buyer_image = (ImageView) mView.findViewById(R.id.buyer_image);
            product_status_buyer = (TextView) mView.findViewById(R.id.product_status_buyer);
            product_name_buyer = (TextView) mView.findViewById(R.id.product_name_buyer);
            product_price_buyer = (TextView) mView.findViewById(R.id.product_price_buyer);
            edit_buyer = (LinearLayout) mView.findViewById(R.id.edit_buyer);

            delete_buyer = (LinearLayout) mView.findViewById(R.id.delete_buyer);

        }


        @Override
        public void onClick(View v) {
            if (v == delete_buyer) {
                addressListener.setAppDeletePostion("delete_product", addressDetailModels.get(getAdapterPosition()), getAdapterPosition());
            }
            if (v == edit_buyer) {
                addressListener.setAppUpdatePostion("update_product", addressDetailModels.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }
}
