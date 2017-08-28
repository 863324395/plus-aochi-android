package com.zhiyicx.thinksnsplus.data.source.repository;

import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailContract;

import javax.inject.Inject;

/**
 * @author LiuChao
 * @describe
 * @date 2017/2/27
 * @contact email:450127106@qq.com
 */

public class DynamicDetailRepository extends BaseDynamicRepository implements
        DynamicDetailContract.Repository {

    @Inject
    public DynamicDetailRepository(ServiceManager serviceManager) {
        super(serviceManager);
    }


}
