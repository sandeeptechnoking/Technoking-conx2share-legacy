package com.conx2share.conx2share.ui.sayno;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.model.InvitationState;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.response.GetGroupListResponse;
import com.conx2share.conx2share.ui.base.BaseAppCompatActivity;
import com.conx2share.conx2share.ui.feed.FeedActivity;
import com.conx2share.conx2share.ui.sayno.cell.SayNoGroupCell;
import com.conx2share.conx2share.ui.sayno.dialog.SayNoInfoDialogFragment;
import com.conx2share.conx2share.ui.sayno.dialog.SayNoNotificationDialogFragment;
import com.conx2share.conx2share.ui.view.MarginItemDecorator;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import io.techery.celladapter.Cell;
import io.techery.celladapter.CellAdapter;
import roboguice.inject.InjectView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class SayNoSignInActivity extends BaseAppCompatActivity {
    private static final String TAG = SayNoChatActivity.class.getName();

    private static final String INFO_TAG = "dialog-info";

    private static final int MIN_NUMBER_LETTERS = 2;

    @InjectView(R.id.say_no_sign_in_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.say_no_sign_in_search)
    SearchView searchView;

    @InjectView(R.id.say_no_sign_in_progress_bar)
    ProgressBar progressBar;

    @InjectView(R.id.say_no_sign_in_school_list)
    RecyclerView schoolList;

    @InjectView(R.id.empty_view_container)
    ViewGroup emptyContainer;

    @InjectView(R.id.empty_view_logo)
    ImageView emptyLogo;

    @InjectView(R.id.empty_view_text)
    TextView emptyText;

    @Inject
    NetworkClient networkClient;

    @Inject
    SayNoFlowInteractor sayNoFlowInteractor;

    private AlertDialog confirmationDialog;
    private CellAdapter groupCellAdapter;

    private PublishSubject<String> querySubject = PublishSubject.create();
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public static void start(Context context) {
        context.startActivity(new Intent(context, SayNoSignInActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_say_no_sign_in);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        groupCellAdapter = new CellAdapter();
        groupCellAdapter.registerCell(Group.class, SayNoGroupCell.class, new Cell.Listener<Group>() {
            @Override
            public void onCellClicked(Group group) {
                showConfirmationDialog(group.getId());
            }
        });

        schoolList.setLayoutManager(new LinearLayoutManager(this));
        schoolList.setHasFixedSize(true);
        schoolList.addItemDecoration(new MarginItemDecorator(this, R.dimen.school_item_margin, true));
        schoolList.setAdapter(groupCellAdapter);

        ViewCompat.setElevation(searchView, getResources().getDimensionPixelSize(R.dimen.sign_in_elevation));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= MIN_NUMBER_LETTERS) {
                    progressState(true);
                    querySubject.onNext(newText);
                } else {
                    showEmptyState(R.drawable.ic_school, R.string.say_no_sign_in_empty);
                }

                return true;
            }
        });
        searchView.setQuery("", true); //initiate text changes

        subscribeSearchView();
    }

    private void showEmptyState(@DrawableRes int iconRes,
                                @StringRes int textRes) {
        progressBar.setVisibility(View.GONE);
        schoolList.setVisibility(View.GONE);

        emptyContainer.setVisibility(View.VISIBLE);
        emptyLogo.setImageResource(iconRes);
        emptyText.setText(textRes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.say_no_sign_in_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.say_no_sign_in_info:
                SayNoInfoDialogFragment.newInstance().show(getSupportFragmentManager(), INFO_TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeSubscription.clear();
        super.onDestroy();
    }

    private void progressState(boolean isInProgress) {
        if (isInProgress) {
            emptyContainer.setVisibility(View.GONE);
        }

        schoolList.setVisibility(isInProgress ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isInProgress ? View.VISIBLE : View.GONE);
    }

    private void subscribeSearchView() {
        compositeSubscription.add(querySubject
                .debounce(2, TimeUnit.SECONDS)
                .flatMap(new Func1<String, Observable<List<Group>>>() {
                    @Override
                    public Observable<List<Group>> call(String queryString) {
                        return networkClient.searchGroupsBy(queryString, 1)
                                .map(GetGroupListResponse::getGroups); //add pages
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groups -> {
                    progressState(false);

                    if (groups.isEmpty()) {
                        showEmptyState(R.drawable.ic_no_result, R.string.say_no_sign_in_no_result);
                    } else {
                        groupCellAdapter.setItems(groups);
                    }
                }, throwable -> {
                    progressState(false);
                    Log.e(TAG, "ERROR", throwable);
                }));
    }

    private void showConfirmationDialog(int groupId) {
        if (confirmationDialog == null) {
            confirmationDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.say_no_sign_in_confirmation_dialog)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, (dialog, which) -> inviteMember(groupId))
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .create();
        }

        confirmationDialog.show();
    }

    private void inviteMember(int groupId) {
        compositeSubscription.add(networkClient.inviteMemberToGroup(groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> {
                    SayNoNotificationDialogFragment.newInstance(R.string.say_no_sign_in_request_sent, false)
                            .setNotificationDialogInteraction(() -> {
                                finish();
                                FeedActivity.start(SayNoSignInActivity.this);
                            })
                            .show(getSupportFragmentManager(), TAG);
                    sayNoFlowInteractor.setInvitationState(InvitationState.PENDING);
                }, throwable -> {
                    Log.d(TAG, "ERROR", throwable);
                    Toast.makeText(SayNoSignInActivity.this, R.string.say_no_sign_in_request_failed, Toast.LENGTH_SHORT).show();
                }));
    }
}