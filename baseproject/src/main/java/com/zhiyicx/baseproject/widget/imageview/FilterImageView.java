package com.zhiyicx.baseproject.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.zhiyicx.baseproject.R;
import com.zhiyicx.common.utils.log.LogUtils;

import static java.lang.Compiler.enable;

/**
 * @Describe show #mPressedColor color when pressed
 * @Author Jungle68
 * @Date 2017/2/29
 * @Contact master.jungle68@gmail.com
 */

public class FilterImageView extends ImageView {
    private static final int SHAPE_SQUARE = 0;
    private static final int SHAPE_CIRLCE = 1;
    private static final int DEFAULT_PRESSED_COLOR = 0x26000000; // cover：#000000 alpha 15%
    private int mPressedColor = DEFAULT_PRESSED_COLOR;// pressed color
    private int mShape = SHAPE_SQUARE;
    private Paint mPaint;
    private Rect mRect;

    public FilterImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }


    public FilterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public FilterImageView(Context context) {
        super(context);
        enable();
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        enable();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FilterImageView);
        mPressedColor = array.getInteger(R.styleable.FilterImageView_pressColor, DEFAULT_PRESSED_COLOR);
        mShape = array.getInteger(R.styleable.FilterImageView_pressShape, SHAPE_SQUARE);
        array.recycle();
        mPaint = new Paint();
        mPaint.setColor(mPressedColor);
        mRect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isPressed()) {
            switch (mShape) {
                case SHAPE_SQUARE: // square
                    canvas.drawColor(mPressedColor);
                    break;
                case SHAPE_CIRLCE: // circle
                    canvas.getClipBounds(mRect);
                    canvas.drawCircle(mRect.centerX(), mRect.centerY(), canvas.getWidth() / 2, mPaint);
                    break;
                default:

            }
        }
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        super.dispatchSetPressed(pressed);
        invalidate();
    }

}
