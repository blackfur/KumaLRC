package com.shirokuma.musicplayer.lyrics;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BindSrvOpMenusActivity;
import com.shirokuma.musicplayer.common.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class LyricsActivity extends BindSrvOpMenusActivity {
    private LyricView mLrcView;
    View mBtnBack;
    private ImageButton mBtnPlay, mBtnStop, mBtnPause, mBtnPre, mBtnNext, mBtnRewind, mBtnFastFroward;
    SeekBar mSeek;

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
                    break;
                case R.id.previous:
                    mMusicSrv.playPrev();
                    break;
                case R.id.stop:
                    stopSeek();
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
    SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mMusicSrv != null && mMusicSrv.isPlaying() && fromUser) {
                mMusicSrv.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @Override
    protected void initView() {
        super.initView();
        mSeek = (SeekBar) findViewById(R.id.seekbar);
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
                mSeek.setOnSeekBarChangeListener(mSeekListener);
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
        stopSeek();
        super.onDestroy();
    }

    TimerTask mSeekTask;
    Timer mTimer;

    private void restartSeek() {
        // delay to wait for player preparing
        mSeek.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMusicSrv.isPlaying()) {
                    mSeek.setMax(mMusicSrv.getPlayer().getDuration());
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer.purge();
                    }
                    mTimer = new Timer();
                    if (mSeekTask != null) {
                        mSeekTask.cancel();
                    }
                    mSeekTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (mMusicSrv != null) {
                                mSeek.setProgress(mMusicSrv.getCurrentPosition());
                            }
                        }
                    };
                    mTimer.schedule(mSeekTask, 0, Utils.SEEK_INTERVAL);
                }
            }
        }, 200);
    }

    private void stopSeek() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (mSeekTask != null) {
            mSeekTask.cancel();
            mSeekTask = null;
        }
    }

    @Override
    protected void onMusicSrvConnected() {
        if (mMusicSrv.isPlaying()) {
            mBtnPlay.setVisibility(View.GONE);
            mBtnPause.setVisibility(View.VISIBLE);
        }
        if (mMusicSrv.getCurrentSong() != null) {
            mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
            restartSeek();
        }
    }

    @Override
    protected void onMusicSrvDisconnected() {
        mLrcView.release();
        stopSeek();
    }

    @Override
    protected void onMusicPrev() {
        if (mMusicSrv != null && mLrcView != null && mMusicSrv.getCurrentSong() != null) {
            mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
            mSeek.setMax(mMusicSrv.getPlayer().getDuration());
        }
    }

    @Override
    protected void onMusicNext() {
        if (mMusicSrv != null && mLrcView != null && mMusicSrv.getCurrentSong() != null) {
            mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
            mSeek.setMax(mMusicSrv.getPlayer().getDuration());
        }
    }

    @Override
    protected void onMusicPlay() {
        if (mMusicSrv != null && mLrcView != null) {
            mLrcView.start();
            restartSeek();
        }
    }

    @Override
    protected void onMusicPause() {
        if (mLrcView != null)
            mLrcView.stop();
        stopSeek();
    }

    @Override
    protected void onMusicSeek() {
        mLrcView.refresh();
        mSeek.setProgress(mMusicSrv.getCurrentPosition());
    }
}
