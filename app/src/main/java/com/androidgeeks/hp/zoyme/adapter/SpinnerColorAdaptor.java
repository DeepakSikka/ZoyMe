package com.androidgeeks.hp.zoyme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androidgeeks.hp.zoyme.R;
import com.androidgeeks.hp.zoyme.model.ColorModel;
import com.androidgeeks.hp.zoyme.model.SizeModel;

/**
 * Created by Deepak Sikka on 11/28/2017.
 */

public class SpinnerColorAdaptor extends ArrayAdapter<ColorModel> {
    private Context context;
    private int resource;
    private ColorModel[] objects;
    private LayoutInflater inflater;


    public SpinnerColorAdaptor(Context context, int resource, ColorModel[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;

    }

    @Override
    public int getCount() {
        return objects.length;
    }

    @Override
    public ColorModel getItem(int position) {
        return objects[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View view = getCustomView(position, convertView, parent, R.layout.array_adaptor_item);

        return view;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, int ViewLayout) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(ViewLayout, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.item_tv);
        tvName.setText(objects[position].getName());
        return convertView;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View dropDownView = getDropDownCustomView(position, convertView, parent, R.layout.array_adaptor_item_dropdown);

        return dropDownView;
    }

    public View getDropDownCustomView(int position, View convertView, ViewGroup parent, int ViewLayout) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(ViewLayout, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.item_tv);

        tvName.setText(objects[position].getName());


        return convertView;

    }
}
