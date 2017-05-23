package com.zhiyicx.thinksnsplus.modules.wallet.withdrawals.detail;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.recycleviewdecoration.CustomLinearDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.wallet.account.AccountActivity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import butterknife.BindView;

/**
 * @Author Jliuer
 * @Date 2017/05/23/10:56
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class WithdrawalsDetailFragment extends TSListFragment {

    @BindView(R.id.v_shadow)
    View mVshadow;

    private ActionPopupWindow mActionPopupWindow;

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_withdrawals_detail;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.withdraw_details);
    }

    @Override
    protected void setCenterClick() {
        mActionPopupWindow.showTop();
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
//        mToolbarCenter.setCompoundDrawables(null,null,getResources().getDrawable(R.mipmap.arr),null);
        mActionPopupWindow = ActionPopupWindow.builder()
                .with(getActivity())
                .isFocus(true)
                .isOutsideTouch(true)
                .parentView(mDriver)
                .animationStyle(R.style.style_actionPopupAnimation_revert)
                .item1Str(getString(R.string.withdraw_all))
                .item2Str(getString(R.string.withdraw_out))
                .item3Str(getString(R.string.withdraw_in))
                .item1ClickListener(new ActionPopupWindow.ActionPopupWindowItem1ClickListener() {
                    @Override
                    public void onItem1Clicked() {
                        mActionPopupWindow.hide();
                    }
                })
                .item2ClickListener(new ActionPopupWindow.ActionPopupWindowItem2ClickListener() {
                    @Override
                    public void onItem2Clicked() {
                        mActionPopupWindow.hide();
                    }
                })
                .item3ClickListener(new ActionPopupWindow.ActionPopupWindowItem3ClickListener() {
                    @Override
                    public void onItem3Clicked() {
                        mActionPopupWindow.hide();
                    }
                })
                .dismissListener(new ActionPopupWindow.ActionPopupWindowShowOrDismissListener() {
                    @Override
                    public void onShow() {
                        mVshadow.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onDismiss() {
                        mVshadow.setVisibility(View.GONE);
                    }
                })
                .build();
    }

    @Override
    protected void initData() {
        super.initData();
        mListDatas.add("");
        mListDatas.add("");
        mListDatas.add("");
        mListDatas.add("");
        mListDatas.add("");
        mListDatas.add("");
        mListDatas.add("");
        mListDatas.add("");
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        CommonAdapter adapter = new CommonAdapter<String>(getActivity(), R.layout.item_withdrawals_detail, mListDatas) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView desc = holder.getView(R.id.withdrawals_desc);
                if (position % 2 == 0) {
                    desc.setEnabled(false);
                }
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(getActivity(), AccountActivity.class));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        return adapter;
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new CustomLinearDecoration(0, getResources().getDimensionPixelSize(R.dimen
                .divider_line), 0, 0, ContextCompat.getDrawable(getContext(), R.drawable
                .shape_recyclerview_grey_divider));

    }

    @Override
    protected List requestCacheData(Long maxId, boolean isLoadMore) {
        return null;
    }

    @Override
    protected void requestNetData(Long maxId, boolean isLoadMore) {
    }
}