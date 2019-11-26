package com.androidgeeks.hp.zoyme.adapter;

/**
 * Created by Deepak Sikka on 10/25/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;


import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.activity.ZoyMeHomeActivity;
import com.androidgeeks.hp.zoyme.model.ZoyMe_Model;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * To display data of expandable lsit in navigation drawer
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<HashMap<String, String>> _listDataHeader; // header titles
    ExpandableListView exp;
    private HashMap<String, List<String>> _listDataChild;
    private HashMap<String, List<String>> _listDataChild_id;


    public ExpandableListAdapter(Context context, ArrayList<HashMap<String, String>> listDataHeader,
                                 HashMap<String, List<String>> listChildData, HashMap<String, List<String>> listChildData_id) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._listDataChild_id = listChildData_id;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition).get(ZoyMeHomeActivity.TAG_NAME)).get(childPosititon);
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.drawer_list_item_tv_name);

        txtListChild.setText(childText);
        //.Log.e("Adpterchild",""+childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition).get(ZoyMeHomeActivity.TAG_NAME)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition).get(ZoyMeHomeActivity.TAG_NAME);
    }


    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final String headerTitle = this._listDataHeader.get(groupPosition).get(ZoyMeHomeActivity.TAG_NAME);
        String image_url = this._listDataHeader.get(groupPosition).get(ZoyMeHomeActivity.TAG_Image);
        final String header_id = this._listDataHeader.get(groupPosition).get(ZoyMeHomeActivity.TAG_ID);

        int a = getChildrenCount(groupPosition);


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parent_group, null);
        }


        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.row_name);
        ImageView iblListImage = (ImageView) convertView.findViewById(R.id.exp_image);
        final ImageView plus = (ImageView) convertView.findViewById(R.id.add_sign);

        if (a == 0) {
            plus.setVisibility(View.INVISIBLE);
        } else {
            plus.setVisibility(View.VISIBLE);
        }

        if (isExpanded) {

            plus.setBackgroundResource(R.mipmap.caret_symbol_up);

        } else {
            plus.setBackgroundResource(R.mipmap.caret_symbol_down);

        }

        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        Picasso.with(_context).load(image_url).placeholder(R.mipmap.main_banner_placeholder).into(iblListImage);


        iblListImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

