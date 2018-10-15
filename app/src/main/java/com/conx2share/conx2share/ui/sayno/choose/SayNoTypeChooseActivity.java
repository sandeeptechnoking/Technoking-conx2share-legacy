package com.conx2share.conx2share.ui.sayno.choose;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.sayno.AttachmentHolder;
import com.conx2share.conx2share.ui.sayno.SayNoActivityType;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SayNoTypeChooseActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101;

    private static final String EXTRA_TYPE = "type-extra";
    private static final String EXTRA_ATTACHMENT = "attachment_holder";

    public static void startForResult(Activity activity) {
        startForResult(activity, null);
    }

    public static void startForResult(Activity activity,
                                      @Nullable AttachmentHolder attachmentHolder) {
        Intent intent = new Intent(activity, SayNoTypeChooseActivity.class);
        if (attachmentHolder != null) {
            intent.putExtra(EXTRA_ATTACHMENT, attachmentHolder);
        }

        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    public static boolean canHandle(int requestCode) {
        return REQUEST_CODE == requestCode;
    }

    public static boolean isAnonymous(Intent data) {
        return SayNoAnonymityChooseActivity.isAnonymous(data);
    }

    public static SayNoActivityType getType(Intent data) {
        return (SayNoActivityType) data.getSerializableExtra(EXTRA_TYPE);
    }

    @Nullable
    public static AttachmentHolder getAttachmentHolder(Intent data) {
        return data.getParcelableExtra(EXTRA_ATTACHMENT);
    }

    private SayNoActivityType type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_say_no_type_chooser);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            type = (SayNoActivityType) savedInstanceState.getSerializable(EXTRA_TYPE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_TYPE, type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK &&
                SayNoAnonymityChooseActivity.canHandle(requestCode)) {
            Intent resultIntent = new Intent()
                    .putExtra(EXTRA_TYPE, type)
                    .putExtras(data.getExtras());

            if (getIntent().getExtras() != null) {
                resultIntent.putExtras(getIntent().getExtras());
            }

            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    @OnClick(R.id.say_no_type_chooser_chat)
    void onChooseChatClicked() {
        selectTypeGoNext(SayNoActivityType.CHAT);
    }

    @OnClick(R.id.say_no_type_chooser_report)
    void onChooseReportClicked() {
        selectTypeGoNext(SayNoActivityType.REPORT);
    }

    private void selectTypeGoNext(SayNoActivityType type) {
        this.type = type;
        SayNoAnonymityChooseActivity.startForResult(this);
    }
}