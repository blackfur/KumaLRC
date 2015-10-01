package com.shirokuma.musicplayer.common;

import android.app.ProgressDialog;
import android.content.*;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import com.shirokuma.musicplayer.KumalrcApplication;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.setting.MediaSetting;
import com.shirokuma.musicplayer.setting.SettingActivity;
import com.shirokuma.musicplayer.setting.TimerActivity;
import com.shirokuma.musicplayer.playback.MusicBroadcast;
import com.shirokuma.musicplayer.playback.MusicService;
import com.tencent.mm.sdk.openapi.IWXAPI;

// bind service and create options menu
public abstract class BindSrvOpMenusActivity extends BaseActivity implements MediaScannerConnection.OnScanCompletedListener {
    protected MusicService mMusicSrv;
    protected ProgressDialog mProgress;
    private IWXAPI api;

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean b = MediaSetting.getInstance(this).getShuffle();
        menu.findItem(R.id.action_in_order).setVisible(b);
        menu.findItem(R.id.action_shuffle).setVisible(!b);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.action_scan:
                if (mMusicSrv != null && mMusicSrv.isPlaying())
                    mMusicSrv.stop();
                MediaScannerConnection.scanFile(this, new String[]{Environment
                        .getExternalStorageDirectory().getAbsolutePath()}, null, this);
                mProgress = ProgressDialog.show(this, "", getString(R.string.scanning));
                break;
            case R.id.action_in_order:
                mMusicSrv.setShuffle(false);
                MediaSetting.getInstance(this).setShuffle(false);
                break;
            case R.id.action_shuffle:
                mMusicSrv.setShuffle(true);
                MediaSetting.getInstance(this).setShuffle(true);
                break;
            case R.id.action_end:
                ((KumalrcApplication) getApplication()).exit();
                break;
            case R.id.action_timer:
                startActivity(new Intent(this, TimerActivity.class));
                break;
            case R.id.action_setting:
                if (mMusicSrv != null && mMusicSrv.isPlaying())
                    mMusicSrv.stop();
                startActivity(new Intent(this, SettingActivity.class));
                break;
	    case R.id.action_share:
		api = WXAPIFactory.createWXAPI(this, Utils.WEBCHAT_APPID, false);
		break;		
        }
        return super.onOptionsItemSelected(item);
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
