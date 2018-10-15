package com.conx2share.conx2share.ui.livestream.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Event;
import com.conx2share.conx2share.ui.livestream.adapter.viewholder.ItemViewHolder;
import com.conx2share.conx2share.ui.livestream.adapter.viewholder.TitleViewHolder;

import java.util.List;


public class AssociatedEventsRecyclerAdapter extends RecyclerView.Adapter {

    static final int TITLE_TYPE = 100;
    static final int NO_EVENT_TYPE = 111;
    static final int EVENT_TYPE = 222;

    private List<Event> events;

    private OnItemClickListener itemClickListener;

    public void setEvents(List<Event> groups) {
        this.events = groups;
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
                    if (viewType == NO_EVENT_TYPE) {
                        itemClickListener.onNoEventClick();
                    } else if (viewType == EVENT_TYPE) {
                        itemClickListener.onEventClick(events.get(position - 2));
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleViewHolder) {
            ((TitleViewHolder) holder).headerTv.setText(R.string.stream_as_associated_event);
        } else {
            bindItemHolder((ItemViewHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        int header = 1;
        int noEvent = 1;
        int eventsSze = events != null ? events.size() : 0;
        return header + noEvent + eventsSze;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TITLE_TYPE;
        } else if(position == 1) {
            return NO_EVENT_TYPE;
        } else {
            return EVENT_TYPE;
        }
    }

    private void bindItemHolder(ItemViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == NO_EVENT_TYPE) {
            holder.image.setVisibility(View.GONE);
            holder.name.setText(R.string.stream_as_no_event);
        } else {
            Event event = events.get(position - 2);
            holder.image.setVisibility(View.VISIBLE);
            holder.image.initView(event.getUrl(), event.getName());
            holder.name.setText(event.getName());
        }
    }


    public interface OnItemClickListener  {
        void onNoEventClick();
        void onEventClick(Event event);
    }

}
