package com.androidgeeks.hp.zoyme.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.activity.ZoyMeSubCategories;
import com.androidgeeks.hp.zoyme.model.CategoryModel;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.AppUserListDataListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak Sikka on 10/27/2017.
 */

public class ZoymeSubCategoriesAdapter extends RecyclerView.Adapter<ZoymeSubCategoriesAdapter.ViewHolder> {
    private Context mContext;
    private AppUserListDataListener dataListener;
    private ArrayList<String> mNameArrayList = new ArrayList<String>();
    private ArrayList<String> mImageArrayList = new ArrayList<String>();
    private ArrayList<Integer> mIdArrayList = new ArrayList<Integer>();


    private static final int TYPE_FOOTER = 0;
    private static final int TYPE_ITEM = 1;

    public ZoymeSubCategoriesAdapter(Context mContext, ArrayList<String> mNameArrayList, ArrayList<String> mImageArrayList, ArrayList<Integer> mIdArrayList, AppUserListDataListener dataListener) {
        this.mContext = mContext;
        this.dataListener = dataListener;
        this.mNameArrayList = mNameArrayList;
        this.mImageArrayList = mImageArrayList;
        this.mIdArrayList = mIdArrayList;
    }



    @Override
    public ZoymeSubCategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.categories_grid, parent, false);
        ZoymeSubCategoriesAdapter.ViewHolder holder = new ZoymeSubCategoriesAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ZoymeSubCategoriesAdapter.ViewHolder holder, int position) {


        /*final CategoryModel.SubCategoryModel subCategoryModelList = subCategoryModels.get(position);*/
        Picasso.with(mContext).load(mImageArrayList.get(position)).placeholder(R.mipmap.product_placeholder).fit().centerCrop().into(holder.imageView1);
        holder.txtTitle1.setText(mNameArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mIdArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView1;
        private TextView txtTitle1;
        private FrameLayout subcategoryclick;

        public ViewHolder(View itemView) {
            super(itemView);
            getId(itemView);
            subcategoryclick.setOnClickListener(this);

        }

        private void getId(View mView) {

            imageView1 = (ImageView) mView.findViewById(R.id.imageView1);
            txtTitle1 = (TextView) mView.findViewById(R.id.txtTitle1);
            subcategoryclick = (FrameLayout) mView.findViewById(R.id.subcategoryclick);

        }

        @Override
        public void onClick(View v) {
            if (v == subcategoryclick) {
                dataListener.subCategoryPosition("home_category_click", null, getAdapterPosition(), "" + mIdArrayList.get(getAdapterPosition()), mNameArrayList.get(getAdapterPosition()));


            }
        }
    }

}
