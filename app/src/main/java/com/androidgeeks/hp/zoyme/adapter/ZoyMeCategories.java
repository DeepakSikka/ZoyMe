package com.androidgeeks.hp.zoyme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.model.CategoryModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.AppUserListDataListener;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.SubCategoryInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak SIkka on 10/27/2017.
 */

public class ZoyMeCategories extends RecyclerView.Adapter<ZoyMeCategories.ViewHolder> {
    private Context mContext;
    private SubCategoryInterface dataListener;
    private JSONArray mParentCategory;

    public static LinearLayout category_cell_zero;

    public ZoyMeCategories(Context mContext, SubCategoryInterface dataListener, JSONArray mParentCategory) {
        this.mContext = mContext;
        this.dataListener = dataListener;
        this.mParentCategory = mParentCategory;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject mjsonObject = null;
        try {
            mjsonObject = (JSONObject) mParentCategory.get(position);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mjsonObject != null) {
            try {
                holder.category__text.setText(mjsonObject.getString("name"));
                Picasso.with(mContext).load(mjsonObject.getString("image_url")).placeholder(R.mipmap.placeholder_product).fit().centerCrop().into(holder.category__image);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (position == 0) {
            ZoyMeCategories.category_cell_zero = holder.category_cell;
        }
    }

    @Override
    public int getItemCount() {
        return mParentCategory.length();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LinearLayout category_cell;
        private TextView category__text;
        private ImageView category__image;


        public ViewHolder(View itemView) {
            super(itemView);
            getId(itemView);
            category_cell.setOnClickListener(this);

        }

        private void getId(View mView) {
            category_cell = (LinearLayout) mView.findViewById(R.id.category_cell);
            category__text = (TextView) mView.findViewById(R.id.category__text);
            category__image = (ImageView) mView.findViewById(R.id.category__image);
        }

        @Override
        public void onClick(View v) {
            if (v == category_cell) {
                JSONObject mJsonObject = null;
                JSONArray mSubCategoryJsonArray = null;
                try {
                    mJsonObject = (JSONObject) mParentCategory.get(getAdapterPosition());
                    mSubCategoryJsonArray = (JSONArray) mJsonObject.getJSONArray("subcategory");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataListener.setAppSubCategoryDataPostion("home_category_click", mSubCategoryJsonArray, getAdapterPosition());


            }

        }
    }

}
