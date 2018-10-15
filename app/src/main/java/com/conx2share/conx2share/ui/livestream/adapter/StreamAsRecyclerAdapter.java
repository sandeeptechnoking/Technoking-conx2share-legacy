package com.conx2share.conx2share.ui.livestream.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.conx2share.conx2share.model.AuthUser;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.ui.livestream.adapter.viewholder.ItemViewHolder;
import com.conx2share.conx2share.ui.livestream.adapter.viewholder.TitleViewHolder;

import java.util.List;
import java.util.Locale;


public class StreamAsRecyclerAdapter extends RecyclerView.Adapter {

    static final int TITLE_TYPE = 100;
    static final int USER_TYPE = 111;
    static final int GROUP_TYPE = 222;
    static final int BUSINESS_TYPE = 333;

    private AuthUser user;
    private List<Group> groups;
    private List<Business> businesses;

    private OnItemClickListener itemClickListener;

    public void setUser(AuthUser user) {
        this.user = user;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TITLE_TYPE) {
            View headerView = TitleViewHolder.inflate(parent);
            return new TitleViewHolder(headerView);
        } else {
            View itemView = ItemViewHolder.inflate(parent);
            return new ItemViewHolder(itemView, position -> {
                if (itemClickListener != null) {
                    if (viewType == USER_TYPE) {
                        itemClickListener.onUserClick(user);
                    } else if(viewType == GROUP_TYPE) {
                        itemClickListener.onGroupClick(groups.get(position - 2));
                    } else if (viewType == BUSINESS_TYPE) {
                        int index = groups != null ? position - groups.size() - 2 : position - 2;
                        itemClickListener.onBusinessClick(businesses.get(index));
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleViewHolder) {
            ((TitleViewHolder) holder).headerTv.setText("Stream as");
        } else {
            bindItemHolder((ItemViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        int header = 1;
        int userAs = user != null ? 1 : 0;
        int groupsSize = groups != null ? groups.size() : 0;
        int businessesSize = businesses != null ? businesses.size() : 0;
        return header + userAs + groupsSize + businessesSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TITLE_TYPE;
        } else if(position == 1) {
            return USER_TYPE;
        } else if (groups != null && !groups.isEmpty() && position > 1 && position <= (groups.size() + 1)) {
            return GROUP_TYPE;
        } else if (businesses != null && !businesses.isEmpty() && (groups != null && !groups.isEmpty())
                ? position > groups.size() : position > 1 && position < getItemCount()) {
            return BUSINESS_TYPE;
        }
        throw new RuntimeException("There is no type that matches the position " + position);
    }

    private void bindItemHolder(ItemViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == USER_TYPE) {
            holder.image.initView(user.getAvatar().getAvatar().getThumbUrl(), user.getFirstName(), user.getLastName());
            holder.name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        } else if(itemViewType == GROUP_TYPE) {
            Group group = groups.get(position - 2);
            holder.image.initView(group.getGroupAvatarUrl(), group.getName());
            holder.name.setText(group.getName());
        } else if (itemViewType == BUSINESS_TYPE) {
            int index = groups != null ? position - groups.size() - 2 : position - 2;
            Business business = businesses.get(index);
            holder.image.initView(business.getAvatarUrl(), business.getName());
            holder.name.setText(business.getName());
        }
    }


    public interface OnItemClickListener  {
        void onUserClick(AuthUser user);
        void onGroupClick(Group group);
        void onBusinessClick(Business business);
    }

}
