package com.zhiyicx.thinksnsplus.data.source.remote;

import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.common.base.BaseJson;
import com.zhiyicx.common.base.BaseJsonV2;
import com.zhiyicx.thinksnsplus.data.beans.CheckInBean;
import com.zhiyicx.thinksnsplus.data.beans.CommentedBean;
import com.zhiyicx.thinksnsplus.data.beans.DigRankBean;
import com.zhiyicx.thinksnsplus.data.beans.DigedBean;
import com.zhiyicx.thinksnsplus.data.beans.FlushMessages;
import com.zhiyicx.thinksnsplus.data.beans.FollowFansBean;
import com.zhiyicx.thinksnsplus.data.beans.IMBean;
import com.zhiyicx.thinksnsplus.data.beans.NearbyBean;
import com.zhiyicx.thinksnsplus.data.beans.TSPNotificationBean;
import com.zhiyicx.thinksnsplus.data.beans.UserCertificationInfo;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.UserTagBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_CHANGE_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_CHECK_IN;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_DYNAMIC_REWARDS;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_BATCH_SPECIFIED_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_BY_PHONE_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_CHECK_IN_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_CHECK_IN_RANKS;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_CURRENT_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_HOT_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_IM_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_NEW_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_RECOMMENT_BY_TAG_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_SPECIFIED_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_USER_AROUND;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_GET_USER_INFO;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_REWARD_USER;
import static com.zhiyicx.baseproject.config.ApiConfig.APP_PATH_UPDATE_USER_LOCATION;

/**
 * @author LiuChao
 * @describe 用户信息相关的网络请求
 * @date 2017/1/9
 * @contact email:450127106@qq.com
 */

public interface UserInfoClient {
    // 上传头像功能，写在CommonClient中

    /**
     * 修改用户资料
     *
     * @param userFieldMap 用户需要修改哪那些信息不确定
     */
    @FormUrlEncoded
    @PATCH(APP_PATH_CHANGE_USER_INFO)
    Observable<Object> changeUserInfo(@FieldMap HashMap<String, Object> userFieldMap);


    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    @GET(APP_PATH_GET_CURRENT_USER_INFO)
    Observable<UserInfoBean> getCurrentLoginUserInfo();

    /**
     * 获取指定用户信息  其中 following、follower 是可选参数，验证用户我是否关注以及是否关注我的用户 id ，默认为当前登陆用户。
     *
     * @param userId          the specified user id
     * @param followingUserId following user id
     * @param followerUserId  follow user id
     * @return
     */
    @GET(APP_PATH_GET_SPECIFIED_USER_INFO)
    Observable<UserInfoBean> getSpecifiedUserInfo(@Path("user_id") long userId, @Query("following") Long followingUserId, @Query("follower") Long followerUserId);

    /**
     * 批量获取用户信息
     *
     * @param user_ids Get multiple designated users, multiple IDs using , split.
     * @param name     Used to retrieve users whose username contains name.
     * @param since    The integer ID of the last User that you've seen.
     * @param order    Sorting. Enum: asc, desc
     * @param limit    List user limit, minimum 1 max 50.
     * @return
     */
    @GET(APP_PATH_GET_BATCH_SPECIFIED_USER_INFO)
    Observable<List<UserInfoBean>> getBatchSpecifiedUserInfo(@Query("id") String user_ids, @Query("name") String name, @Query("since") Integer since, @Query("order") String order, @Query("limit") Integer limit);

    /**
     * 获取 IM 信息
     *
     * @return
     */
    @GET(APP_PATH_GET_IM_INFO)
    Observable<BaseJson<IMBean>> getIMInfo();

    /**
     * 获取用户关注状态
     *
     * @param user_ids 多个用户 id 通过“ ，”来隔开
     */
    @GET(ApiConfig.APP_PATH_GET_USER_FOLLOW_STATE)
    Observable<BaseJson<List<FollowFansBean>>> getUserFollowState(@Query("user_ids") String user_ids);


    /**
     * 用户点赞排行
     *
     * @param page  页码 默认为 1
     * @param limit 返回数据条数 默认15条
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_DIGGS_RANK)
    Observable<BaseJson<List<DigRankBean>>> getDigRankList(@Query("page") int page,
                                                           @Query("limit") int limit);


    /**
     * 获取用户收到的点赞
     *
     * @param after 用来翻页数据体记录 id
     * @param limit 返回数据条数 默认 20 条
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_MY_DIGGS)
    Observable<List<DigedBean>> getMyDiggs(@Query("after") int after,
                                           @Query("limit") int limit);

    /**
     * 获取用户收到的评论
     *
     * @param after 用来翻页数据体记录 id
     * @param limit 返回数据条数 默认 20 条
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_MY_COMMENTS)
    Observable<List<CommentedBean>> getMyComments(@Query("after") int after,
                                                  @Query("limit") int limit);

    /**
     * 获取用户收到的评论
     *
     * @param time 零时区的秒级时间戳
     * @param key  查询关键字 默认查询全部 多个以逗号隔开 可选参数有 diggs comments follows
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_MY_FLUSHMESSAGES)
    Observable<BaseJson<List<FlushMessages>>> getMyFlushMessages(@Query("time") long time,
                                                                 @Query("key") String key);

    /**
     * 未读通知数量检查
     *
     * @return
     * @see {https://github.com/slimkit/thinksns-plus/blob/master/docs/api/v2/notifications.md#%E9%80%9A%E7%9F%A5%E5%88%97%E8%A1%A8}
     */
    @HEAD(ApiConfig.APP_PATH_GET_CKECK_UNREAD_NOTIFICATION)
    Observable<Void> ckeckUnreadNotification();

    /**
     * 通知列表
     *
     * @param notification 检索具体通知，可以是由 , 拼接的 IDs 组，也可以是 Array
     * @param type         获取通知类型，可选 all,read,unread 默认 all
     * @param limit        获取条数，默认 20
     * @param offset       数据偏移量，默认 0
     * @return
     * @see {https://github.com/slimkit/thinksns-plus/blob/master/docs/api/v2/notifications.md#%E9%80%9A%E7%9F%A5%E5%88%97%E8%A1%A8}
     */
    @GET(ApiConfig.APP_PATH_GET_NOTIFICATION_LIST)
    Observable<List<TSPNotificationBean>> getNotificationList(@Query("notification") String notification, @Query("type") String type, @Query("limit") Integer limit, @Query("offset") Integer offset);

    /**
     * 读取通知
     *
     * @param notificationId
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_NOTIFICATION_DETIAL)
    Observable<TSPNotificationBean> getNotificationDetail(@Path("notification") String notificationId);

    /**
     * 标记通知阅读
     *
     * @param notificationId 通知ID，可以是由 , 拼接的 IDs 组，也可以是 Array
     * @return
     */
    @PATCH(ApiConfig.APP_PATH_MAKE_NOTIFICAITON_READED)
    Observable<Object> makeNotificationReaded(@Query("notification") String notificationId);

    /**
     * 更新用户头像
     *
     * @param multipartBody
     * @return
     */
    @POST(ApiConfig.APP_PATH_UPDATE_USER_AVATAR)
    Observable<Object> updateAvatar(@Body MultipartBody multipartBody);

    /**
     * 更新用户背景
     *
     * @param multipartBody
     * @return
     */
    @POST(ApiConfig.APP_PATH_UPDATE_USER_BG)
    Observable<Object> updateBg(@Body MultipartBody multipartBody);

    /*******************************************  标签  *********************************************/


    /**
     * 获取一个用户的标签
     *
     * @param user_id
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_USER_TAGS)
    Observable<List<UserTagBean>> getUserTags(@Path("user_id") long user_id);

    /**
     * 获取当前认证用户的标签
     *
     * @return
     */
    @GET(ApiConfig.APP_PATH_GET_CURRENT_USER_TAGS)
    Observable<List<UserTagBean>> getCurrentUserTags();

    /**
     * 当前认证用户附加一个标签
     *
     * @param tag_id
     * @return
     */
    @PUT(ApiConfig.APP_PATH_CURRENT_USER_ADD_TAGS)
    Observable<Object> addTag(@Path("tag_id") long tag_id);

    /**
     * 当前认证用户分离一个标签
     *
     * @param tag_id
     * @return
     */
    @DELETE(ApiConfig.APP_PATH_CURRENT_USER_DELETE_TAGS)
    Observable<Object> deleteTag(@Path("tag_id") long tag_id);

    /*******************************************  认证  *********************************************/

    /**
     * 获取用户认证信息
     */
    @GET(ApiConfig.APP_PATH_CERTIFICATION)
    Observable<UserCertificationInfo> getUserCertificationInfo();

    /**
     * 提交认证信息
     */
    @POST(ApiConfig.APP_PATH_CERTIFICATION)
    Observable<BaseJsonV2<Object>> sendUserCertificationInfo(@Body RequestBody requestBody);

    /**
     * 更新认证信息
     */
    @PATCH(ApiConfig.APP_PATH_CERTIFICATION)
    Observable<BaseJsonV2<Object>> updateUserCertificationInfo();


    /*******************************************  打赏  *********************************************/

    /**
     * 打赏一个用户
     *
     * @param user_id target user
     * @param amount  reward amount 真实货币的分单位
     * @return
     */
    @FormUrlEncoded
    @POST(APP_PATH_REWARD_USER)
    Observable<Object> rewardUser(@Path("user_id") long user_id, @Field("amount") Integer amount);

    /*******************************************  找人  *********************************************/

    /**
     * 热门用户
     *
     * @param limit  每页数量
     * @param offset 偏移量, 注: 此参数为之前获取数量的总和
     * @return
     */
    @GET(APP_PATH_GET_HOT_USER_INFO)
    Observable<List<UserInfoBean>> getHotUsers(@Query("limit") Integer limit, @Query("offset") Integer offset);

    /**
     * 最新用户
     *
     * @param limit  每页数量
     * @param offset 偏移量, 注: 此参数为之前获取数量的总和
     * @return
     */
    @GET(APP_PATH_GET_NEW_USER_INFO)
    Observable<List<UserInfoBean>> getNewUsers(@Query("limit") Integer limit, @Query("offset") Integer offset);

    /**
     * tag 推荐用户
     *
     * @param limit  每页数量
     * @param offset 偏移量, 注: 此参数为之前获取数量的总和
     * @return
     */
    @GET(APP_PATH_GET_RECOMMENT_BY_TAG_USER_INFO)
    Observable<List<UserInfoBean>> getUsersRecommentByTag(@Query("limit") Integer limit, @Query("offset") Integer offset);

    /**
     * phone 推荐用户
     * <p>
     * { "phones": [ 18877778888, 18999998888, 17700001111 ] }
     *
     * @return
     */
    @POST(APP_PATH_GET_BY_PHONE_USER_INFO)
    Observable<List<UserInfoBean>> getUsersByPhone(@Body RequestBody phones);

    /**
     * 更新位置数据
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return
     */
    @FormUrlEncoded
    @POST(APP_PATH_UPDATE_USER_LOCATION)
    Observable<Object> updateUserLocation(@Field("longitude") double longitude, @Field("latitude") double latitude);

    /**
     * 根据经纬度查询周围最多 50KM 内的 TS+ 用户
     *
     * @param longitude 当前用户所在位置的纬度
     * @param latitude  当前用户所在位置的经度
     * @param radius    搜索范围，米为单位 [0 - 50000], 默认3000
     * @param limit     默认20， 最大100
     * @param page      分页参数， 默认1，当返回数据小于limit， page达到最大值
     * @return
     */
    @GET(APP_PATH_GET_USER_AROUND)
    Observable<List<NearbyBean>> getNearbyData(@Query("longitude") double longitude, @Query("latitude") double latitude, @Query("radius") Integer radius, @Query("limit") Integer limit, @Query("page") Integer page);

    /*******************************************  签到  *********************************************/

    /**
     * 获取签到信息
     *
     * @return
     */
    @GET(APP_PATH_GET_CHECK_IN_INFO)
    Observable<CheckInBean> getCheckInInfo();

    /**
     * 签到
     *
     * @return
     */
    @PUT(APP_PATH_CHECK_IN)
    Observable<Object> checkIn();

    /**
     * 连续签到排行榜
     *
     * @param offset 数据偏移数，默认为 0。
     * @return
     */
    @GET(APP_PATH_GET_CHECK_IN_RANKS)
    Observable<List<UserInfoBean>> getCheckInRanks(@Query("offset") Integer offset);

}
