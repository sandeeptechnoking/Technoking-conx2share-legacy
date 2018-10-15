package com.conx2share.conx2share.ui.feed.post;

import com.conx2share.conx2share.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PostReceiverSpinnerAdapter implements SpinnerAdapter {

    List<PostReceiver> mPostReceivers;

    private Context mContext;

    // TODO - consider a better way to build this adapter
    PostReceiverSpinnerAdapter(final Context context, @Nullable List<? extends PostReceiver> groups, @Nullable List<? extends PostReceiver> businesses) {
        mContext = context;
        mPostReceivers = new ArrayList<>();

        mPostReceivers.add(new PostReceiver() {
            @Override
            public String getName() {
                return context.getString(R.string.post_everyone);
            }

            @Override
            public PostReceiverType getType() {
                return PostReceiverType.EVERYONE;
            }

            @Override
            public Integer getReceiverId() {
                return -1;
            }
        });

        mPostReceivers.add(new PostReceiver() {
            @Override
            public String getName() {
                return context.getString(R.string.post_followers);
            }

            @Override
            public PostReceiverType getType() {
                return PostReceiverType.FOLLOWERS;
            }

            @Override
            public Integer getReceiverId() {
                return -2;
            }
        });

        if (groups != null && groups.size() > 0) {
            mPostReceivers.addAll(groups);
        }

        if (businesses != null && businesses.size() > 0) {
            mPostReceivers.addAll(businesses);
        }
    }

    public PostReceiverSpinnerAdapter(Context context) {
        this.mContext = context;
        mPostReceivers = new ArrayList<>();
        mPostReceivers.add(new PostReceiver() {
            @Override
            public String getName() {
                return context.getString(R.string.post_followers);
            }

            @Override
            public PostReceiverType getType() {
                return PostReceiverType.FOLLOWERS;
            }

            @Override
            public Integer getReceiverId() {
                return -2;
            }
        });
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getPostReceiverView(position, convertView, parent);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return mPostReceivers.size();
    }

    @Override
    public Object getItem(int position) {

        return mPostReceivers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getPostReceiverView(position, convertView, parent);
    }

    public View getPostReceiverView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = View.inflate(mContext, R.layout.item_post_receiver, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) view.findViewById(R.id.post_receiver_name);

            view.setTag(viewHolder);
        }
        TextView nameTextView = ((ViewHolder) view.getTag()).nameTextView;
        PostReceiver receiver = (PostReceiver) getItem(position);
        switch (receiver.getType()) {
            case EVERYONE:
                nameTextView.setText(receiver.getName());
                break;
            case FOLLOWERS:
                nameTextView.setText(receiver.getName());
                break;
            case GROUP:
                nameTextView.setText(mContext.getString(R.string.group) + receiver.getName());
                break;
            case BUSINESS:
                nameTextView.setText(mContext.getString(R.string.business) + receiver.getName());
                break;
        }

        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public Integer getPositionOfPostReceiver(PostReceiver postReceiver) {
        int searchId = postReceiver.getReceiverId();

        for (int i = 0; i < mPostReceivers.size(); i++) {
            if (mPostReceivers.get(i).getReceiverId().equals(searchId)) {
                return i;
            }
        }
        return null;
    }

    private class ViewHolder {

        TextView nameTextView;
    }
}
