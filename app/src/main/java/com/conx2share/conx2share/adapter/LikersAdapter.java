package com.conx2share.conx2share.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.SimpleLiker;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.conx2share.conx2share.util.DateUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;

public class LikersAdapter extends RecyclerView.Adapter<LikersAdapter.ViewHolder> {

    private List<SimpleLiker> mItems;
    private OnUserClickListener mOnClickListener;

    public LikersAdapter(List<SimpleLiker> items, OnUserClickListener listener) {
        mItems = items;
        mOnClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View likerItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.liker_list_item, parent, false);
        return new ViewHolder(likerItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleLiker model = mItems.get(position);
        holder.mAvatar.initView(model.getAvatarUrl(), model.getFirstName(), model.getLastName());
        holder.mUsername.setText(model.getUsername());
        holder.mName.setText(model.getName());
        holder.mTime.setText(model.getTime());
        holder.mTime.setText(DateUtils.getTimeDifference(model.getCreatedAt()));
        holder.mItemLayout.setOnClickListener(v -> {
            if (mOnClickListener != null) {
                mOnClickListener.onUserClick(model.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.liker_avatar)
        AvatarImageView mAvatar;
        @BindView(R.id.liker_name)
        TextView mName;
        @BindView(R.id.liker_username)
        TextView mUsername;
        @BindView(R.id.liker_time)
        TextView mTime;
        @BindView(R.id.liker_root_layout)
        ConstraintLayout mItemLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnUserClickListener {
        void onUserClick(Integer userID);
    }
}
