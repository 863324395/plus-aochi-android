package com.zhiyicx.thinksnsplus.data.beans;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicListBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicListBeanDao;

public class GroupDynamicListBeanTest extends AbstractDaoTestLongPk<GroupDynamicListBeanDao, GroupDynamicListBean> {

    public GroupDynamicListBeanTest() {
        super(GroupDynamicListBeanDao.class);
    }

    @Override
    protected GroupDynamicListBean createEntity(Long key) {
        GroupDynamicListBean entity = new GroupDynamicListBean();
        entity.setId(key);
        entity.setGroup_id(1);
        entity.setViews(1);
        entity.setDiggs(1);
        entity.setCollections(1);
        entity.setComments(1);
        entity.setIs_audit(1);
        return entity;
    }

}
