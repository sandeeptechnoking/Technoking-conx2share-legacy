package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;


public class DiscoverAutoCompleteAdapter extends ArrayAdapter<String> {

    public static final String TAG = DiscoverAutoCompleteAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    private DiscoverAutoCompleteCallbacks mCallbacks;

    private Context mContext;

    public DiscoverAutoCompleteAdapter(Context context, DiscoverAutoCompleteCallbacks callback, ArrayList<String> hashTagTitles) {
        super(context, R.layout.item_discover_autocomplete, hashTagTitles);
        mContext = context;
        mCallbacks = callback;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        String hashTagTitle = getItem(position);

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.item_discover_autocomplete, null);
        }

        TextView title = (TextView) view.findViewById(R.id.hash_tag_title);

        title.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mCallbacks.itemClicked();
            }
            return false;
        });

        title.setText("#" + hashTagTitle);

        if (position >= getCount() - 1) {
            mCallbacks.onNearingEndOfList();
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return hashTagFilter;
    }

    Filter hashTagFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    };

    public interface DiscoverAutoCompleteCallbacks{
        void onNearingEndOfList();
        void itemClicked();
    }
}
