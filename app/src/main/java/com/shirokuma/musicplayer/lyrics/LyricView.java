package com.shirokuma.musicplayer.lyrics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.list.Song;

import java.util.Timer;
import java.util.TimerTask;

public class LyricView extends View {
    private float STEP = 0.8f;
    private float mMiddleX;        //屏幕X轴的中点，此值固定，保持歌词在X中间显示
    private float mMiddleY;
    private float mMaxY; // bottom y
    // allow move lyrics position
    private boolean mAllowMove = false;
    private float touchY;    //当触摸歌词View时，保存为当前触点的Y轴坐标
    private int INTERVEL = 64;
    private float mLineSpace;//歌词每行的间隔
    Paint paint = new Paint();//画笔，用于画不是高亮的歌词
    Paint paintHL = new Paint();//画笔，用于画高亮的歌词，即当前唱到这句歌词
    private MediaPlayer mPlayer;
    private Timer mTimer;
    private Handler mUIhandler;
    private Lyrics mLyrics;
    Handler mWorkHandler;
    private HandlerThread mWorkThread;

    private void init() {
        // paint
        mWorkThread = new HandlerThread("lyric view");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper());
        mUIhandler = new Handler();
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setAlpha(180);
        paintHL = new Paint();
        paintHL.setTextAlign(Paint.Align.CENTER);
        paintHL.setColor(Color.BLUE);
        paintHL.setAntiAlias(true);
        paintHL.setAlpha(255);
        // calculate size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // lyrics
        mLyrics = new Lyrics(displayMetrics.widthPixels);
        setBackgroundColor(getContext().getResources().getColor(R.color.grey));
    }

    public LyricView(Context context) {
        super(context);
        init();
    }

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    Song mSong;

    public void reset(MediaPlayer player, final Song song) {
        mSong = song;
        mTouchOffsetY = 0;
        mPlayer = player;
        // parse lyrics
        final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading));
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                mLyrics.parse(song.lrc);
                mLineSpace = mLyrics.getCharSize() / 2;
                progressDialog.dismiss();
                start();
            }
        });
    }

    public void start() {
        mUIhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPlayer.isPlaying() && mLyrics.isFoundLrc()) {
                    // start draw thread
                    if (mTimer != null)
                        mTimer.cancel();
                    mTimer = new Timer();
                    if (rollTask != null)
                        rollTask.cancel();
                    rollTask = new RollTask();
                    mTimer.schedule(rollTask, 200, INTERVEL);
                } else if (mSong != null) {
                    // if not playing, show the lyrics found or just title if not found
                    invalidate();
                }
            }
        }, 200);
    }

    public void stop() {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = new Timer();
        if (rollTask != null)
            rollTask.cancel();
        mRollingOffsetY = mTouchOffsetY = 0;
    }

    // roll lyrics
    TimerTask rollTask;

    public void zoomIn() {
        mLyrics.setCharSize(mLyrics.getCharSize() * 1.2f);
    }

    public void zoomOut() {
        mLyrics.setCharSize(mLyrics.getCharSize() * 0.8f);
    }

    public void refresh() {
        mUIhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // if it's playing now, leave the refresh job to rolling task
                if (!mPlayer.isPlaying()) {
                    mLyrics.setCurrentIndex(mPlayer.getCurrentPosition());
                    invalidate();
                }
            }
        }, 200);
    }

    private class RollTask extends TimerTask {
        @Override
        public void run() {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    boolean newLine = mLyrics.setCurrentIndex(mPlayer.getCurrentPosition());
                    // reset rolling offset and step
                    if (newLine) {
                        mRollingOffsetY = 0;
                        Lyrics.Sentence obj = mLyrics.getCurrentSentence();
                        if (obj.timeline > 0)
                            STEP = (mLyrics.getCharSize() + mLineSpace) * obj.lrcs.length * INTERVEL / (float) obj.timeline;
                        else {
                            STEP = 0.8f;
                        }
                    }
                    mUIhandler.post(new Runnable() {
                        public void run() {
                            invalidate(); // 更新视图
                        }
                    });
                }
            } else {
                stop();
            }
        }
    }

    public void release() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (rollTask != null) {
            rollTask.cancel();
            rollTask = null;
        }
        mWorkThread.quit();
    }

    // used for roll smoothly
    private float mRollingOffsetY;

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            // lyrics
            if (mLyrics.isFoundLrc()) {
                paintHL.setTextSize(mLyrics.getCharSize());
                paint.setTextSize(mLyrics.getCharSize());
                Lyrics.Sentence temp = mLyrics.getCurrentSentence();
                if (temp.lrc.length() == 0) {
                    canvas.drawText("~", mMiddleX, mMiddleY + mRollingOffsetY + mTouchOffsetY, paintHL);
                } else {
                    for (int i = 0; i < temp.lrcs.length; i++) {
                        canvas.drawText(temp.lrcs[i], mMiddleX, mMiddleY + mRollingOffsetY + mTouchOffsetY + (mLyrics.getCharSize() + mLineSpace) * i, paintHL);
                    }
                }
                // 画当前歌词之后的歌词
                // offset y
                int j = temp.lrcs.length;
                // get next sentence's index
                Integer current = mLyrics.next(mLyrics.getCurrentIndex());
                while (current != null) {
                    float positionY = mMiddleY + (mLyrics.getCharSize() + mLineSpace) * j;
                    // out of screen
                    if (positionY > mMaxY) {
                        break;
                    }
                    temp = mLyrics.getSentence(current);
                    if (temp.lrc.length() == 0) {
                        canvas.drawText("~", mMiddleX, positionY + mRollingOffsetY + mTouchOffsetY, paint);
                        j++;
                    } else {
                        for (int x = 0; x < temp.lrcs.length; x++) {
                            canvas.drawText(temp.lrcs[x], mMiddleX, positionY + mRollingOffsetY + mTouchOffsetY + (mLyrics.getCharSize() + mLineSpace) * x, paint);
                        }
                        j += temp.lrcs.length;
                    }
                    current = mLyrics.next(current);
                }
                // 画当前歌词之前的歌词
                j = 1;
                current = mLyrics.prev(mLyrics.getCurrentIndex());
                while (current != null) {
                    float positionY = mMiddleY - (mLyrics.getCharSize() + mLineSpace) * j;
                    // out of screen
                    if (positionY < 0) {
                        break;
                    }
                    temp = mLyrics.getSentence(current);
                    if (temp.lrc.length() == 0) {
                        canvas.drawText("~", mMiddleX, positionY + mRollingOffsetY + mTouchOffsetY, paint);
                        j++;
                    } else {
                        for (int x = 0; x < temp.lrcs.length; x++) {
                            // revers print
                            int index = temp.lrcs.length - x - 1;
                            canvas.drawText(temp.lrcs[index], mMiddleX, positionY + mRollingOffsetY + mTouchOffsetY - (mLyrics.getCharSize() + mLineSpace) * x, paint);
                        }
                        j += temp.lrcs.length;
                    }
                    current = mLyrics.prev(current);
                }
                // keep rolling during singing current lyric
                if (-mRollingOffsetY < (mLyrics.getCharSize() + mLineSpace) * mLyrics.getCurrentSentence().lrcs.length)
                    mRollingOffsetY -= STEP;
            } else {
                paint.setTextSize(20);
                canvas.drawText(mSong.title + " - " + mSong.artist, mMiddleX, mMiddleY, paint);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        super.onDraw(canvas);
    }

    private float mTouchOffsetY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLyrics.isFoundLrc() && mAllowMove) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchOffsetY = event.getY() - touchY;
                    break;
                case MotionEvent.ACTION_UP:
                    mTouchOffsetY = event.getY() - touchY;
                    break;
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mMiddleX = w * 0.5f;
        // little upper than middle line
        mMiddleY = h * 0.5f;
        mMaxY = h;
        mLyrics.setScreenWidth(w);
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
