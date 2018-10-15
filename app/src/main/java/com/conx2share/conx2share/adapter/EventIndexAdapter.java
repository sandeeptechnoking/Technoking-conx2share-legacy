package com.conx2share.conx2share.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Event;
import com.conx2share.conx2share.ui.events.EventOptionResponder;

import java.util.List;

public class EventIndexAdapter extends BaseAdapter {

    private List<Event> mItems;
    private RequestManager mGlide;

    private EventOptionResponder mEventOptionResponder;

    public EventIndexAdapter(List<Event> mItems, RequestManager mGlide, EventOptionResponder eventOptionResponder) {
        this.mItems = mItems;
        this.mGlide = mGlide;
        this.mEventOptionResponder = eventOptionResponder;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_index_list_item, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.event_photo);
            viewHolder.dateTime = (TextView) convertView.findViewById(R.id.event_datetime);
            viewHolder.name = (TextView) convertView.findViewById(R.id.event_name);
            viewHolder.description = (TextView) convertView.findViewById(R.id.event_description);
            viewHolder.options = (RadioGroup) convertView.findViewById(R.id.event_options_container);
            viewHolder.optionGoing = (RadioButton) convertView.findViewById(R.id.event_option_going);
            viewHolder.optionMaybeGoing = (RadioButton) convertView.findViewById(R.id.event_option_maybe);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(mItems.get(position).getName());
        mGlide.load(mItems.get(position).getUrl()).dontAnimate().centerCrop().into(viewHolder
                .photo);
        viewHolder.dateTime.setText(mItems.get(position).getFormattedStartDateTime());
        viewHolder.description.setText(mItems.get(position).getDescription());
        viewHolder.options.setVisibility(mItems.get(position).getIs_owner() ? View.GONE : View.VISIBLE);

        viewHolder.optionGoing.setSelected(mItems.get(position).getRsvp_status().equals("1"));
        viewHolder.optionMaybeGoing.setSelected(mItems.get(position).getRsvp_status().equals("2"));

        viewHolder.optionGoing.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            if (v.isSelected()) {
                viewHolder.optionMaybeGoing.setSelected(false);
            }
            mEventOptionResponder.setAttendence(mItems.get(position).getId(), v.isSelected(), viewHolder
                    .optionMaybeGoing.isSelected());
        });

        viewHolder.optionMaybeGoing.setOnClickListener(v -> {
            v.setSelected(!v.isSelected());
            if (v.isSelected()) {
                viewHolder.optionGoing.setSelected(false);
            }
            mEventOptionResponder.setAttendence(mItems.get(position).getId(), viewHolder.optionGoing.isSelected(), v
                    .isSelected());
        });

        return convertView;
    }

    class ViewHolder {
        ImageView photo;
        TextView dateTime, name, description;
        RadioButton optionGoing, optionMaybeGoing;
        RadioGroup options;
    }
}
