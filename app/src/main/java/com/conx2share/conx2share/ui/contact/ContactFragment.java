package com.conx2share.conx2share.ui.contact;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Contact;
import com.conx2share.conx2share.model.ContactParams;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.Result;
import com.conx2share.conx2share.network.models.response.SuccessResponse;
import com.conx2share.conx2share.ui.base.BaseFragment;
import com.conx2share.conx2share.ui.contact.adapter.ContactsAdapter;
import com.conx2share.conx2share.util.PermissionUtil;
import com.conx2share.conx2share.util.ViewUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

//import butterknife.InjectView;
import butterknife.OnClick;
import roboguice.inject.InjectView;

public class ContactFragment extends BaseFragment {

    public static final String TAG = ContactFragment.class.getSimpleName();
    private static final int REQUEST_PERMISSION_SETTING = 1003;

    @Inject
    NetworkClient mNetworkClient;

    @InjectView(R.id.contact_list_view)
    ListView mContactListView;

    @InjectView(R.id.invite_button)
    FloatingActionButton inviteButton;

    @InjectView(R.id.search_edit_text)
    EditText mSearchEditText;

    @InjectView(R.id.contact_permission_layout)
    RelativeLayout mDenyLayout;

    private List<Contact> mContacts;

    private ContactParams mContactParams;

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (hasContactPermission()) {
            new GatherFriendsAsync().execute();
        } else {
            requestContactPermission();
        }

        mDenyLayout.setOnClickListener(v -> {
            if (hasContactPermission()) {
                new GatherFriendsAsync().execute();
                mDenyLayout.setVisibility(View.GONE);
            } else if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", getActivity().getPackageName(), null)), REQUEST_PERMISSION_SETTING);
            } else {
                requestContactPermission();
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.send_report).setOnMenuItemClickListener(v -> {

            ContactsAdapter contactsAdapter = (ContactsAdapter) mContactListView.getAdapter();

            if (contactsAdapter != null) {

                List<Contact> chosenContacts = contactsAdapter.getChosenContacts();
                if (!chosenContacts.isEmpty()) {

                    inviteButton.setEnabled(false);

                    List<String> chosenEmails = new ArrayList<>();
                    for (int i = 0; i < chosenContacts.size(); i++) {
                        chosenEmails.add(chosenContacts.get(i).getEmail());
                    }

                    mContactParams = new ContactParams();
                    mContactParams.setEmails(chosenEmails);

                    new InviteFriendsAsyncTask().execute(mContactParams);
                }
            }
            return true;
        });
        super.onPrepareOptionsMenu(menu);
    }

    @OnClick(R.id.invite_button)
    void inputNewEmail() {
        AddEmailDialog addEmailDialog = new AddEmailDialog(getActivity(), R.style.EmailDialog);
        addEmailDialog.setEmailListener(email -> {
            ContactParams contactParams = new ContactParams();
            List<String> chosenEmails = new ArrayList<>();
            chosenEmails.add(email);
            contactParams.setEmails(chosenEmails);
            new InviteFriendsAsyncTask().execute(contactParams);
        });
        addEmailDialog.setCanceledOnTouchOutside(true);
        addEmailDialog.setOnDismissListener(dialog -> ViewUtil.hideKeyboard(getActivity()));
        addEmailDialog.setOnCancelListener(dialog -> ViewUtil.hideKeyboard(getActivity()));
        addEmailDialog.show();
    }

    private void initSearchBar() {

        mSearchEditText.setHint(getString(R.string.search_for_users_to_invite_text));
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                ContactsAdapter contactsAdapter = (ContactsAdapter) mContactListView.getAdapter();
                if (contactsAdapter != null) {
                    contactsAdapter.getFilter().filter(s.toString());
                }
            }
        });
    }

    public List<Contact> readContacts() {

        List<Contact> contacts = new ArrayList<>();

        ContentResolver cr = getActivity().getContentResolver();
        String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID};
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";

        Cursor contactsCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
        while (contactsCursor != null && contactsCursor.moveToNext()) {
            contacts.add(new Contact(contactsCursor));
        }

        if (contactsCursor != null) contactsCursor.close();
        return contacts;
    }

    private void initAdapter(List<Contact> contacts) {

        ContactsAdapter adapter = new ContactsAdapter(getActivity(), contacts);
        mContactListView.setAdapter(adapter);

        mContactListView.setOnItemClickListener((parent, view, position, id) -> {

            Contact contact = (Contact) parent.getItemAtPosition(position);
            CheckBox contactCheckBox = (CheckBox) view.findViewById(R.id.contact_checkbox);

            if (contactCheckBox.isChecked()) {

                contactCheckBox.setChecked(false);
                contact.setChosenForInvite(false);
            } else {

                contactCheckBox.setChecked(true);
                contact.setChosenForInvite(true);
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CONTACT_RESULT) {
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                new GatherFriendsAsync().execute();
                mDenyLayout.setVisibility(View.GONE);
            } else {
                mDenyLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public class GatherFriendsAsync extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected List<Contact> doInBackground(Void... params) {
            return readContacts();
        }

        @Override
        protected void onPostExecute(List<Contact> result) {
            if (result != null) {
                mContacts = result;
                initAdapter(mContacts);
                inviteButton.setEnabled(true);
                initSearchBar();
            } else {
                inviteButton.setEnabled(false);
                if (getActivity() != null) {
                    SnackbarManager.show(
                            Snackbar.with(getActivity())
                                    .type(SnackbarType.MULTI_LINE)
                                    .text(getString(R.string.unable_to_gather_friends_text))
                                    .actionLabel(getString(R.string.retry))
                                    .actionListener(snackbar -> {
                                        new GatherFriendsAsync().execute();
                                        SnackbarManager.dismiss();
                                    })
                            , getActivity());
                }
            }
        }
    }

    public class InviteFriendsAsyncTask extends AsyncTask<ContactParams, Void, Result<SuccessResponse>> {

        @Override
        protected Result<SuccessResponse> doInBackground(ContactParams... params) {

            return mNetworkClient.inviteFriends(params[0]);
        }

        @Override
        protected void onPostExecute(Result<SuccessResponse> inviteResult) {

            super.onPostExecute(inviteResult);

            if (getActivity() != null && !getActivity().isFinishing() && inviteResult != null) {

                inviteButton.setEnabled(true);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inviteButton.getWindowToken(), 0);
                if (inviteResult.getError() == null) {
                    Snackbar.with(getActivity()).text(getString(R.string.invite_successful)).show(getActivity());
                } else {
                    SnackbarManager.show(
                            Snackbar.with(getActivity())
                                    .type(SnackbarType.MULTI_LINE)
                                    .text(getString(R.string.unable_to_invite_friends_text))
                                    .actionLabel(getString(R.string.retry))
                                    .actionListener(snackbar -> {
                                        new InviteFriendsAsyncTask().execute(mContactParams);
                                        SnackbarManager.dismiss();
                                    })
                            , getActivity());
                }
            }
        }
    }
}