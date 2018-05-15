package com.zhiyicx.common.utils.recycleviewdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/1/5
 * @Contact master.jungle68@gmail.com
 */

public class LinearDecoration extends RecyclerView.ItemDecoration {
    private int top;
    private int bottom;
    private int left;
    private int right;
    private boolean mIsNeedLastDecoration = true;
    private boolean mIsNeedFirstDecoration = false;

    public void setNeedLastDecoration(boolean needLastDecoration) {
        mIsNeedLastDecoration = needLastDecoration;
    }

    public void setNeedFirstDecoration(boolean needFirstDecoration) {
        mIsNeedFirstDecoration = needFirstDecoration;
    }

    public LinearDecoration(int top, int bottom, int left, int right) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public LinearDecoration(int top, int bottom, int left, int right, boolean isNeedLastDecoration) {
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.mIsNeedLastDecoration = isNeedLastDecoration;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = top;
        outRect.bottom = bottom;
        boolean isLastItem = parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1;
        boolean isFirstItem = parent.getChildAdapterPosition(view) == 0;

        boolean notNeedOffset = (isFirstItem && !mIsNeedFirstDecoration)||(isLastItem && !mIsNeedLastDecoration);
        if (notNeedOffset) {
            outRect.bottom = 0;
        }

        outRect.left = left;
        outRect.right = right;

    }

}
