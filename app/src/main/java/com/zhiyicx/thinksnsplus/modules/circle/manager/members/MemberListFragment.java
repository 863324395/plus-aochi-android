package com.zhiyicx.thinksnsplus.modules.circle.manager.members;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.utils.recycleviewdecoration.StickySectionDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CircleMembers;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

/**
 * @author Jliuer
 * @Date 2017/12/08/15:38
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MemberListFragment extends TSListFragment<MembersContract.Presenter, CircleMembers>
        implements MembersContract.View {

    public static final String CIRCLEID = "circleID";

    public static MemberListFragment newInstance(Bundle bundle) {
        MemberListFragment memberListFragment = new MemberListFragment();
        memberListFragment.setArguments(bundle);
        return memberListFragment;
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    public long getCIrcleId() {
        return getArguments().getLong(CIRCLEID);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new CommonAdapter<CircleMembers>(getActivity(), R.layout.item_circle_member, mListDatas) {
            @Override
            protected void convert(ViewHolder holder, CircleMembers circleMembers, int position) {
                holder.setText(R.id.tv_member_name, circleMembers.getUser().getName());
            }
        };
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {

        return new StickySectionDecoration(getActivity(), position -> {

            CircleMembers members = mListDatas.get(position);
            StickySectionDecoration.GroupInfo groupInfo;
            if (CircleMembers.FOUNDER.equals(members.getRole()) || CircleMembers.ADMINISTRATOR.equals(members.getRole())) {
                int groupId = 1;
                groupInfo = new StickySectionDecoration.GroupInfo(groupId, groupId + "");
                groupInfo.setPosition(position);
                groupInfo.setGroupLength(2);
                return groupInfo;
            }

            return null;
        });
    }
}
