package com.conx2share.conx2share.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.discover.DiscoverSearchBusinessesFragment;
import com.conx2share.conx2share.ui.discover.DiscoverSearchGroupsFragment;
import com.conx2share.conx2share.ui.discover.DiscoverSearchHashTagPostsFragment;
import com.conx2share.conx2share.ui.discover.DiscoverSearchLivestreamFragment;
import com.conx2share.conx2share.ui.discover.DiscoverSearchUsersFragment;

public class DiscoverViewPagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    public DiscoverViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return DiscoverSearchLivestreamFragment.newInstance();
            case 1:
                return DiscoverSearchUsersFragment.newInstance();
            case 2:
                return DiscoverSearchHashTagPostsFragment.newInstance();
            case 3:
                return DiscoverSearchGroupsFragment.newInstance();
            case 4:
                return DiscoverSearchBusinessesFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.discover_live_stream);
            case 1:
                return mContext.getString(R.string.discover_people);
            case 2:
                return mContext.getString(R.string.discover_hash_tags);
            case 3:
                return mContext.getString(R.string.discover_groups);
            case 4:
                return mContext.getString(R.string.discover_businesses);
            default:
                return mContext.getString(R.string.page) + position;
        }
    }
}
