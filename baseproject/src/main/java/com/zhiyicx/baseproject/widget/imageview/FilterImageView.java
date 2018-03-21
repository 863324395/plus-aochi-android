package com.zhiyicx.baseproject.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.zhiyicx.baseproject.R;
import com.zhiyicx.common.utils.SkinUtils;


/**
 * @Describe show #mPressedColor color when pressed
 * @Author Jungle68
 * @Date 2017/2/29
 * @Contact master.jungle68@gmail.com
 */
public class FilterImageView extends android.support.v7.widget.AppCompatImageView {
    private static final int SHAPE_SQUARE = 0;
    private static final int SHAPE_CIRLCE = 1;
    private static final int DEFAULT_PRESSED_COLOR = 0x26000000; // cover：#000000 alpha 15%
    private int mPressedColor = DEFAULT_PRESSED_COLOR; // pressed color
    private int mShape = SHAPE_SQUARE;
    private Paint mPaint;
    private boolean isText;


    private Bitmap mLongImageBitmap;
    private Bitmap mGifImageBitmap;

    private boolean mIshowLongTag;
    private boolean mIshowGifTag;


    public FilterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }


    public FilterImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterImageView(Context context) {
        this(context, null);
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FilterImageView);
        mPressedColor = array.getInteger(R.styleable.FilterImageView_pressColor, DEFAULT_PRESSED_COLOR);
        mShape = array.getInteger(R.styleable.FilterImageView_pressShape, SHAPE_SQUARE);
        array.recycle();
        mPaint = new TextPaint();
        mPaint.setColor(mPressedColor);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isPressed()) {
            switch (mShape) {
                // square
                case SHAPE_SQUARE:
                    canvas.drawColor(mPressedColor);
                    break;
                // circle
                case SHAPE_CIRLCE:
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
                    break;
                default:

            }
        }
        if (isText) {
            mPaint.setColor(SkinUtils.getColor(R.color.general_for_hint));
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);
            mPaint.setTextSize(getWidth() / 2);
            mPaint.setColor(Color.WHITE);
            canvas.drawText("匿", getWidth() / 2 - mPaint.measureText("匿") / 2, getHeight() / 2 - (mPaint.descent() + mPaint.ascent()) / 2, mPaint);
        }
        if (mIshowLongTag && mLongImageBitmap != null) {
            canvas.drawBitmap(mLongImageBitmap, getWidth() - mLongImageBitmap.getWidth(), getHeight() - mLongImageBitmap.getHeight(), null);
        }

        if (mIshowGifTag && mGifImageBitmap != null) {
            canvas.drawBitmap(mGifImageBitmap, (getWidth() - mGifImageBitmap.getWidth()) / 2, (getHeight() - mGifImageBitmap.getHeight()) / 2, null);
        }
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        invalidate();
    }

    public void setIsText(boolean isText) {
        this.isText = isText;
        postInvalidate();
    }

    public void setIshowGifTag(boolean ishowGifTag) {
        mIshowGifTag = ishowGifTag;
        if (ishowGifTag) {
            if (mGifImageBitmap == null) {
                mGifImageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.detail_share_wechat);
            }
            postInvalidate();
        }
    }

    public void showLongImageTag(boolean isShow) {
        this.mIshowLongTag = isShow;
        if (isShow) {
            if (mLongImageBitmap == null) {
                mLongImageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_longpic);
            }
            postInvalidate();
        }
    }
}
