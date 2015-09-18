package com.shirokuma.musicplayer.common;

import android.app.ProgressDialog;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import com.shirokuma.musicplayer.KumalrcApplication;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.Setting.TimerActivity;
import com.shirokuma.musicplayer.playback.MusicBroadcast;
import com.shirokuma.musicplayer.playback.MusicService;

public abstract class BindSrvOpMenusActivity extends BaseActivity {
    protected MusicService mMusicSrv;
    private ProgressDialog mProgress;

    @Override
    protected void initData() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicBroadcast.MUSIC_BROADCAST_ACTION_PLAYBACK);
        registerReceiver(mMusicBroadcastReceiver, filter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((KumalrcApplication) getApplication()).addBoundActivity(this);
    }

    @Override
    protected void initView() {
        // media control
        // wait for service bound
        if (mMusicSrv == null)
            mProgress = ProgressDialog.show(this, "", getString(R.string.loading));
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
        ((KumalrcApplication) getApplication()).removeBoundActivity(this);
        if (mMusicSrv != null) {
            unbindService(mMusicSrvConn);
            mMusicSrv = null;
        }
        // if there is no more bound activities, the service will stop(call it's ondestroy method)
        stopService(new Intent(this, MusicService.class));
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Utils.setOptionMenuIconEnable(menu, true);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                mMusicSrv.setShuffle();
                break;
            case R.id.action_end:
                ((KumalrcApplication) getApplication()).exit();
                break;
            case R.id.action_timer:
                startActivity(new Intent(this, TimerActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
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