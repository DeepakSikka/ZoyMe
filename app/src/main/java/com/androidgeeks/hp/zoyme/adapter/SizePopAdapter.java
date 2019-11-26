package com.androidgeeks.hp.zoyme.adapter;

/**
 * Created by hp on 12/20/2017.
 */

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.androidgeeks.hp.zoyme.R;

import java.util.ArrayList;

public class SizePopAdapter extends BaseAdapter {
    ArrayList<String> packageList;
    ArrayList<String> packageListid;
    public static String value="";
    public static String id="";
    Activity context;
    boolean[] itemChecked;

    public SizePopAdapter(Activity context, ArrayList<String> packageList, ArrayList<String> packageListid) {
        super();
        this.context = context;
        this.packageList = packageList;
        this.packageListid=packageListid;
    }

    private class ViewHolder {
        TextView apkName;
        CheckBox ck1;
    }

    public int getCount() {
        return packageList.size();
    }

    public Object getItem(int position) {
        return packageList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.drop_down_list_row, null);
            holder = new ViewHolder();

            holder.apkName = (TextView) convertView.findViewById(R.id.textView1);
            holder.ck1 = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.apkName.setText(packageList.get(position));

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }


        holder.ck1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (holder.ck1.isChecked()) {
                    value += holder.apkName.getText().toString() + ",";
                    id+=packageListid.get(position).toString()+ ",";
                    Log.e("", "" + value);
                }
                else{

                }

            }
        });

        return convertView;

    }



}