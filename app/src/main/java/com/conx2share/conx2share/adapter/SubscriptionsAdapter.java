package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Subscription;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SubscriptionsAdapter extends ArrayAdapter<Subscription> {

    private static final String TAG = SubscriptionsAdapter.class.getSimpleName();

    private LayoutInflater mLayoutInflater;

    private SubscribeToPlanCallback mCallback;

    public SubscriptionsAdapter(ArrayList<Subscription> subscriptions, Context context, Fragment fragment) {
        super(context, R.layout.subscription_list_item, subscriptions);
        mLayoutInflater = LayoutInflater.from(context);

        try {
            mCallback = (SubscribeToPlanCallback) fragment;

        } catch (ClassCastException e) {
            throw new ClassCastException("Does not implement SubscribeToPlanCallback");
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.subscription_list_item, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        Subscription subscription = getItem(position);

        if (subscription.getTitle() != null) {
            vh.subscriptionTitle.setText(subscription.getTitle());
        }

        if (subscription.getDescription() != null) {
            vh.subscriptionDescription.setText(subscription.getDescription());
        }

        if (subscription.getPrice() != null) {
            if (!subscription.getPrice().equals("0")) {
                vh.subscriptionPrice.setVisibility(View.VISIBLE);
                vh.subscriptionPrice.setText(subscription.getPrice());
            } else {
                vh.subscriptionPrice.setVisibility(View.GONE);
            }
        }

        if (subscription.getSubscribed()) {
            vh.subscribeButton.setVisibility(View.GONE);
            vh.currentSubscriptionText.setVisibility(View.VISIBLE);
            vh.currentSubscriptionText.setTextColor(Color.parseColor("#2684D1"));
            vh.starIcon.setVisibility(View.VISIBLE);
        } else {
            vh.subscribeButton.setVisibility(View.VISIBLE);
            vh.currentSubscriptionText.setVisibility(View.GONE);
            vh.currentSubscriptionText.setTextColor(Color.parseColor("#ee414141"));
            vh.starIcon.setVisibility(View.GONE);
        }

        vh.currentSubscriptionText.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.conx2share.conx2share"));
            getContext().startActivity(intent);
        });

        vh.subscribeButton.setOnClickListener(v -> mCallback.onSubscribeToPlan(position));

        return convertView;
    }

    public interface SubscribeToPlanCallback {
        void onSubscribeToPlan(int position);
    }

    private class ViewHolder {

        TextView subscriptionTitle;
        TextView subscriptionDescription;
        TextView subscriptionPrice;
        Button subscribeButton;
        TextView currentSubscriptionText;
        ImageView starIcon;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            subscriptionTitle = (TextView) view.findViewById(R.id.subscription_title);
            subscriptionDescription = (TextView) view.findViewById(R.id.subscription_description);
            subscriptionPrice = (TextView) view.findViewById(R.id.subscription_price);
            subscribeButton = (Button) view.findViewById(R.id.subscribe_button);
            currentSubscriptionText = (TextView) view.findViewById(R.id.current_subscription_text);
            starIcon = (ImageView) view.findViewById(R.id.star_icon);
        }

        public void resetViews(View view) {
            subscriptionTitle = null;
            subscriptionDescription = null;
            subscriptionPrice = null;
            subscribeButton = null;
            currentSubscriptionText = null;
            starIcon = null;
            setupViews(view);
        }
    }
}
