package com.zhiyicx.thinksnsplus.modules.photopicker;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhiyicx.baseproject.base.TSActivity;

import java.util.List;

import me.iwf.photopicker.fragment.ImagePagerFragment;

import static com.zhiyicx.thinksnsplus.modules.photopicker.PhotoAlbumDetailsFragment.EXTRA_MAX_COUNT;
import static com.zhiyicx.thinksnsplus.modules.photopicker.PhotoAlbumDetailsFragment.EXTRA_VIEW_ALL_PHOTOS;
import static com.zhiyicx.thinksnsplus.modules.photopicker.PhotoAlbumDetailsFragment.EXTRA_VIEW_HEIGHT;
import static com.zhiyicx.thinksnsplus.modules.photopicker.PhotoAlbumDetailsFragment.EXTRA_VIEW_INDEX;
import static com.zhiyicx.thinksnsplus.modules.photopicker.PhotoAlbumDetailsFragment.EXTRA_VIEW_LOCATION;
import static com.zhiyicx.thinksnsplus.modules.photopicker.PhotoAlbumDetailsFragment.EXTRA_VIEW_SELECTED_PHOTOS;
import static com.zhiyicx.thinksnsplus.modules.photopicker.PhotoAlbumDetailsFragment.EXTRA_VIEW_WIDTH;

public class PhotoViewActivity extends TSActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void componentInject() {

    }

    @Override
    protected Fragment getFragment() {
        Bundle bundle = getIntent().getExtras();
        List<String> allPhotos = bundle.getStringArrayList(EXTRA_VIEW_ALL_PHOTOS);
        List<String> selectedPhotos = bundle.getStringArrayList(EXTRA_VIEW_SELECTED_PHOTOS);
        int index = bundle.getInt(EXTRA_VIEW_INDEX);
        int[] screenLocation = bundle.getIntArray(EXTRA_VIEW_LOCATION);
        int width = bundle.getInt(EXTRA_VIEW_WIDTH);
        int height = bundle.getInt(EXTRA_VIEW_HEIGHT);
        int maxCount = bundle.getInt(EXTRA_MAX_COUNT);
        PhotoViewFragment imagePagerFragment =
                PhotoViewFragment.newInstance(selectedPhotos, allPhotos, index, maxCount);
        return imagePagerFragment;
    }
}
