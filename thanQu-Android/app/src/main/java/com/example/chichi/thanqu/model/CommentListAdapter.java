package com.example.chichi.thanqu.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pacilo on 2017. 6. 7..
 */
public class CommentListAdapter extends BaseAdapter {

    Context context;
    List<CommentModel> list;

    public CommentListAdapter(Context c, List<CommentModel> list) {
        this.context = c;
        this.list = list;
    }

    public void add(CommentModel m) {
        list.add(m);
    }

    public void clear() {
        list.clear();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        }
        TextView tv = (TextView) v.findViewById(android.R.id.text1);
        tv.setText(list.get(i).content);
        return v;
    }
}
