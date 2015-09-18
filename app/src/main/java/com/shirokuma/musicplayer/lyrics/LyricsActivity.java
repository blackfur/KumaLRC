package com.shirokuma.musicplayer.lyrics;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BindSrvOpMenusActivity;

public class LyricsActivity extends BindSrvOpMenusActivity {
    private LyricView mLrcView;
    View mBtnBack;
    private ImageButton mBtnPlay, mBtnStop, mBtnPause, mBtnPre, mBtnNext, mBtnRewind, mBtnFastFroward;

    @Override
    protected void initData() {
        super.initData();
    }

    protected View.OnClickListener mBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play:
                    mMusicSrv.play();
                    mBtnPlay.setVisibility(View.GONE);
                    mBtnPause.setVisibility(View.VISIBLE);
                    break;
                case R.id.next:
                    mMusicSrv.playNext();
                    mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
                    break;
                case R.id.previous:
                    mMusicSrv.playPrev();
                    mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
                    break;
                case R.id.stop:
                    mLrcView.stop();
                    mMusicSrv.stop();
                    mBtnPlay.setVisibility(View.VISIBLE);
                    mBtnPause.setVisibility(View.GONE);
                    break;
                case R.id.pause:
                    mMusicSrv.pause();
                    mBtnPlay.setVisibility(View.VISIBLE);
                    mBtnPause.setVisibility(View.GONE);
                    break;
                case R.id.rewind:
                    mMusicSrv.rewind();
                    break;
                case R.id.fastforward:
                    mMusicSrv.fastForward();
                    break;
                case R.id.zoom_in:
                    mLrcView.zoomIn();
                    break;
                case R.id.zoom_out:
                    mLrcView.zoomOut();
                    break;
                case R.id.btn_back:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void initView() {
        super.initView();
        mBtnFastFroward = (ImageButton) findViewById(R.id.fastforward);
        mBtnNext = (ImageButton) findViewById(R.id.next);
        mBtnPlay = (ImageButton) findViewById(R.id.play);
        mBtnPause = (ImageButton) findViewById(R.id.pause);
        mBtnStop = (ImageButton) findViewById(R.id.stop);
        mBtnPre = (ImageButton) findViewById(R.id.previous);
        mBtnRewind = (ImageButton) findViewById(R.id.rewind);
        mBtnNext.setOnClickListener(mBtnListener);
        mBtnPlay.setOnClickListener(mBtnListener);
        mBtnPause.setOnClickListener(mBtnListener);
        mBtnStop.setOnClickListener(mBtnListener);
        mBtnPre.setOnClickListener(mBtnListener);
        mBtnFastFroward.setOnClickListener(mBtnListener);
        mBtnRewind.setOnClickListener(mBtnListener);
        mBtnBack = findViewById(R.id.btn_back);
        mLrcView = (LyricView) findViewById(R.id.lrc);
        mBtnBack.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtnBack.setVisibility(View.VISIBLE);
                mBtnBack.setOnClickListener(mBtnListener);
            }
        }, 200);
    }

    @Override
    protected int setContentViewRes() {
        return R.layout.activity_lyrics;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mLrcView.release();
        super.onDestroy();
    }

    @Override
    protected void onMusicSrvConnected() {
        if (mMusicSrv.isPlaying()) {
            mBtnPlay.setVisibility(View.GONE);
            mBtnPause.setVisibility(View.VISIBLE);
        }
        if (mMusicSrv.getCurrentSong() != null)
            mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
    }

    @Override
    protected void onMusicSrvDisconnected() {
        mLrcView.release();
    }

    @Override
    protected void onMusicNext() {
        if (mMusicSrv != null && mLrcView != null && mMusicSrv.getCurrentSong() != null)
            mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
    }

    @Override
    protected void onMusicPlay() {
        if (mMusicSrv != null && mLrcView != null)
            mLrcView.start();
    }

    @Override
    protected void onMusicPause() {
        if (mLrcView != null)
            mLrcView.stop();
    }

    @Override
    protected void onMusicSeek() {
        mLrcView.refresh();
    }
}
