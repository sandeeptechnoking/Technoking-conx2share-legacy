package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.NewsItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class NewsAdapter extends ArrayAdapter{

    private static final String TAG = NewsAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    public NewsAdapter(Context context, ArrayList<NewsItem> newsItems) {
        super(context, R.layout.item_news, newsItems);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_news, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        NewsItem newsItem = (NewsItem) getItem(position);

        vh.newsTitle.setText(newsItem.getTitle());
        vh.newsDescription.setText(newsItem.getDescription());
        vh.newsPubDate.setText(newsItem.getPubDate());

        return convertView;
    }

    private class ViewHolder {

        TextView newsTitle;
        TextView newsDescription;
        TextView newsPubDate;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            newsTitle = (TextView) view.findViewById(R.id.news_title);
            newsDescription = (TextView) view.findViewById(R.id.news_description);
            newsPubDate = (TextView) view.findViewById(R.id.news_pub_date);
        }

        public void resetViews(View view) {
            newsTitle = null;
            newsDescription = null;
            newsPubDate = null;
            setupViews(view);
        }
    }
}