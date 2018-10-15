package com.conx2share.conx2share.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.FriendsAdapter;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.RoboGuice;
import roboguice.inject.InjectView;


public class DeleteInsideView extends RelativeLayout implements View.OnDragListener {

    @InjectView(R.id.recycler_friend_iv)
    ImageView recyclerFriendIv;

    @InjectView(R.id.recycler_friend_layout)
    RelativeLayout recyclerFriendLayout;

    FriendWasDeleted friendWasDeleted;

    public DeleteInsideView(Context context, FriendWasDeleted friendWasDeleted) {
        super(context);
        this.friendWasDeleted = friendWasDeleted;
        init();
    }

    public DeleteInsideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DeleteInsideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.delete_inside_view, this);
        ButterKnife.bind(this, view);
        setBackgroundResource(R.color.white);
        setOnDragListener(this);
        RoboGuice.injectMembers(getContext(), this);
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                recyclerFriendIv.getDrawable().setColorFilter(
                        ContextCompat.getColor(getContext(), R.color.white),
                        PorterDuff.Mode.SRC_ATOP);
                getBackground().setColorFilter(
                        ContextCompat.getColor(getContext(), R.color.float_bt),
                        PorterDuff.Mode.SRC_ATOP);
                invalidate();
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                returnViewsToInitialState();
                break;
            case DragEvent.ACTION_DROP:
                returnViewsToInitialState();
                ViewGroup viewGroup = (ViewGroup) getParent();
                viewGroup.removeView(this);
                if (friendWasDeleted != null) {
                    friendWasDeleted.onFriendWasDeleted(event
                            .getClipData().getItemAt(0).getIntent().getIntExtra(FriendsAdapter.FRIEND_ID, 0));
                }
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                if (view != null) {
                    returnViewsToInitialState();
                }
                break;
        }
        return true;
    }

    private void returnViewsToInitialState() {
        recyclerFriendIv.getDrawable().clearColorFilter();
        getBackground().clearColorFilter();
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setOnDragListener(null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAlpha(0f);
        animate()
                .alpha(1f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setAlpha(1f);
                    }
                })
                .start();
    }

    public interface FriendWasDeleted {
        void onFriendWasDeleted(int id);
    }
}
