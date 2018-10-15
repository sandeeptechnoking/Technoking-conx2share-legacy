package com.conx2share.conx2share.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircularImageView extends android.support.v7.widget.AppCompatImageView {

    private int borderWidth = 2;

    private int viewWidth;

    private int viewHeight;

    private Bitmap image;

    private Paint paint;

    private Paint paintBorder;

    private BitmapShader shader;

    public CircularImageView(Context context) {
        super(context);
        setup();
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {

        paint = new Paint();
        paint.setAntiAlias(true);

        paintBorder = new Paint();
        paintBorder.setAntiAlias(true);
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        this.invalidate();
    }

    public void setBorderColor(int borderColor) {
        if (paintBorder != null) {
            paintBorder.setColor(borderColor);
        }

        this.invalidate();
    }

    private void loadBitmap() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();

        if (bitmapDrawable != null) {
            image = bitmapDrawable.getBitmap();
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {

        loadBitmap();

        if (image != null) {

            image = ThumbnailUtils.extractThumbnail(image, 400, 400);
            shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvas.getWidth(), canvas.getHeight(), false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            int circleCenter = (viewWidth) / 2;

            canvas.drawCircle(circleCenter + borderWidth * 2, circleCenter + borderWidth * 2, circleCenter + borderWidth, paintBorder);
            canvas.drawCircle(circleCenter + borderWidth * 2, circleCenter + borderWidth * 2, circleCenter, paint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec, widthMeasureSpec);

        viewWidth = width - (borderWidth * 2);
        viewHeight = height - (borderWidth * 2);

        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = viewWidth;

        }

        return result;
    }

    private int measureHeight(int measureSpecHeight, int measureSpecWidth) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpecHeight);
        int specSize = MeasureSpec.getSize(measureSpecHeight);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = viewHeight;
        }
        return result;
    }
}