package com.zhiyicx.thinksnsplus.data.source.repository;

import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.topic.TopicDetailContract;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/8/14
 * @contact email:648129313@qq.com
 */

public class TopicDetailRepository implements TopicDetailContract.Repository{

    @Inject
    public TopicDetailRepository(ServiceManager manager) {
    }
}
