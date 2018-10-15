package com.conx2share.conx2share.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseDrawerActivity;

import java.util.List;

public class FeedDrawerAdapter extends ArrayAdapter<BaseDrawerActivity.BaseDrawerItem> {

    private int mResourceId;

    private List<BaseDrawerActivity.BaseDrawerItem> mItems;

    public FeedDrawerAdapter(Context context, int resource, List<BaseDrawerActivity.BaseDrawerItem> items) {
        super(context, resource, items);
        mResourceId = resource;
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        FeedItemHolder holder = null;

        if (row == null) {

            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            row = inflater.inflate(mResourceId, parent, false);

            holder = new FeedItemHolder();
            holder.itemText = (TextView) row.findViewById(R.id.item_feed_text);

            row.setTag(holder);
        } else {

            holder = (FeedItemHolder) row.getTag();
        }

        holder.itemText.setText(mItems.get(position).getTitle());

        int itemCount = mItems.get(position).getCount();
        if (itemCount <= 0) {
            row.findViewById(R.id.item_count).setVisibility(View.GONE);
        } else {
            TextView itemCountTextView = (TextView) row.findViewById(R.id.item_count);
            itemCountTextView.setText(itemCount + "");
            row.findViewById(R.id.item_count).setVisibility(View.VISIBLE);
        }

        return row;
    }

    private class FeedItemHolder {

        TextView itemText;
    }
}
