package com.zhiyicx.thinksnsplus.data.source.repository;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.source.local.UserInfoBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.remote.EasemobClient;
import com.zhiyicx.thinksnsplus.data.source.remote.FollowFansClient;
import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.modules.home.mine.friends.IBaseFriendsRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Catherine
 * @describe
 * @date 2017/12/25
 * @contact email:648129313@qq.com
 */

public class BaseFriendsRepository implements IBaseFriendsRepository{

    FollowFansClient mClient;
    EasemobClient mEasemobClient;
    @Inject
    UpLoadRepository mUpLoadRepository;
    @Inject
    protected UserInfoBeanGreenDaoImpl mUserInfoBeanGreenDao;

    @Inject
    public BaseFriendsRepository(ServiceManager manager) {
        mClient = manager.getFollowFansClient();
        mEasemobClient = manager.getEasemobClient();
    }

    @Override
    public Observable<List<UserInfoBean>> getUserFriendsList(long maxId, String keyword) {
        return mClient.getUserFriendsList(maxId, TSListFragment.DEFAULT_PAGE_SIZE, keyword)
                .observeOn(Schedulers.io())
                .map(userInfoBeen -> {
                    // 保存用户信息
                    mUserInfoBeanGreenDao.insertOrReplace(userInfoBeen);
                    return userInfoBeen;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ChatGroupBean> createGroup(String groupName, String groupIntro, boolean isPublic,
                                                 int maxUser, boolean isMemberOnly, boolean isAllowInvites,
                                                 long owner, String members) {
        return mEasemobClient.createGroup(groupName, groupIntro, isPublic ? 1 : 0, maxUser, isMemberOnly, isAllowInvites ? 1 : 0, owner, members)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ChatGroupBean> updateGroup(String imGroupId, String groupName, String groupIntro, int isPublic,
                                                 int maxUser, boolean isMemberOnly, int isAllowInvites, String groupFace, boolean isEditGroupFace) {
        // 如果是修改头像才去上传图片
        if (isEditGroupFace) {
            return mUpLoadRepository.upLoadSingleFileV2(groupFace, "", true, 0, 0)
                    .flatMap(integerBaseJson -> mEasemobClient.updateGroup(imGroupId, groupName, groupIntro, isPublic, maxUser, isMemberOnly, isAllowInvites, String.valueOf(integerBaseJson.getData()))
                            .flatMap(chatGroupBean -> Observable.just(chatGroupBean)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())));
        } else {
            return mEasemobClient.updateGroup(imGroupId, groupName, groupIntro, isPublic, maxUser, isMemberOnly, isAllowInvites, "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

    }
}
