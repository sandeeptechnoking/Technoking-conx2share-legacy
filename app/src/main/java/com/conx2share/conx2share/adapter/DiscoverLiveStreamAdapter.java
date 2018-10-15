package com.conx2share.conx2share.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.LiveEvent;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.streaming.EventVideoActivity;
import com.conx2share.conx2share.ui.view.AvatarImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;

public class DiscoverLiveStreamAdapter extends RecyclerView.Adapter<DiscoverLiveStreamAdapter.ViewHolder> {

    public static final String TAG = DiscoverHashTagPostsAdapter.class.getSimpleName();

    private List<LiveEvent> liveEvents;

    public DiscoverLiveStreamAdapter(List<LiveEvent> liveEvents) {
        this.liveEvents = liveEvents;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.
                from(parent.getContext()).inflate(R.layout.item_livestream, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LiveEvent liveEvent = liveEvents.get(position);
        Context context = holder.liveTags.getContext();

        User user = liveEvent.getUser();
        String title = liveEvent.getTitle();

        holder.liveImage.initView(liveEvent.getImageUrl(),
                user != null ? user.getFirstName() : title, user != null ? user.getLastName() : "");

        holder.liveUserName.setText(title);
        holder.liveTags.setText(liveEvent.getDescription());
        holder.liveBadge.setVisibility(liveEvent.isLive() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(liveEvent.getUrl())) {
                EventVideoActivity.start(context, liveEvent.getUrl());
            }
        });
        holder.liveDivider.setVisibility((position == getItemCount() - 1) ? View.GONE : View.VISIBLE);
    }


    @Override
    public int getItemCount() {
        return liveEvents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.live_badge)
        TextView liveBadge;
        @BindView(R.id.live_views)
        TextView liveViews;
        @BindView(R.id.live_user_name)
        TextView liveUserName;
        @BindView(R.id.live_tags)
        TextView liveTags;
        @BindView(R.id.live_image)
        AvatarImageView liveImage;
        @BindView(R.id.live_divider)
        View liveDivider;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
