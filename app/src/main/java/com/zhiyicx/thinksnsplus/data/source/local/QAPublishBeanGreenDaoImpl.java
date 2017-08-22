package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.BaseDraftBean;
import com.zhiyicx.thinksnsplus.data.beans.QAPublishBean;
import com.zhiyicx.thinksnsplus.data.beans.QAPublishBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2017/08/22/10:36
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class QAPublishBeanGreenDaoImpl extends CommonCacheImpl<QAPublishBean> {

    private QAPublishBeanDao mQAPublishBeanDao;

    @Inject
    public QAPublishBeanGreenDaoImpl(Application context) {
        super(context);
        mQAPublishBeanDao = getWDaoSession().getQAPublishBeanDao();
    }

    @Override
    public long saveSingleData(QAPublishBean singleData) {
        return mQAPublishBeanDao.insertOrReplace(singleData);
    }

    @Override
    public void saveMultiData(List<QAPublishBean> multiData) {

    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public QAPublishBean getSingleDataFromCache(Long primaryKey) {
        QAPublishBean draft=mQAPublishBeanDao.queryBuilder().where(QAPublishBeanDao.Properties.Mark.eq
                (primaryKey))
                .build().unique();

        List<QAPublishBean> all=getMultiDataFromCache();

        return draft;
//        return mQAPublishBeanDao.load(primaryKey);
    }

    @Override
    public List<QAPublishBean> getMultiDataFromCache() {
        return mQAPublishBeanDao.loadAll();
    }

    public List<BaseDraftBean> getMultiBasetDraftDataFromCache() {
        List<QAPublishBean> realData = getMultiDataFromCache();
        List<BaseDraftBean> needData = new ArrayList<>();
        if (!realData.isEmpty()) {
            needData.addAll(realData);
        }
        return needData;
    }

    @Override
    public void clearTable() {
        mQAPublishBeanDao.deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        mQAPublishBeanDao.deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(QAPublishBean dta) {
        if (dta!=null){
            mQAPublishBeanDao.delete(dta);
        }
    }

    @Override
    public void updateSingleData(QAPublishBean newData) {
        saveSingleData(newData);
    }

    @Override
    public long insertOrReplace(QAPublishBean newData) {
        return saveSingleData(newData);
    }
}
