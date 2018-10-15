package com.conx2share.conx2share.ui.business;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.adapter.BusinessAdapter;
import com.conx2share.conx2share.async.RestResultCallback;
import com.conx2share.conx2share.model.Business;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.util.SnackbarUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import javax.inject.Inject;

import retrofit.RetrofitError;
import roboguice.inject.InjectView;

public class MyBusinessesFragment extends BaseFragment {

    public static final String TAG = MyBusinessesFragment.class.getSimpleName();

    @InjectView(R.id.listView)
    ListView mListView;

    @InjectView(R.id.progressBar)
    ProgressBar mProgressBar;

    @Inject
    SnackbarUtil mSnackbarUtil;

    private MyBusinessesCallback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (MyBusinessesCallback) activity;
        } catch (ClassCastException e) {
            throw new RuntimeException(MyBusinessesFragment.class.getName() + " must implement " + MyBusinessesCallback.class.getName(), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_businesses, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            Business business = (Business) mListView.getItemAtPosition(position);

            Intent businessProfileActivityIntent = new Intent(getActivity(), BusinessProfileActivity.class);
            businessProfileActivityIntent.putExtra(BusinessProfileFragment.EXTRA_BUSINESS_ID, business.getId());
            startActivity(businessProfileActivityIntent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getBusinesses();
    }

    private void getBusinesses() {
        mCallback.getBusinesses(new RestResultCallback<ArrayList<Business>>() {
            @Override
            public void onSuccess(ArrayList<Business> result) {
                mProgressBar.setVisibility(View.GONE);

                if (result.size() > 0) {
                    mListView.setAdapter(new BusinessAdapter(result, getActivity(), null));
                    mListView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(RetrofitError error) {
                Log.e(TAG, "Could not retrieve businesses", error);
                mProgressBar.setVisibility(View.GONE);
                mSnackbarUtil.showSnackBarWithAction(getActivity(), R.string.could_not_retrieve_your_businesses, R.string.retry, snackbar -> {
                    mProgressBar.setVisibility(View.VISIBLE);
                    getBusinesses();
                    snackbar.dismiss();
                });
            }
        });
    }

    public interface MyBusinessesCallback {

        void getBusinesses(RestResultCallback<ArrayList<Business>> callback);
    }
}
