package com.zhiyicx.thinksnsplus.modules.music_fm.paided_music;

import android.content.ComponentName;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.music_fm.music_play.MusicPlayService;

public class MyMusicActivity extends TSActivity {

    private MediaBrowserCompat mMediaBrowserCompat;

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        connectToSession(mMediaBrowserCompat.getSessionToken());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

    public final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {

                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {

                }
            };

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(
                this, token);
        setSupportMediaController(mediaController);
        mediaController.registerCallback(mMediaControllerCallback);
    }

    @Override
    protected Fragment getFragment() {
        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this,
                MusicPlayService.class), mConnectionCallback, null);
        return MyMusicFragmentContainer.getInstance();
    }

    @Override
    protected void componentInject() {

    }
}
