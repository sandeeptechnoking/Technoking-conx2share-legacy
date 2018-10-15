package com.conx2share.conx2share.ui.livestream.adapter.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.view.AvatarImageView;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @InjectView(R.id.stream_as_element_image)
    public AvatarImageView image;

    @InjectView(R.id.stream_as_element_name_tv)
    public TextView name;

    private OnItemClickListener itemClickListener;

    public ItemViewHolder(View itemView, OnItemClickListener itemClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(this);
        this.itemClickListener = itemClickListener;
    }

    public static View inflate(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream_as_element, parent, false);
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
