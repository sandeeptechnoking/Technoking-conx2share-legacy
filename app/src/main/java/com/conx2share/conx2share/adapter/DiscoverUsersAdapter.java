package com.conx2share.conx2share.adapter;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.BR;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Post;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.profile.ProfileActivity;
import com.conx2share.conx2share.ui.profile.ProfileFragment;

import java.util.ArrayList;

public class DiscoverUsersAdapter extends RecyclerView.Adapter<DiscoverUsersAdapter.ViewHolder> {

    private DiscoverUsersAdapterCallbacks mCallbacks;

    private ArrayList<User> mUsers;

    public DiscoverUsersAdapter(DiscoverUsersAdapterCallbacks callbacks, ArrayList<User> users) {
        mUsers = users;
        mCallbacks = callbacks;
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .dontAnimate()
                .centerCrop()
                .into(view);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        ViewDataBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_discover, viewGroup, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        final User user = mUsers.get(position);
        if (position >= mUsers.size() - 1) {
            if (mCallbacks != null) mCallbacks.onNearingEndOfList();
        }
        vh.bind(user);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
    public void onUserClick(View view, User user){
        view.getContext().startActivity(new Intent(view.getContext(), ProfileActivity.class)
                .putExtra(ProfileFragment.PROFILEID_KEY, String.valueOf(user.getId())));
    }

    public void onFollowUserClick(User user){
        if (mCallbacks != null && !user.getIsFollowing()){
            mCallbacks.onFollowUserClick(user);
        }
    }

   public void onPostClick(Post post){
        if (mCallbacks != null && post != null){
            mCallbacks.onPostClick(post);
        }
    }

    public interface DiscoverUsersAdapterCallbacks {
        void onNearingEndOfList();
        void onFollowUserClick(User user);
        void onPostClick(Post post);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User user) {
            binding.setVariable(BR.user, user);
            binding.setVariable(BR.adapter, DiscoverUsersAdapter.this);
            binding.executePendingBindings();
        }
    }
}
