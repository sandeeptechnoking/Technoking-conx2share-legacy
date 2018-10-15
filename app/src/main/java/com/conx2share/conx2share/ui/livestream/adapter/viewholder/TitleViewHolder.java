package com.conx2share.conx2share.ui.livestream.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.conx2share.conx2share.R;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class TitleViewHolder extends RecyclerView.ViewHolder {

    @InjectView(R.id.stream_as_header_tv)
    public TextView headerTv;

    public TitleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static View inflate(ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stream_as_title, parent, false);
    }

}
