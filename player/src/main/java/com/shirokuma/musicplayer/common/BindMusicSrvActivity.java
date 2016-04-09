package com.shirokuma.musicplayer.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.*;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import com.shiro.tools.view.ProgressDialogWrapper;
import com.shirokuma.musicplayer.PlayerEnv;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.playback.MusicBroadcast;
import com.shirokuma.musicplayer.playback.MusicService;

// bind service and create options menu
public abstract class BindMusicSrvActivity extends BaseActivity implements MediaScannerConnection.OnScanCompletedListener {
    protected MusicService mMusicSrv;
    protected ProgressDialog mProgress;
    protected ProgressDialogWrapper progressWrapper;

    @Override
    protected void initData() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicBroadcast.MUSIC_BROADCAST_ACTION_PLAYBACK);
        registerReceiver(mMusicBroadcastReceiver, filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PlayerEnv.addBoundActivity(this);
    }

    @Override
    protected void initView() {
        // media control
        // wait for service bound
        if (mMusicSrv == null)
            mProgress = ProgressDialog.show(this, "", getString(R.string.loading));
        progressWrapper = new ProgressDialogWrapper(getContext());
    }

    private Activity getContext() {
        return this;
    }

    //connect to the service
    protected ServiceConnection mMusicSrvConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            mMusicSrv = binder.getService();
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
            onMusicSrvConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicSrv = null;
            onMusicSrvDisconnected();
        }
    };

    protected void onMusicSrvConnected() {
    }

    protected void onMusicSrvDisconnected() {
    }

    //start and bind the service when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MusicService.class), mMusicSrvConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mMusicBroadcastReceiver);
        PlayerEnv.removeBoundActivity(this);
        if (mMusicSrv != null) {
            unbindService(mMusicSrvConn);
            mMusicSrv = null;
        }
        // if there is no more bound activities, the service will stop(call it's ondestroy method)
        stopService(new Intent(this, MusicService.class));
        super.onDestroy();
    }


    @Override
    public void onScanCompleted(String path, Uri uri) {
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    MusicBroadcast mMusicBroadcastReceiver = new MusicBroadcast() {
        @Override
        protected void onReceivePlayback(Playback action) {
            switch (action) {
                case Previous:
                    onMusicPrev();
                    break;
                case Next:
                    onMusicNext();
                    break;
                case Play:
                    onMusicPlay();
                    break;
                case Pause:
                    onMusicPause();
                    break;
                case Rewind:
                    onMusicSeek();
                    break;
                case FastForward:
                    onMusicSeek();
                    break;
            }
        }
    };

    protected void onMusicPrev() {
    }

    protected void onMusicNext() {
    }

    protected void onMusicPlay() {
    }

    protected void onMusicPause() {
    }

    protected void onMusicSeek() {
    }

    public MusicService getMusicSrv() {
        return mMusicSrv;
    }
}
