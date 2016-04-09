package com.shirokuma.musicplayer.lyrics;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import com.shiro.tools.Listener;
import com.shirokuma.musicplayer.PlayerEnv;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BindMusicSrvActivity;
import com.shirokuma.musicplayer.playback.MusicService;
import com.shirokuma.musicplayer.view.LyricListView;
import com.shirokuma.musicplayer.view.PlaybackSeekBar;
import com.shirokuma.musicplayer.view.TextScrollView;

import java.util.Timer;
import java.util.TimerTask;

public class LyricsActivity extends BindMusicSrvActivity {
//    View mBtnBack;
    private ImageButton mBtnPlay, mBtnStop, mBtnPause, mBtnPre, mBtnNext, mBtnRewind, mBtnFastFroward;
    PlaybackSeekBar mSeek;
    // LRC lyrics
    private LyricListView mLrcView;
    // txt lyrics
    private TextScrollView mTextScroll;
    Handler mWorkHandler;
    private HandlerThread mWorkThread;
    ProgressDialog progress;

    protected int setContentViewRes() {
        return R.layout.activity_lyrics;
    }
    @Override
    protected void initData() {
        super.initData();
        mWorkThread = new HandlerThread("follow playback work");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());
    }

    protected View.OnClickListener mBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.play) {
                mMusicSrv.play();
                mBtnPlay.setVisibility(View.GONE);
                mBtnPause.setVisibility(View.VISIBLE);
            } else if (i == R.id.next) {
                mMusicSrv.playNext();
                mBtnPlay.setVisibility(View.GONE);
                mBtnPause.setVisibility(View.VISIBLE);
            } else if (i == R.id.previous) {
                mMusicSrv.playPrev();
                mBtnPlay.setVisibility(View.GONE);
                mBtnPause.setVisibility(View.VISIBLE);
            } else if (i == R.id.stop) {
                stop();
                mMusicSrv.stop();
                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnPause.setVisibility(View.GONE);
            } else if (i == R.id.pause) {
                mMusicSrv.pause();
                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnPause.setVisibility(View.GONE);
            } else if (i == R.id.rewind) {
                mMusicSrv.rewind();
            } else if (i == R.id.fastforward) {
                mMusicSrv.fastForward();
            } else if (i == R.id.btn_back) {
                finish();
            }
        }
    };
    SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mMusicSrv != null && mMusicSrv.getCurrentState() != MusicService.State.Stopped && mMusicSrv.getDuration() > 0 && fromUser) {
                mMusicSrv.seekTo(progress);
                mLrcView.progress(progress);
                //mTextScroll.progress(mMusicSrv.getDuration(), progress);
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
        mSeek = (PlaybackSeekBar) findViewById(R.id.seek_bar);
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
        mLrcView = (LyricListView) findViewById(R.id.lrclist);
        mTextScroll = (TextScrollView) findViewById(R.id.txt_lrc);
        mSeek.setOnSeekBarChangeListener(mSeekListener);
        progress = ProgressDialog.show(this, "", "loading", false, true);
    }


    @Override
    public void onDestroy() {
        stop();
        if (mWorkHandler != null) {
            mWorkHandler = null;
            mWorkThread.quit();
            mWorkThread = null;
        }
        super.onDestroy();
    }

    private void reset(final Listener callback) {
        mWorkHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSeek.reset(mMusicSrv.getDuration());
                // try to find a LRC file and show
                mLrcView.reset(mMusicSrv.getCurrentSong());
                // if there is no LRC file, try to find a txt
                if (mLrcView.isFoundLrc()) {
                    mSeek.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextScroll.setVisibility(View.GONE);
                        }
                    });
                } else {
                    mTextScroll.reset(mMusicSrv.getCurrentSong());
                }
                if (callback != null) {
                    // go back to main thread
                    mSeek.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.process();
                        }
                    });
                }
            }
        }, 200);
    }

    TimerTask progressTask;
    Timer mTimer;

    // update UI(txt lyrics and seek bar) according to playback progress
    private void start() {
        // delay to wait for player preparing
        mSeek.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMusicSrv.isPlaying()) {
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer.purge();
                    }
                    mTimer = new Timer();
                    if (progressTask != null) {
                        progressTask.cancel();
                    }
                    progressTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (mMusicSrv != null) {
                                mWorkHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSeek.progress(mMusicSrv.getCurrentPosition());
                                        //mTextScroll.progress(mMusicSrv.getDuration(), mMusicSrv.getCurrentPosition());
                                        mLrcView.progress(mMusicSrv.getCurrentPosition());
                                    }
                                });
                            }
                        }
                    };
                    mTimer.schedule(progressTask, 0, PlayerEnv.SEEK_INTERVAL);
                }
            }
        }, 200);
    }

    private void stop() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (progressTask != null) {
            progressTask.cancel();
            progressTask = null;
        }
    }

    @Override
    protected void onMusicSrvConnected() {
        progress.dismiss();
        if (mMusicSrv.isPlaying()) {
            mBtnPlay.setVisibility(View.GONE);
            mBtnPause.setVisibility(View.VISIBLE);
        }
        if (mMusicSrv.getCurrentSong() != null) {
            reset(new Listener() {
                public Object process(Object... pars) {
                    start();
                    return null;
                }
            });
        }
    }

    @Override
    protected void onMusicSrvDisconnected() {
        stop();
    }

    @Override
    protected void onMusicPrev() {
        if (mMusicSrv != null && mLrcView != null && mMusicSrv.getCurrentSong() != null) {
//            mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
            reset(new Listener() {
                @Override
                public Object process(Object... pars) {
                    if (mTimer == null)
                        start();
                    return null;
                }
            });
        }
    }

    @Override
    protected void onMusicNext() {
        if (mMusicSrv != null && mLrcView != null && mMusicSrv.getCurrentSong() != null) {
//            mLrcView.reset(mMusicSrv.getPlayer(), mMusicSrv.getCurrentSong());
            reset(new Listener() {
                @Override
                public Object process(Object... pars) {
                    if (mTimer == null)
                        start();
                    return null;
                }
            });
        }
    }

    @Override
    protected void onMusicPlay() {
        if (mMusicSrv != null && mLrcView != null) {
            start();
        }
    }

    @Override
    protected void onMusicPause() {
        stop();
    }

    @Override
    protected void onMusicSeek() {
        mLrcView.progress(mMusicSrv.getCurrentPosition());
        mSeek.progress(mMusicSrv.getCurrentPosition());
        //mTextScroll.progress(mMusicSrv.getDuration(), mMusicSrv.getCurrentPosition());
    }
}
