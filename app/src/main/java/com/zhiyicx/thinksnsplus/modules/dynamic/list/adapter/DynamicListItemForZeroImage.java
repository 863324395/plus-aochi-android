package com.zhiyicx.thinksnsplus.modules.dynamic.list.adapter;

import android.content.Context;

import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;

import static com.zhiyicx.thinksnsplus.data.beans.DynamicListAdvert.DEFAULT_ADVERT_FROM_TAG;

/**
 * @author LiuChao
 * @describe
 * @date 2017/3/10
 * @contact email:450127106@qq.com
 */

public class DynamicListItemForZeroImage extends DynamicListBaseItem {
    public DynamicListItemForZeroImage(Context context) {
        super(context);
    }


    @Override
    public boolean isForViewType(DynamicDetailBeanV2 item, int position) {
        return item.getFeed_mark() != null&& item.getFeed_from() != DEFAULT_ADVERT_FROM_TAG && (item.getImages() == null || item.getImages().isEmpty()) && item.getVideo() == null;
    }

}
