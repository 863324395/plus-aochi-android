package com.zhiyicx.thinksnsplus.data.source.remote;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.data.beans.AnswerCommentListBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.ExpertBean;
import com.zhiyicx.thinksnsplus.data.beans.QAAnswerBean;
import com.zhiyicx.thinksnsplus.data.beans.QAPublishBean;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.qa.QATopicBean;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @Author Jliuer
 * @Date 2017/08/14/10:03
 * @Email Jliuer@aliyun.com
 * @Description
 */
public interface QAClient {

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(ApiConfig.APP_PATH_PUBLISH_QUESTIONS)
    Observable<BaseJsonV2<QAPublishBean>> publishQuestion(@Body RequestBody body);

    @FormUrlEncoded
    @POST(ApiConfig.APP_PATH_PUBLISH_ANSWER)
    Observable<BaseJsonV2<QAAnswerBean>> publishAnswer(@Path("question") Long question_id,@Field("body") String body, @Field("anonymity") int anonymity);

    /**
     * @param name   用于搜索话题，传递话题名称关键词。
     * @param after  获取 id 之后的数据，要获取某条话题之后的数据，传递该话题 ID。
     * @param follow 是否检查当前用户是否关注了某话题，默认为不检查，如果传递 follow 切拥有任意值（空除外），都会检查当前用户与话题的关注关系。
     * @param limit  这次请求获取的条数，默认为 20 条，为了避免过大或者错误查询，设置了一个修正值，最大 50 最小 1 。
     */
    @GET(ApiConfig.APP_PATH_GET_ALL_TOPIC)
    Observable<List<QATopicBean>> getQATopic(@Query("name") String name, @Query
            ("after") Long after, @Query("follow") Long follow, @Query("limit") Long limit);

    /**
     * 获取回答评论列表
     * @param answer_id 回答 id
     * @param after
     * @param limit
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_ANSWER_COMMENTS)
    Observable<List<AnswerCommentListBean>> getAnswerCommentList(@Path("answer_id") long answer_id, @Query
            ("after") Long after, @Query("limit") Long limit);

    /**
     *
     * @param type 默认值为 follow 代表用户关注的话题列表，如果值为 expert 则获取该用户的专家话题（哪些话题下是专家）。
     * @param after
     * @param limit
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_FOLLOEW_TOPIC)
    Observable<List<QATopicBean>> getQAFollowTopic(@Query("type") String type, @Query
            ("after") Long after, @Query("limit") Long limit);

    /**
     * @param subject 用于搜索问题，传递话题名称关键词。
     * @param after   获取 id 之后的数据，要获取某条问题之后的数据，传递该问题 ID。
     * @param type    默认值 new, all - 全部、new - 最新、hot - 热门、reward - 悬赏、excellent - 精选 。
     */
    @GET(ApiConfig.APP_PATH_PUBLISH_QUESTIONS)
    Observable<List<QAListInfoBean>> getQAQustion(@Query("subject") String subject, @Query
            ("offset") Long after, @Query("type") String type, @Query("limit") Long limit);

    @GET(ApiConfig.APP_PATH_GET_QUESTION_LIST_BY_TOPIC)
    Observable<List<QAListInfoBean>> getQAQustionByTopic(@Path("topic") String topic_id, @Query("subject") String subject, @Query
            ("offset") Long after, @Query("type") String type, @Query("limit") Long limit);

    @GET(ApiConfig.APP_PATH_GET_TOPIC_EXPERTS)
    Observable<List<ExpertBean>> getTopicExperts(@Path("topic_id") int topic_id, @Query("after") Long after, @Query("limit") Long limit);

    /**
     * 获取话题详情
     *
     * @param topic_id 话题id
     */
    @GET(ApiConfig.APP_PATH_GET_TOPIC_DETAIL)
    Observable<QATopicBean> getTopicDetail(@Path("topic") String topic_id);
}
