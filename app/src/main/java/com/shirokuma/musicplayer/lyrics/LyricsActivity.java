package com.shirokuma.musicplayer.lyrics;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BindSrvOpMenusActivity;
import com.shirokuma.musicplayer.common.Utils;
import com.shirokuma.musicplayer.musiclib.Song;
import com.shirokuma.musicplayer.playback.MusicService;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class LyricsActivity extends BindSrvOpMenusActivity {
    View mBtnBack;
    private ImageButton mBtnPlay, mBtnStop, mBtnPause, mBtnPre, mBtnNext, mBtnRewind, mBtnFastFroward;
    SeekBar mSeek;
    // LRC lyrics
    private LyricView mLrcView;
    // txt lyrics
    private ScrollView mLrcScroll;
    TextView mLrcContent;

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
                    mBtnPlay.setVisibility(View.GONE);
                    mBtnPause.setVisibility(View.VISIBLE);
                    break;
                case R.id.previous:
                    mMusicSrv.playPrev();
                    mBtnPlay.setVisibility(View.GONE);
                    mBtnPause.setVisibility(View.VISIBLE);
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
                case R.id.btn_back:
                    finish();
                    break;
            }
        }
    };
    SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mMusicSrv != null && mMusicSrv.getCurrentState() != MusicService.State.Stopped && mMusicSrv.getDuration() > 0 && fromUser) {
                mMusicSrv.seekTo(progress);
                mLrcView.refresh();
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
        mLrcScroll = (ScrollView) findViewById(R.id.txt_lrc);
        mLrcContent = (TextView) findViewById(R.id.txt_lrc_content);
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

    // update UI(txt lyrics and seek bar) according to playback progress
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
                                mSeek.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSeek.setProgress(mMusicSrv.getCurrentPosition());
                                        // if txt lyrics found, auto scroll depend on playback progress
                                        if (mLrcScroll.getVisibility() == View.VISIBLE) {
                                            int y = 0;
                                            if (mMusicSrv.getCurrentPosition() > 0)
                                                y = mLrcScroll.getChildAt(0).getHeight() / (mMusicSrv.getDuration() / mMusicSrv.getCurrentPosition());
                                            mLrcScroll.scrollTo(0, y);
                                        }
                                    }
                                });
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
            resetLrc();
            restartSeek();
        }
    }

    private void resetLrc() {
        // try to find a LRC file and show
        if (mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong())) {
            mLrcScroll.setVisibility(View.GONE);
        } else {
            mLrcScroll.setVisibility(View.VISIBLE);
            // if there is no LRC file, try to find a txt
            String txt = new StringBuilder().append(mMusicSrv.getCurrentSong().path.substring(0, mMusicSrv.getCurrentSong().path.lastIndexOf('.'))).append(".txt").toString();
            String content = Utils.file2str(txt);
            if (content != null) {
                mLrcContent.setText(content);
            } else {
                // if no txt found then show the title
                mLrcContent.setText(mMusicSrv.getCurrentSong().head());
            }
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
//            mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
            resetLrc();
            mSeek.setMax(mMusicSrv.getPlayer().getDuration());
            if (mTimer == null)
                restartSeek();
        }
    }

    @Override
    protected void onMusicNext() {
        if (mMusicSrv != null && mLrcView != null && mMusicSrv.getCurrentSong() != null) {
//            mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
            resetLrc();
            mSeek.setMax(mMusicSrv.getPlayer().getDuration());
            if (mTimer == null)
                restartSeek();
        }
    }

    @Override
    protected void onMusicPlay() {
        if (mMusicSrv != null && mLrcView != null) {
            if (mLrcView.getVisibility() == View.VISIBLE)
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
        if (mLrcView.getVisibility() == View.VISIBLE)
            mLrcView.refresh();
        mSeek.setProgress(mMusicSrv.getCurrentPosition());
    }
}
