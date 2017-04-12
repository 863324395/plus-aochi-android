package com.zhiyicx.thinksnsplus.data.source.local;

import android.app.Application;

import com.zhiyicx.thinksnsplus.data.beans.DigRankBean;
import com.zhiyicx.thinksnsplus.data.beans.DigBeanDao;
import com.zhiyicx.thinksnsplus.data.source.local.db.CommonCacheImpl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * @Describe 赞列表数据库
 * @Author Jungle68
 * @Date 2017/4/11
 * @Contact master.jungle68@gmail.com
 */

public class DigBeanGreenDaoImpl extends CommonCacheImpl<DigRankBean> {

    @Inject
    public DigBeanGreenDaoImpl(Application context) {
        super(context);
    }

    @Override
    public long saveSingleData(DigRankBean singleData) {
        DigBeanDao digBeanDao = getWDaoSession().getDigBeanDao();
        return digBeanDao.insert(singleData);
    }

    @Override
    public void saveMultiData(List<DigRankBean> multiData) {
        DigBeanDao digBeanDao = getWDaoSession().getDigBeanDao();
        digBeanDao.insertOrReplaceInTx(multiData);
    }

    @Override
    public boolean isInvalide() {
        return false;
    }

    @Override
    public DigRankBean getSingleDataFromCache(Long primaryKey) {
        DigBeanDao digBeanDao = getRDaoSession().getDigBeanDao();
        return digBeanDao.load(primaryKey);
    }

    @Override
    public List<DigRankBean> getMultiDataFromCache() {
        DigBeanDao digBeanDao = getRDaoSession().getDigBeanDao();
        List<DigRankBean> datas = digBeanDao.loadAll();

        Collections.sort(datas, new Comparator<DigRankBean>() {
            @Override
            public int compare(DigRankBean o1, DigRankBean o2) {
                try {
                    return Integer.parseInt(o2.getValue()) - Integer.parseInt(o1.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        return datas;
    }

    @Override
    public void clearTable() {
        DigBeanDao digBeanDao = getWDaoSession().getDigBeanDao();
        digBeanDao.deleteAll();
    }

    @Override
    public void deleteSingleCache(Long primaryKey) {
        DigBeanDao digBeanDao = getWDaoSession().getDigBeanDao();
        digBeanDao.deleteByKey(primaryKey);
    }

    @Override
    public void deleteSingleCache(DigRankBean dta) {
        DigBeanDao digBeanDao = getWDaoSession().getDigBeanDao();
        digBeanDao.delete(dta);
    }

    @Override
    public void updateSingleData(DigRankBean newData) {
        DigBeanDao digBeanDao = getWDaoSession().getDigBeanDao();
        digBeanDao.update(newData);
    }

    @Override
    public long insertOrReplace(DigRankBean newData) {
        DigBeanDao digBeanDao = getWDaoSession().getDigBeanDao();
        return digBeanDao.insertOrReplace(newData);
    }

}
