package com.conx2share.conx2share.ui.events;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Event;
import com.conx2share.conx2share.network.NetworkClient;
import com.conx2share.conx2share.network.models.param.EventParam;
import com.conx2share.conx2share.ui.base.BaseAppCompatActivity;
import com.conx2share.conx2share.util.DateUtils;
import com.conx2share.conx2share.util.MediaUploadUtil;
import com.conx2share.conx2share.util.SnackbarUtil;
import com.conx2share.conx2share.util.TypedUri;
import com.conx2share.conx2share.util.ValidationUtil;
import com.nispok.snackbar.SnackbarManager;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class NewEventActivity extends BaseAppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = NewEventActivity.class.getSimpleName();

    public static final int PERMISSION_CAMERA_RESULT = 1000;

    @InjectView(R.id.new_event_collapsing_bar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @InjectView(R.id.new_event_toolbar)
    Toolbar toolbar;

    @InjectView(R.id.new_event_backdrop_img)
    ImageView collapseBackdropImage;

    @InjectView(R.id.new_event_name_input_edit_text)
    TextInputEditText eventNameInputET;

    @InjectView(R.id.new_event_description_input_edit_text)
    TextInputEditText eventDescriptionInputET;

    @InjectView(R.id.new_event_location_input_edit_text)
    TextInputEditText eventLocationInputET;

    @InjectView(R.id.new_event_date_value_tv)
    TextView eventDateTv;

    @InjectView(R.id.new_event_time_value_tv)
    TextView eventTimeTv;

    @Inject
    SnackbarUtil snackbarUtil;

    @Inject
    NetworkClient networkClient;

    private Event event;
    private int eventId;
    private int groupId;
    private String groupType;
    private TypedUri attachmentUri;

    private boolean isLoading;
    private boolean isSavedInstanceState, eventCreated;

    private ProgressDialog progressDialog;
    private MediaUploadUtil mediaUploadUtil;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);

        parseExtras(getIntent().getExtras());
        initActionBar();
        initViews();
    }

    private void parseExtras(Bundle extras) {
        groupId = extras.getInt(EventsListActivity.EXTRA_GROUP_ID, -1);
        groupType = extras.getString(EventsListActivity.EXTRA_GROUP_TYPE);
        event = extras.getParcelable(EventsListActivity.EXTRA_EVENT_OBJECT);
        if (event != null) {
            eventId = event.getId();
        } else {
            eventId = -1;
        }
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (!isEditMode()) {
                actionBar.setTitle(R.string.new_event_title);
            }
        }
    }

    private void initViews() {
        mediaUploadUtil = new MediaUploadUtil(this, null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.creating_event));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if (isEditMode()) {
            progressDialog.setMessage(getString(R.string.updating_event));
            populateUi(event);
        }
    }

    private void populateUi(Event event) {

        Glide.with(this)
                .load(event.getUrl())
                .centerCrop()
                .dontAnimate()
                .into(collapseBackdropImage);

        collapsingToolbarLayout.setTitle(event.getName());
        eventNameInputET.setText(event.getName());
        eventDescriptionInputET.setText(event.getDescription());
        eventDateTv.setText(DateUtils.getFormattedLocalDate(event.getStart_time()));
        eventDateTv.setTag(DateUtils.getDateMillis(event.getStart_time()));
        eventTimeTv.setText(DateUtils.getFormattedLocalTime(event.getStart_time()));
        eventTimeTv.setTag(DateUtils.getTimeMillis(event.getStart_time()));
        eventLocationInputET.setText(event.getLocation());
    }

    private boolean isEditMode() {
        return (eventId != -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.create_event_done_btn) {
            hideKeyboard();
            createOrUpdateEvent();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void createOrUpdateEvent() {
        if (isFormValid()) {
            if (!isEditMode()) {
                createEvent(attachmentUri, groupId, eventNameInputET.getText().toString().trim(),
                        eventDescriptionInputET.getText().toString().trim(), getEventDateTime(), getEventDateTime(),
                        eventLocationInputET.getText().toString().trim(), eventDescriptionInputET.getText().toString().trim());
            } else {
                updateEvent(attachmentUri, groupId, eventNameInputET.getText().toString().trim(),
                        eventDescriptionInputET.getText().toString().trim(), getEventDateTime(), getEventDateTime(),
                        eventLocationInputET.getText().toString().trim(), eventDescriptionInputET.getText().toString().trim());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSavedInstanceState && eventCreated) {
            setResult(Activity.RESULT_OK);
            onBackPressed();
        }
        isSavedInstanceState = false;
    }

    private boolean isFormValid() {
        if (!ValidationUtil.checkIfEditTextIsValid(eventNameInputET)) {
            snackbarUtil.displaySnackBar(this, R.string.event_validation_name);
            return false;
        }
        if (!isEditMode()) {
            if (attachmentUri == null) {
                snackbarUtil.displaySnackBar(this, R.string.event_validation_photo);
                return false;
            }
        }
        if (!ValidationUtil.checkIfEditTextIsValid(eventDescriptionInputET)) {
            snackbarUtil.displaySnackBar(this, R.string.event_validation_description);
            return false;
        }
        if (!ValidationUtil.checkIfStringIsValid(eventDateTv.getText().toString())) {
            snackbarUtil.displaySnackBar(this, R.string.event_validation_date);
            return false;
        }
        if (!ValidationUtil.checkIfStringIsValid(eventTimeTv.getText().toString())) {
            snackbarUtil.displaySnackBar(this, R.string.event_validation_time);
            return false;
        }
        return true;

    }

    @OnFocusChange(R.id.new_event_name_input_edit_text)
    public void onEventNameFocusChanged(View view, boolean hasFocus) {
        if (!hasFocus) {
            String title = eventNameInputET.getText().toString().trim();
            if (!TextUtils.isEmpty(title)) {
                collapsingToolbarLayout.setTitle(title);
            }
        }
    }

    @OnClick(R.id.choose_event_image_btn)
    public void changeStreamImageButton() {
        if (hasCameraPermission()) {
            mediaUploadUtil.launchUploadPhotoDialog(null);
        } else {
            requestCameraPermission();
        }
    }

    public boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_CAMERA_RESULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA_RESULT ) {
            boolean permissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false;
                    break;
                }
            }
            if (permissionsGranted) {
                mediaUploadUtil.launchUploadPhotoDialog(null);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isSavedInstanceState = false;

        TypedUri photoUri = mediaUploadUtil.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "Photo Uri: " + photoUri);

        if (resultCode == Activity.RESULT_OK) {
            if (photoUri != null) {
                attachmentUri = photoUri;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    if (DocumentsContract.isDocumentUri(this, attachmentUri.getUri())) {
                        String documentPath = mediaUploadUtil.getStorageFrameworkPath(this
                                .getContentResolver(), attachmentUri.getUri());
                        if (documentPath != null) {
                            Uri uri = Uri.parse(documentPath);
                            attachmentUri.setUri(uri);
                            attachmentUri.setFilePath(documentPath);
                        } else {
                            // There was an error
                            Toast.makeText(this, getString(R.string.something_went_wrong_while_uploading_your_file), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                Log.d(TAG, "mAttachmentUri.getFilePath(): " + attachmentUri.getFilePath());

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(attachmentUri.getFilePath(), options);

                Glide.with(this).load("file:" + attachmentUri.getFilePath())
                        .dontAnimate()
                        .centerCrop()
                        .into(collapseBackdropImage);
            } else {
                snackbarUtil.showSnackBarWithoutAction(this, R.string
                        .something_went_wrong_while_uploading_your_image);
            }
        } else {
            Log.e(TAG, "resultCode was not OK. resultCode: " + resultCode);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        isSavedInstanceState = true;
    }

    @OnClick({R.id.new_event_date_value_tv, R.id.new_event_date_arrow})
    public void showDatePicker() {
        Calendar currentCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.ConxPicker, this,
                currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar
                .DAY_OF_MONTH));
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), datePickerDialog);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog);
        datePickerDialog.show();
    }

    @OnClick({R.id.new_event_time_value_tv, R.id.new_event_time_arrow})
    public void showTimePicker() {
        Calendar currentCalendar = Calendar.getInstance();
        TimePickerDialog datePickerDialog = new TimePickerDialog(this, R.style.ConxPicker, this,
                currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), datePickerDialog);
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), datePickerDialog);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);
        eventDateTv.setText(DateUtils.getFormattedLocalDate(calendar.getTimeInMillis()));
        eventDateTv.setTag(calendar.getTimeInMillis());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        eventTimeTv.setText(DateUtils.getFormattedLocalTime(calendar.getTimeInMillis()));
        eventTimeTv.setTag(calendar.getTimeInMillis());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
    }


    public String getEventDateTime() {
        return DateUtils.getIsoDateTime(((long) eventDateTv.getTag()), ((long) eventTimeTv.getTag()));
    }

    private void createEvent(TypedUri attachmentUri, int groupId, String eventName, String eventDescription, String
            eventStartTime, String eventEndTime, String eventLocation, String eventAbout) {
        hideKeyboard();
        enableProgressDialog(true);

        if (isLoading) return;
        isLoading = true;

        EventParam eventParams = new EventParam(groupType, groupId, eventId, eventAbout, eventDescription, eventStartTime,
                eventEndTime, eventLocation, eventName, attachmentUri);

        compositeSubscription
                .add(networkClient.createEvent(eventParams)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            isLoading = false;
                            eventCreated = true;
                            enableProgressDialog(false);
                            Toast.makeText(NewEventActivity.this, String.format(getString(R.string.event_creation_success),
                                    response.getEvent().getName()), Toast.LENGTH_SHORT).show();
                            if (!isSavedInstanceState) {
                                setResult(Activity.RESULT_OK);
                                onBackPressed();
                            }
                        }, throwable -> {
                            enableProgressDialog(false);
                            isLoading = false;
                            snackbarUtil.showSnackBarWithAction(NewEventActivity.this, R.string.event_creation_fail, R.string.retry,
                                    snackbar -> {
                                        createEvent(NewEventActivity.this.attachmentUri, NewEventActivity.this.groupId,
                                                eventNameInputET.getText().toString().trim(), eventDescriptionInputET.getText().toString().trim(),
                                                getEventDateTime(), getEventDateTime(), eventLocationInputET.getText().toString().trim(),
                                                eventDescriptionInputET.getText().toString().trim());
                                        SnackbarManager.dismiss();
                                    });
                        }));

    }

    private void updateEvent(TypedUri attachmentUri, int groupId, String eventName, String eventDescription, String
            eventStartTime, String eventEndTime, String eventLocation, String eventAbout) {
        hideKeyboard();
        enableProgressDialog(true);

        if (isLoading)  return;
        isLoading = true;

        EventParam eventParams = new EventParam(groupType, groupId, event.getId(), eventAbout, eventDescription,
                eventStartTime, eventEndTime, eventLocation, eventName, attachmentUri);

        compositeSubscription
                .add(networkClient.updateEvent(eventParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventResponse -> {
                    isLoading = false;
                    enableProgressDialog(false);
                    Toast.makeText(NewEventActivity.this, String.format(getString(R.string.event_update_success),
                            eventResponse.getEvent().getName()), Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    onBackPressed();
                }, throwable -> {
                    enableProgressDialog(false);
                    isLoading = false;
                    snackbarUtil.showSnackBarWithAction(NewEventActivity.this, R.string.event_update_fail, R.string.retry,
                            snackbar -> {
                                updateEvent(NewEventActivity.this.attachmentUri, NewEventActivity.this.groupId,
                                        eventNameInputET.getText().toString().trim(), eventDescriptionInputET.getText().toString().trim(),
                                        getEventDateTime(), getEventDateTime(), eventLocationInputET.getText().toString().trim(),
                                        eventDescriptionInputET.getText().toString().trim());
                                SnackbarManager.dismiss();
                            });
                }));
    }

    private void enableProgressDialog(boolean enable) {
        if (progressDialog != null) {
            if (enable && !progressDialog.isShowing()) {
                progressDialog.show();
            } else if (!enable && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eventNameInputET.getWindowToken(), 0);
    }

}
