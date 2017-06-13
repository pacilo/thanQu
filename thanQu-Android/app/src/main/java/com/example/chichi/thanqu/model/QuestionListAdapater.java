package com.example.chichi.thanqu.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chichi.thanqu.QuestionDetailActivity;
import com.example.chichi.thanqu.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by 치치 on 2017-05-09.
 */

public class QuestionListAdapater extends  RecyclerView.Adapter<QuestionListAdapater.ViewHolder> {

    private List<QuestionModel> list;
    private Context ctx;

    public QuestionListAdapater(Context ctx, List<QuestionModel> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_questionlist, null), this.ctx, this.list);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    public void add(QuestionModel m) {
        list.add(m);
    }

    public void clear() {
        list.clear();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_writer.setText("댓글 "+ list.get(position).commentCnt.toString()); //작성자
        holder.tv_title.setText("좋아요 " +list.get(position).likeCnt.toString()); //제목
        holder.tv_content.setText(list.get(position).content.toString()); //내용 일부
        holder.tv_date.setText(list.get(position).time.toString()); //작성일
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void reverse() {
        Collections.reverse(list);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_title;
        TextView tv_date;
        TextView tv_content;
        TextView tv_writer;
        CardView cv;
        Context ctx;
        List<QuestionModel> list;

        public ViewHolder(View v, Context context, List list) {
            super(v);
            this.ctx = context;
            this.list = list;
            tv_title = (TextView) v.findViewById(R.id.tv_title);
            tv_date = (TextView) v.findViewById(R.id.tv_date);
            tv_content = (TextView) v.findViewById(R.id.tv_content);
            tv_writer = (TextView) v.findViewById(R.id.tv_writer);
            cv = (CardView) v.findViewById(R.id.cv);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(this.ctx, QuestionDetailActivity.class);
            intent.putExtra("questionID", list.get(getAdapterPosition()).id);
            this.ctx.startActivity(intent);
        }
    }

}
