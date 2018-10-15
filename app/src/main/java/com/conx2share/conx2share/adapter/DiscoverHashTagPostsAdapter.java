package com.conx2share.conx2share.adapter;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.ui.feed.post_comments.PostCommentsActivity;
import com.conx2share.conx2share.ui.feed.post_comments.PostCommentsFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DiscoverHashTagPostsAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static final String TAG = DiscoverHashTagPostsAdapter.class.getSimpleName();

    private DiscoverHashTagPostsAdapterCallBacks mCallbacks;

    private LayoutInflater mLayoutInflater;

    private ArrayList<Post> mPosts;

    public DiscoverHashTagPostsAdapter(ArrayList<Post> mPosts, DiscoverHashTagPostsAdapterCallBacks mCallbacks) {
        this.mPosts = mPosts;
        this.mCallbacks = mCallbacks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discover_tag, null);
        ViewHolder rcv = new ViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = mPosts.get(position);

        if (position >= mPosts.size() - 1) {
            mCallbacks.onNearingEndOfList(post);
        }

        String imageUrl = post.getImageUrl();
        if (imageUrl != null) {
            Glide.with(holder.discoverListItem.getContext()).load(imageUrl).centerCrop().placeholder(R.drawable.friend_placeholder).error(R.drawable.friend_placeholder).dontAnimate().into(holder.postImage);
        }else{
            Glide.with(holder.discoverListItem.getContext()).load(R.drawable.friend_placeholder).centerCrop().dontAnimate().into(holder.postImage);
        }

        holder.name.setText(post.hashTags());

        View.OnClickListener postOnClickListener = v -> {
            Intent intent = new Intent(holder.discoverListItem.getContext(), PostCommentsActivity.class);
            intent.putExtra(PostCommentsFragment.EXTRA_POST_ID, String.valueOf(post.getId()));
            holder.discoverListItem.getContext().startActivity(intent);
        };

        holder.discoverListItem.setOnClickListener(postOnClickListener);

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public interface DiscoverHashTagPostsAdapterCallBacks {
        void onNearingEndOfList(Post post);
    }

}

class ViewHolder extends RecyclerView.ViewHolder {

    LinearLayout discoverListItem;
    ImageView postImage;
    TextView name;

    public ViewHolder(View view) {
        super(view);
        setupViews(view);
    }

    private void setupViews(View view) {
        discoverListItem = (LinearLayout) view.findViewById(R.id.discover_list_item);
        postImage = (ImageView) view.findViewById(R.id.discover_post_pic);
        name = (TextView) view.findViewById(R.id.discover_user_name);
    }

    public void resetViews(View view) {
        postImage = null;
        name = null;
        setupViews(view);
    }
}
