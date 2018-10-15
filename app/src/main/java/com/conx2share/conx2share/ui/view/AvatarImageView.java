package com.conx2share.conx2share.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Friend;
import com.conx2share.conx2share.network.models.User;
import com.conx2share.conx2share.util.MaskTransformation;

public class AvatarImageView extends android.support.v7.widget.AppCompatImageView {

    public AvatarImageView(Context context) {
        super(context);
    }

    public AvatarImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initView(User user){
        initView(user.getAvatarUrl(), user.getFirstName(), user.getLastName());
    }

    public void initView(Friend friend){
        initView(friend.getFriendPhotoUrl(), friend.getFriendFirstName(), friend.getFriendLastName());
    }

    public void initView(String avatarUrl, String name) {
        initView(avatarUrl, name, "");
    }

    public void initView(String avatarUrl, String firstName, String lastName) {
        Glide.clear(this);
        if (!TextUtils.isEmpty(avatarUrl)) {
            Glide.with(getContext()).load(avatarUrl)
                    .placeholder(R.drawable.v_avatar_placeholder)
                    .error(R.drawable.v_avatar_placeholder)
                    .dontAnimate()
                    .fitCenter()
                    .bitmapTransform(new CenterCrop(getContext()), MaskTransformation.getAvatarTransform(getContext()))
                    .into(this);
        } else {
            StringBuilder letters = new StringBuilder();
            if (!TextUtils.isEmpty(firstName)) {
                for (char character : firstName.toCharArray()) {
                    if (Character.isLetter(character)) {
                        letters.append(character);
                        break;
                    }
                }
            }
            if (!TextUtils.isEmpty(lastName)) {
                for (char character : lastName.toCharArray()) {
                    if (Character.isLetter(character)) {
                        letters.append(character);
                        break;
                    }
                }
            }
            if (!TextUtils.isEmpty(letters)) {
                setPlaceHolder(letters.toString().toUpperCase());
            }
        }
    }

    private void setPlaceHolder(String letters) {

        Drawable mIcon = ContextCompat.getDrawable(getContext(), R.drawable.avatar_mask_tv).mutate();
        Bitmap myBitmap = drawableToBitmap(mIcon);

        if (myBitmap == null) {
            mIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.conx_primary), PorterDuff.Mode.SRC_IN);
            this.setImageDrawable(mIcon);
        } else {

            Paint paint = new Paint();
            ColorFilter filter = new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.conx_primary), PorterDuff.Mode.SRC_IN);
            paint.setColorFilter(filter);
            Bitmap newBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas myCanvas = new Canvas(newBitmap);
            myCanvas.drawBitmap(newBitmap, 0, 0, paint);

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
            textPaint.setTextSize(newBitmap.getHeight() / 2);
            textPaint.setTextAlign(Paint.Align.CENTER);
            myCanvas.drawText(letters, myBitmap.getWidth() / 2, myBitmap.getHeight() / 3 * 2, textPaint);

            this.setImageBitmap(newBitmap);
        }
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}

