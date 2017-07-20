package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.DynamicCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicCommentListBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * @Author Jliuer
 * @Date 2017/07/18/16:57
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class GroupDynamicCommentListBeanGreenDaoImpl extends CommonCacheImpl<GroupDynamicCommentListBean> {

    private GroupDynamicCommentListBeanDao mGroupDynamicCommentListBeanDao;

    @Inject
    public GroupDynamicCommentListBeanGreenDaoImpl(Application context) {
        super(context);
        mGroupDynamicCommentListBeanDao = getWDaoSession().getGroupDynamicCommentListBeanDao();
    }

    @Override
    public long saveSingleData(GroupDynamicCommentListBean singleData) {
        return 0;
    }

    @Override
    public void saveMultiData(List<GroupDynamicCommentListBean> multiData) {
        mGroupDynamicCommentListBeanDao.insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public GroupDynamicCommentListBean getSingleDataFromCache(Long primaryKey) {
        return null;
    }

    @Override
    public List<GroupDynamicCommentListBean> getMultiDataFromCache() {
        return mGroupDynamicCommentListBeanDao.loadAll();
    }

    @Override
    public void clearTable() {
        mGroupDynamicCommentListBeanDao.deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        mGroupDynamicCommentListBeanDao.deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(GroupDynamicCommentListBean dta) {
        mGroupDynamicCommentListBeanDao.delete(dta);
    }

    @Override
    public void updateSingleData(GroupDynamicCommentListBean newData) {

    }

    @Override
    public long insertOrReplace(GroupDynamicCommentListBean newData) {
        return mGroupDynamicCommentListBeanDao.insertOrReplace(newData);
    }

    public void insertOrReplace(List<GroupDynamicCommentListBean> newData) {
        if (newData != null && !newData.isEmpty()){
            mGroupDynamicCommentListBeanDao.insertOrReplaceInTx(newData);
        }
    }

    public List<GroupDynamicCommentListBean> getMySendingComment(long feed_id) {
        if (AppApplication.getmCurrentLoginAuth() == null) {
            return new ArrayList<>();
        }
        return mGroupDynamicCommentListBeanDao.queryBuilder()
                .where(GroupDynamicCommentListBeanDao.Properties.User_id.eq(AppApplication.getmCurrentLoginAuth().getUser_id()),
                        GroupDynamicCommentListBeanDao.Properties.Id.isNull())
                .orderDesc(GroupDynamicCommentListBeanDao.Properties.Created_at)
                .list();
    }

    public List<GroupDynamicCommentListBean> getGroupCommentsByFeedId(long feed_id) {
        if (AppApplication.getmCurrentLoginAuth() == null) {
            return new ArrayList<>();
        }
        return mGroupDynamicCommentListBeanDao.queryBuilder()
                .where(GroupDynamicCommentListBeanDao.Properties.User_id.eq(AppApplication.getmCurrentLoginAuth().getUser_id()),
                        GroupDynamicCommentListBeanDao.Properties.Feed_id.eq(feed_id))
                .orderDesc(GroupDynamicCommentListBeanDao.Properties.Created_at)
                .list();
    }

    public GroupDynamicCommentListBean getGroupCommentsByCommentMark(long comment_mark) {
        List<GroupDynamicCommentListBean> result = mGroupDynamicCommentListBeanDao.queryBuilder()
                .where(GroupDynamicCommentListBeanDao.Properties.User_id.eq(AppApplication.getmCurrentLoginAuth().getUser_id()),
                        GroupDynamicCommentListBeanDao.Properties.Feed_id.eq(comment_mark))
                .orderDesc(GroupDynamicCommentListBeanDao.Properties.Created_at)
                .list();

        if (!result.isEmpty()) {
            return result.get(0);
        }

        return null;
    }

    /**
     * 通过 feedMark 删除评论
     *
     * @param feedMark
     */
    public void deleteCacheByFeedMark(Long feedMark) {
        Observable.from(getLocalComments(feedMark))
                .subscribeOn(Schedulers.io())
                .filter(dynamicCommentBean -> dynamicCommentBean.getId() != null && dynamicCommentBean.getId() != 0).subscribe(new Observer<GroupDynamicCommentListBean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(GroupDynamicCommentListBean dynamicCommentBean) {
                deleteSingleCache(dynamicCommentBean);
            }
        });
    }

    /**
     * 获取最新的动态列表
     */
    public List<GroupDynamicCommentListBean> getLocalComments(Long feedMark) {
        List<GroupDynamicCommentListBean> dynamicCommentBeen = new ArrayList<>();
        dynamicCommentBeen.add(getSingleDataFromCache(feedMark));
        dynamicCommentBeen.addAll(getMySendingComment(feedMark));
        return dynamicCommentBeen;
    }
}
