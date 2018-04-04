package com.zhiyicx.thinksnsplus.modules.shortvideo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.tym.shortvideo.media.VideoInfo;
import com.tym.shortvideo.utils.DateUtil;
import com.tym.shortvideo.utils.TrimVideoUtil;
import com.zhiyicx.thinksnsplus.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * @Author Jliuer
 * @Date 2018/03/28/14:08
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoGridViewAdapter extends CommonAdapter<VideoInfo> {

    BitmapFactory.Options options = new BitmapFactory.Options();


    public VideoGridViewAdapter(Context context, int layoutId, List<VideoInfo> datas) {
        super(context, layoutId, datas);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    @Override
    protected void convert(ViewHolder holder, VideoInfo video, int position) {
        if (TextUtils.isEmpty(video.getPath())) {
            holder.setVisible(R.id.tv_duration, View.GONE);
            holder.setImageResource(R.id.iv_cover, R.mipmap.pic_shootvideo);
        } else {
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), video.storeId, MediaStore.Images.Thumbnails.MINI_KIND,
                    options);
            int w = holder.getImageViwe(R.id.iv_cover).getWidth();
            if (w != 0) {
                bitmap = Bitmap.createScaledBitmap(bitmap, w, w, true);
            }
            holder.setVisible(R.id.tv_duration, View.VISIBLE);
            holder.setText(R.id.tv_duration, DateUtil.convertSecondsToTime(video.getDuration() / 1000));
            holder.getImageViwe(R.id.iv_cover).setImageBitmap(bitmap);
        }


    }
}
