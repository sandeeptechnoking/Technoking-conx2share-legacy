package com.conx2share.conx2share.adapter;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.NewsSource;
import com.conx2share.conx2share.model.NewsSourceGridItem;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsSourceGridViewAdapter extends ArrayAdapter {

    public static final String TAG = NewsSourceGridViewAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    private ArrayList<NewsSource> mNewsSources;

    public NewsSourceGridViewAdapter(Context context, ArrayList<NewsSource> newsSources) {
        super(context, R.layout.item_news_source, newsSources);
        mNewsSources = newsSources;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsSource newsSource = mNewsSources.get(position);

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_news_source, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        if(newsSource.getImageUrl() != null && !TextUtils.isEmpty(newsSource.getImageUrl())) {
            Glide.with(getContext()).load(newsSource.getImageUrl()).fitCenter().dontAnimate().into(vh.newsSourceGridItem);
        }

        return convertView;
    }

    private class ViewHolder {

        NewsSourceGridItem newsSourceGridItem;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            newsSourceGridItem = (NewsSourceGridItem) view.findViewById(R.id.news_source_image);
        }

        public void resetViews(View view) {
            newsSourceGridItem = null;
            setupViews(view);
        }
    }
}
