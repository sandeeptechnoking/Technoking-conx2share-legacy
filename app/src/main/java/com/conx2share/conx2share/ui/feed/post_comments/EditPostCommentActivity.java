package com.conx2share.conx2share.ui.feed.post_comments;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActionBarActivity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

public class EditPostCommentActivity extends BaseActionBarActivity {

    private static final String TAG = EditPostCommentActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            EditPostCommentFragment fragment = EditPostCommentFragment.newInstance(getIntent().getExtras());
            ft.add(android.R.id.content, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.enter_from_top, R.anim.exit_to_bottom);
    }
}
