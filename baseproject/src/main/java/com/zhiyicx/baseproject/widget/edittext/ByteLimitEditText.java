package com.zhiyicx.baseproject.widget.edittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.zhiyicx.baseproject.R;

/**
 * @Author Jliuer
 * @Date 2018/05/12/11:47
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ByteLimitEditText extends android.support.v7.widget.AppCompatEditText implements TextWatcher {

    protected boolean needWordByteLenghtLimit;
    protected int wordByteLenghtLimit;

    public ByteLimitEditText(Context context) {
        this(context, null);
    }

    public ByteLimitEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ByteLimitEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ByteLimitEditText);
        wordByteLenghtLimit = typedArray.getInt(R.styleable.ByteLimitEditText_word_byte_limit_lengh, 0);
        needWordByteLenghtLimit = typedArray.getBoolean(R.styleable.ByteLimitEditText_need_word_byte_limit, false);
        typedArray.recycle();
        // 设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (needWordByteLenghtLimit && wordByteLenghtLimit > 0) {
            String inputStr = editable.toString().trim();
            byte[] bytes = inputStr.getBytes();
            if (bytes.length > wordByteLenghtLimit) {
                // 截取取字节
                byte[] newBytes = new byte[wordByteLenghtLimit];
                for (int i = 0; i < wordByteLenghtLimit; i++) {
                    newBytes[i] = bytes[i];
                }
                String newStr = new String(newBytes);
                setText(newStr);
                //将光标定位到最后
                Selection.setSelection(getEditableText(), newStr.length());
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}
