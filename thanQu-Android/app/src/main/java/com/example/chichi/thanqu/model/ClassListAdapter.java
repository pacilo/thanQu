package com.example.chichi.thanqu.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chichi.thanqu.R;

import java.util.ArrayList;

/**
 * Created by pacilo on 2017. 6. 6..
 */

public class ClassListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ClassModel> arrayList;

    public ClassListAdapter(Context context, ArrayList list) {
        this.context = context;
        this.arrayList = list;
    }

    public void add(ClassModel m) {
        arrayList.add(m);
    }

    public void clear() {
        arrayList.clear();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        }
        TextView tv = (TextView) v.findViewById(android.R.id.text1);
        tv.setText(arrayList.get(position).className);
        return v;
    }
}
