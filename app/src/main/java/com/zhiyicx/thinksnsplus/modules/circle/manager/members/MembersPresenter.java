package com.zhiyicx.thinksnsplus.modules.circle.manager.members;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.base.AppBasePresenter;
import com.zhiyicx.thinksnsplus.base.BaseSubscribeForV2;
import com.zhiyicx.thinksnsplus.data.beans.CircleMembers;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @author Jliuer
 * @Date 2017/12/08/15:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MembersPresenter extends AppBasePresenter<MembersContract.Repository,
        MembersContract.View>
        implements MembersContract.Presenter {

    public static final String TYPE_ALL = "all";

    @Inject
    public MembersPresenter(MembersContract.Repository repository, MembersContract.View rootView) {
        super(repository, rootView);
    }

    @Override
    public void requestNetData(Long maxId, boolean isLoadMore) {
        int grouLengh[] = new int[4];
        mRepository.getCircleMemberList(mRootView.getCIrcleId(), maxId.intValue(), TSListFragment
                .DEFAULT_ONE_PAGE_SIZE, TYPE_ALL)
                .flatMap(circleMembers -> {
                    List<CircleMembers> manager = new ArrayList<>();
                    List<CircleMembers> member = new ArrayList<>();
                    List<CircleMembers> blacklist = new ArrayList<>();
                    for (CircleMembers members : circleMembers) {
                        switch (members.getRole()) {
                            case CircleMembers.FOUNDER:
                                grouLengh[0]++;
                                manager.add(0, members);
                                break;
                            case CircleMembers.ADMINISTRATOR:
                                manager.add(members);
                                grouLengh[1]++;
                                break;
                            case CircleMembers.MEMBER:
                                member.add(members);
                                grouLengh[2]++;
                                break;
                            case CircleMembers.BLACKLIST:
                                blacklist.add(members);
                                grouLengh[3]++;
                                break;
                            default:
                        }
                    }
                    manager.addAll(member);
                    manager.addAll(blacklist);
                    return Observable.just(manager);
                })
                .subscribe(new BaseSubscribeForV2<List<CircleMembers>>() {
                    @Override
                    protected void onSuccess(List<CircleMembers> data) {
                        mRootView.setGroupLengh(grouLengh);
                        mRootView.onNetResponseSuccess(data, isLoadMore);
                    }

                    @Override
                    protected void onFailure(String message, int code) {
                        super.onFailure(message, code);
                        mRootView.showSnackErrorMessage(message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mRootView.onResponseError(e, isLoadMore);
                    }
                });
    }

    @Override
    public void requestCacheData(Long maxId, boolean isLoadMore) {
        mRootView.onCacheResponseSuccess(null, isLoadMore);
    }

    @Override
    public void dealCircleMember(MemberHandleType type, CircleMembers members) {
        if (type == null) {
            return;
        }
        long circleId = members.getGroup_id();
        long memberId = members.getId();
        Observable<BaseJsonV2<Object>> observable = null;
        switch (type) {
            case APPOINT_MANAFER:
                observable = mRepository.appointCircleManager(circleId, memberId);
                break;
            case CANCLE_MANAFER:
                observable = mRepository.cancleCircleManager(circleId, memberId);
                break;
            case CANCLE_MEMBER:
                observable = mRepository.cancleCircleMember(circleId, memberId);
                break;
            case APPOINT_BLACKLIST:
                observable = mRepository.appointCircleBlackList(circleId, memberId);
                break;
            case CANCLE_BLACKLIST:
                observable = mRepository.cancleCircleBlackList(circleId, memberId);
                break;
            default:
        }
        if (observable == null) {
            return;
        }
        Subscription subscription = observable.subscribe(new BaseSubscribeForV2<BaseJsonV2<Object>>() {
            @Override
            protected void onSuccess(BaseJsonV2<Object> data) {
                int[] groupLengh = mRootView.getGroupLengh();
                switch (type) {
                    case APPOINT_MANAFER:
                        members.setRole(CircleMembers.ADMINISTRATOR);
                        groupLengh[1]++;
                        groupLengh[2]--;
                        break;
                    case CANCLE_MANAFER:
                        members.setRole(CircleMembers.MEMBER);
                        groupLengh[1]--;
                        groupLengh[2]++;
                        break;
                    case CANCLE_MEMBER:
                        groupLengh[2]--;
                        mRootView.getListDatas().remove(members);
                        break;
                    case APPOINT_BLACKLIST:
                        members.setRole(CircleMembers.BLACKLIST);
                        groupLengh[3]++;
                        groupLengh[2]--;
                        break;
                    case CANCLE_BLACKLIST:
                        members.setRole(CircleMembers.MEMBER);
                        groupLengh[2]++;
                        groupLengh[3]--;
                        break;
                    default:
                        break;
                }
                Observable.just(mRootView.getListDatas())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(circleMembers -> {
                            List<CircleMembers> manager = new ArrayList<>();
                            List<CircleMembers> member = new ArrayList<>();
                            List<CircleMembers> blacklist = new ArrayList<>();
                            for (CircleMembers members1 : mRootView.getListDatas()) {
                                switch (members1.getRole()) {
                                    case CircleMembers.FOUNDER:
                                        manager.add(0, members1);
                                        break;
                                    case CircleMembers.ADMINISTRATOR:
                                        manager.add(members1);
                                        break;
                                    case CircleMembers.MEMBER:
                                        member.add(members1);
                                        break;
                                    case CircleMembers.BLACKLIST:
                                        blacklist.add(members1);
                                        break;
                                    default:
                                }
                            }
                            manager.addAll(member);
                            manager.addAll(blacklist);
                            return manager;
                        })
                        .subscribe(circleMembers -> {
                            mRootView.setGroupLengh(groupLengh);
                            mRootView.refreshData();
                        });
            }

            @Override
            protected void onFailure(String message, int code) {
                super.onFailure(message, code);
            }

            @Override
            protected void onException(Throwable throwable) {
                super.onException(throwable);
            }
        });
        addSubscrebe(subscription);
    }

    @Override
    public boolean insertOrUpdateData(@NotNull List<CircleMembers> data, boolean isLoadMore) {
        return false;
    }

    public enum MemberHandleType {
        APPOINT_MANAFER,
        CANCLE_MANAFER,
        CANCLE_MEMBER,
        APPOINT_BLACKLIST,
        CANCLE_BLACKLIST,;
    }

}
