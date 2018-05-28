package com.zhiyicx.baseproject.widget.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.TouchableSpan;
import com.zhiyicx.baseproject.R;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/12/21
 * @Contact master.jungle68@gmail.com
 */
public class SpanTextViewWithEllipsize extends android.support.v7.widget.AppCompatTextView {
    private int mLastCharDown = 0;
    private SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
    private TouchableSpan mTouchableSpan = new TouchableSpan();
    private CharSequence mCharSequence;

    public SpanTextViewWithEllipsize(Context context) {
        this(context, null);
    }

    public SpanTextViewWithEllipsize(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpanTextViewWithEllipsize(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMovementMethod(new LinkTouchMovementMethod());
    }

    /**
     * 注意：spannableString 设置Spannable 的对象到spannableString中时，要用Spannable.SPAN_EXCLUSIVE_EXCLUSIVE的flag值，不然可能会会出现后面的衔接字符串不会显示
     */
    @Override
    protected void onDraw(Canvas canvas) {
        mCharSequence = getText();
        try {
            // 最多显示 3 行
            int lineCount = getLineCount();
            int needCount = getContext().getResources().getInteger(R.integer.dynamic_list_content_show_lines) - 2;
            mLastCharDown = getLayout().getLineVisibleEnd(Math.min(lineCount, needCount));
        } catch (Exception ignored) {
            mLastCharDown = 0;
        }
        if (mLastCharDown > 0 && mCharSequence.length() > mLastCharDown) {
            spannableStringBuilder.clear();
            String lookMore = "『..查看更多』";
            spannableStringBuilder
                    .append(mCharSequence.subSequence(0, mLastCharDown))
                    .append(lookMore);

            spannableStringBuilder.setSpan(mTouchableSpan, spannableStringBuilder.length() - lookMore.length(),
                    spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            setText(spannableStringBuilder);
        }
        super.onDraw(canvas);
    }

    public class TouchableSpan extends ClickableSpan {
        private boolean mIsPressed;

        public void setPressed(boolean isSelected) {
            mIsPressed = isSelected;
        }

        @Override
        public void onClick(View widget) {

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            CharSequence charSequence=getText();
            ds.setColor(0xFF3498DB);
            ds.bgColor = mIsPressed ? 0x55999999 : 0;
            ds.setUnderlineText(false);
        }
    }

    public class LinkTouchMovementMethod extends LinkMovementMethod {
        private TouchableSpan mPressedSpan;

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
                            spannable.getSpanEnd(mPressedSpan));
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            } else {
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return true;
        }

        private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
            TouchableSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        }
    }
}
