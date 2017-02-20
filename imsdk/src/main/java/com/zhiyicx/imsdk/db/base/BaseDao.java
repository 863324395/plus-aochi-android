package com.zhiyicx.imsdk.db.base;

import android.content.Context;

/**
 * Created by jungle on 16/8/12.
 * com.zhiyicx.imsdk.db.base
 * zhibo_android
 * email:335891510@qq.com
 */
public abstract class BaseDao {
    private final String TAG = getClass().getSimpleName();

    protected static final int VERSION = 4;
    protected static final String DB_NAME = "zycxMessage.db";

    public static final long TIME_DEFAULT_ADD = 1451577600000L - 496000; //  消息的MID，`(mid >> 23) + 1451577600000` 为毫秒时间戳
    protected final int DEFAULT_PAGEE = 1;
    protected final int DEFAULT_PAGESIZE = 20;

    protected Context context;
    protected ZBSqlHelper mHelper;

    public abstract void close();
}
