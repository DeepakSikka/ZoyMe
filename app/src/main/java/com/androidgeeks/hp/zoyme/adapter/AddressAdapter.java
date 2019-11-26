package com.androidgeeks.hp.zoyme.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.activity.ZoyMeAddressActivity;
import com.androidgeeks.hp.zoyme.model.AddressDetailModel;
import com.androidgeeks.hp.zoyme.model.ProductListingModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.AddressListClick;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.ProductList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak Sikka on 11/20/2017.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Activity mContext;
    List<AddressDetailModel> addressDetailModels;
    private AddressListClick addressListener;


    public AddressAdapter(Activity mContext, List<AddressDetailModel> addressDetailModels, AddressListClick addressListener) {
        this.mContext = mContext;
        this.addressDetailModels = addressDetailModels;
        this.addressListener = addressListener;

    }

    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saved_address, parent, false);
        AddressAdapter.ViewHolder holder = new AddressAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AddressAdapter.ViewHolder holder, int position) {

        final AddressDetailModel addressAdapter = addressDetailModels.get(position);

        holder.username.setText(addressAdapter.getName());
        holder.saved_address.setText(addressAdapter.getFlat() + "," + addressAdapter.getLocality() + ",");
        holder.saved_state.setText(addressAdapter.getCity() + "," + addressAdapter.getState());
        holder.saved_mobile.setText(addressAdapter.getPhone());

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

        private TextView username, saved_address, saved_state, saved_mobile;
        private ImageView edit_address, delete_address;
        private LinearLayout edit_address_click,delete_address_click;


        public ViewHolder(View itemView) {
            super(itemView);
            getId(itemView);
            delete_address_click.setOnClickListener(this);
            edit_address_click.setOnClickListener(this);
        }

        private void getId(View mView) {

            username = (TextView) mView.findViewById(R.id.username);
            saved_address = (TextView) mView.findViewById(R.id.saved_address);
            saved_mobile = (TextView) mView.findViewById(R.id.saved_mobile);
            saved_state = (TextView) mView.findViewById(R.id.saved_state);
            edit_address = (ImageView) mView.findViewById(R.id.edit_address);
            delete_address = (ImageView) mView.findViewById(R.id.delete_address);
            edit_address_click = (LinearLayout)mView.findViewById(R.id.edit_address_click);

            delete_address_click =(LinearLayout)mView.findViewById(R.id.delete_address_click);

        }


        @Override
        public void onClick(View v) {
            if (v == delete_address_click) {
                addressListener.setAppDeletePostion("delete_address", addressDetailModels.get(getAdapterPosition()), getAdapterPosition());
            }
            if (v == edit_address_click) {
                addressListener.setAppUpdatePostion("update_address", addressDetailModels.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }
}
