package com.longge.whathas.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by long on 2016/7/19.
 */
public class TextRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // 数据集
    private List<String> mDataset;

    public TextRecyclerAdapter(List<String> dataset) {
        super();
        mDataset = dataset;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView

        View view = View.inflate(parent.getContext(), android.R.layout.simple_list_item_1, null);

        // 创建一个ViewHolder

        TextViewHolder holder = new TextViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TextViewHolder) holder).mTextView.setText(mDataset.get(position));
    }


    @Override
    public int getItemCount() {

        return mDataset.size();

    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public TextViewHolder(View itemView) {

            super(itemView);

            mTextView = (TextView) itemView;

        }

    }
}
