package com.conx2share.conx2share.ui.photo_preview;

import com.bumptech.glide.Glide;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.ui.base.BaseActivity;
import com.conx2share.conx2share.util.EventBusUtil;
import com.conx2share.conx2share.util.SnackbarUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import javax.inject.Inject;

import butterknife.ButterKnife;
//import butterknife.InjectView;
import roboguice.inject.InjectView;

public class PhotoPreviewActivity extends BaseActivity {

    private static String TAG = PhotoPreviewActivity.class.getSimpleName();

    @InjectView(R.id.photo_preview_image)
    ImageView photoPreviewImage;

    @InjectView(R.id.photo_edit_text)
    EditText photoEditText;

    @InjectView(R.id.send_photo_button)
    Button sendImageButton;

    @InjectView(R.id.close_photo_preview)
    ImageButton closePhotoPreviewImageButton;

    @Inject
    SnackbarUtil mSnackbarUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_preview);
        ButterKnife.bind(this);

        EventBusUtil.getEventBus().register(this);

        // TODO: replace extra keys with constants
        Bundle b = getIntent().getExtras();
        String mediaType = b.getString("mediaType");
        String defaultText = b.getString("defaultText");

        if (mediaType.equals("photo")) {
            Uri imageUri = Uri.parse(b.getString("imageUri"));
            Glide.with(this).load(imageUri).centerCrop().dontAnimate().into(photoPreviewImage);
        } else {
            String path = b.getString("videoUri");
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
            Drawable videoThumbnail = new BitmapDrawable(getResources(), thumb);
            photoPreviewImage.setImageDrawable(videoThumbnail);
        }

        sendImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("imageText", photoEditText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });

        closePhotoPreviewImageButton.setOnClickListener(v -> onBackPressed());

        if (defaultText != null && !defaultText.equals("")) {
            photoEditText.setText(defaultText);
        }
    }
}
