package com.zhiyicx.imsdk.db.dao.soupport;

import com.zhiyicx.imsdk.entity.Conversation;

import java.util.List;

/**
 * Created by jungle on 16/8/15.
 * com.zhiyicx.imsdk.db.dao.soupport
 * zhibo_android
 * email:335891510@qq.com
 */
public interface ConversationDaoSoupport {

    /**
     * 插入对话
     *
     * @param conversation
     * @return
     */
    long insertConversation(Conversation conversation);

    /**
     * 通过cid获取对话信息
     *
     * @param cid
     * @return
     */
    Conversation getConversationByCid(int cid);

    /**
     * 获取对话列表
     *
     * @param page
     * @return
     */
    List<Conversation> getConversationList(int page);

    /**
     * 获取对话列表
     *
     * @param im_uid 聊天 id
     * @return
     */
    List<Conversation> getConversationListbyImUid(long im_uid);


    /**
     * 删除对话信息
     *
     * @param cid
     * @param type
     * @return
     */
    boolean delConversation(int cid, int type);

    /**
     * 对话是否存在
     *
     * @param cid
     * @return
     */
    boolean hasConversation(int cid);

    /**
     * 更新对话
     *
     * @param conversation
     * @return
     */
    boolean updateConversation(Conversation conversation);

    /**
     * 插入或者更新对话
     *
     * @param conversation
     * @return
     */
    boolean insertOrUpdateConversation(Conversation conversation);

}
