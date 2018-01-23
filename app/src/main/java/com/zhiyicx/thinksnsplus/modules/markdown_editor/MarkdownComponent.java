package com.zhiyicx.thinksnsplus.modules.markdown_editor;

import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.base.AppComponent;
import com.zhiyicx.thinksnsplus.base.InjectComponent;
import com.zhiyicx.thinksnsplus.modules.circle.publish.PublishPostActivity;
import com.zhiyicx.thinksnsplus.modules.information.publish.detail.EditeInfoDetailActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.answer.news.EditeAnswerDetailActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.publish.detail.EditeQuestionDetailActivity;

import dagger.Component;

/**
 * @Author Jliuer
 * @Date 2017/11/17/17:35
 * @Email Jliuer@aliyun.com
 * @Description
 */
@FragmentScoped
@Component(dependencies = AppComponent.class, modules = MarkdownPresenterModule.class)
public interface MarkdownComponent extends InjectComponent<PublishPostActivity> {
    void inject(EditeInfoDetailActivity infoActivity);

    void inject(EditeQuestionDetailActivity questionActivity);

    void inject(EditeAnswerDetailActivity answerActivity);
}
