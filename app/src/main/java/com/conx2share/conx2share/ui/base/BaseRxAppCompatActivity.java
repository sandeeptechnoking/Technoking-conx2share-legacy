package com.conx2share.conx2share.ui.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


public class BaseRxAppCompatActivity extends BaseAppCompatActivity {

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
    }

    public void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }
}
