package com.zhiyicx.thinksnsplus.data.source.repository;

import com.zhiyicx.thinksnsplus.data.source.remote.ServiceManager;
import com.zhiyicx.thinksnsplus.modules.q_a.publish.question.PublishQuestionContract;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe
 * @date 2017/7/24
 * @contact email:648129313@qq.com
 */

public class PublishPublishQuestionRepository extends BasePublishQuestionRepository implements PublishQuestionContract.Repository {

    @Inject
    public PublishPublishQuestionRepository(ServiceManager manager) {
        super(manager);
    }
}