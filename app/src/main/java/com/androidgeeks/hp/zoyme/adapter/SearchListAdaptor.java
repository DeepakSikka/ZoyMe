package com.androidgeeks.hp.zoyme.adapter;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.model.SearchModel;
import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.zoymeinterfaces.SearchClick;

import java.util.ArrayList;

/**
 * Created by Deepak sikka on 11/16/2017.
 */

public class SearchListAdaptor extends RecyclerView.Adapter<SearchListAdaptor.DataObjectViewHolder> {
    private Activity activity;
    private ArrayList<SearchModel> searchuserList;
    private SearchClick searchClick;

    public SearchListAdaptor(Activity activity, ArrayList<SearchModel> searchuserList, SearchClick searchClick) {
        this.activity = activity;
        this.searchuserList = searchuserList;
        this.searchClick = searchClick;
    }


    @Override
    public DataObjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.search_user_item, parent, false);
        SearchListAdaptor.DataObjectViewHolder holder = new SearchListAdaptor.DataObjectViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DataObjectViewHolder holder, int position) {
        SearchModel searchDataModel = searchuserList.get(position);

        holder.user_name.setText(searchDataModel.getName());
    }

    @Override
    public int getItemCount() {
        return searchuserList.size();
    }

    public class DataObjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView user_name;

        public DataObjectViewHolder(View itemView) {
            super(itemView);


            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_name.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v == user_name) {


                searchClick.subSearchPosition("search_click", searchuserList.get(getAdapterPosition()), getAdapterPosition(), searchuserList.get(getAdapterPosition()).getCategory_id(), searchuserList.get(getAdapterPosition()).getName());

                notifyDataSetChanged();
            }

        }
    }


}
