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
    private int headerCount;
    private int footerCount;
    private boolean mIsNeedLastDecoration = true;

    public void setNeedLastDecoration(boolean needLastDecoration) {
        mIsNeedLastDecoration = needLastDecoration;
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

        if (!mIsNeedLastDecoration && parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1) {// 最后一行
            outRect.bottom = 0;
        } else {
            outRect.bottom = bottom;
        }
        if (parent.getChildAdapterPosition(view) < headerCount){
            outRect.bottom = 0;
        }
        outRect.left = left;
        outRect.right = right;

    }

    public int getHeaderCount() {
        return headerCount;
    }

    public void setHeaderCount(int headerCount) {
        this.headerCount = headerCount;
    }

    public int getFooterCount() {
        return footerCount;
    }

    public void setFooterCount(int footerCount) {
        this.footerCount = footerCount;
    }
}
