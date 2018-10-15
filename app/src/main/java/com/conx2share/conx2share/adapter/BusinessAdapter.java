package com.conx2share.conx2share.adapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.ui.business.BusinessProfileActivity;
import com.conx2share.conx2share.ui.business.BusinessProfileFragment;
import com.conx2share.conx2share.ui.view.AvatarImageView;
import com.makeramen.roundedimageview.RoundedImageView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BusinessAdapter extends ArrayAdapter<Business> {

    public static final String TAG = BusinessAdapter.class.getSimpleName();

    private BusinessAdapterCallbacks mCallbacks;

    private LayoutInflater mLayoutInflater;

    public BusinessAdapter(ArrayList<Business> businesses, Context context, @Nullable BusinessAdapterCallbacks callbacks) {
        super(context, R.layout.item_business, businesses);
        mCallbacks = callbacks;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Business business = getItem(position);

        if (position >= getCount() - 1) {
            if (mCallbacks != null) {
                mCallbacks.onNearingEndOfList();
            }
        }

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_business, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = ((ViewHolder) convertView.getTag());
            vh.resetViews(convertView);
        }

        vh.avatarImageView.initView(business.getAvatarUrl(), business.getName());

        String businessName = "";
        if (business.getName() != null) {
            businessName = business.getName();
        }

        if (business.getBadgeCount() != null && business.getBadgeCount() != 0) {
            vh.nameTextView.setText(businessName + " (" + business.getBadgeCount() + ")");
        } else {
            vh.nameTextView.setText(businessName);
        }

        if (business.getIsOwner() != null && business.getIsOwner()) {
            vh.businessOwnerSign.setVisibility(View.VISIBLE);
            vh.businessStatus.setText(getContext().getString(R.string.business_owner));
        } else {
            vh.businessOwnerSign.setVisibility(View.GONE);
            if (business.getIsFollowing() != null && business.getIsFollowing()) {
                vh.businessStatus.setText(getContext().getString(R.string.follower));
            } else {
                vh.businessStatus.setText(getContext().getString(R.string.empty_string));
            }
        }

        vh.businessListItem.setOnClickListener(v -> {
            Intent businessProfileActivityIntent = new Intent(getContext(), BusinessProfileActivity.class);
            businessProfileActivityIntent.putExtra(BusinessProfileFragment.EXTRA_BUSINESS_ID, business.getId());
            getContext().startActivity(businessProfileActivityIntent);
        });

        return convertView;
    }

    public interface BusinessAdapterCallbacks {
        void onNearingEndOfList();
    }

    private class ViewHolder {

        LinearLayout businessListItem;
        AvatarImageView avatarImageView;
        TextView nameTextView;
        RoundedImageView businessOwnerSign;
        TextView businessStatus;

        public ViewHolder(View view) {
            setupViews(view);
        }

        private void setupViews(View view) {
            businessListItem = (LinearLayout) view.findViewById(R.id.business_list_item);
            avatarImageView = (AvatarImageView) view.findViewById(R.id.avatar_imageView);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            businessOwnerSign = (RoundedImageView) view.findViewById(R.id.business_owner_sign);
            businessStatus = (TextView) view.findViewById(R.id.business_status);
        }

        public void resetViews(View view) {
            businessListItem = null;
            avatarImageView = null;
            nameTextView = null;
            businessOwnerSign = null;
            businessStatus = null;
            setupViews(view);
        }
    }
}