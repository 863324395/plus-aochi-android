package com.zhiyicx.thinksnsplus.modules.music_fm.paided_music;

import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;

public class MyMusicActivity extends TSActivity {

    @Override
    protected Fragment getFragment() {
        return MyMusicFragmentContainer.getInstance();
    }

    @Override
    protected void componentInject() {

    }
}
