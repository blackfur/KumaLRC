package com.shirokuma.musicplayer.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.lyrics.FollowPlayback;
import com.shirokuma.musicplayer.lyrics.Lyrics;
import com.shirokuma.musicplayer.musiclib.Song;

public class LyricView extends View implements FollowPlayback {
    ProgressDialog progressDialog;
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
    private Lyrics mLyrics;
    private boolean parsed = false;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // paint
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
    }

    public boolean ifFoundLRC() {
        return mLyrics.isFoundLrc();
    }

    Song mSong;

    @Override
    public void reset(Object... pars) {
        final Song song = (Song) pars[0];
        if (mLyrics.findLrc(song)) {
            post(new Runnable() {
                @Override
                public void run() {
                    setVisibility(View.VISIBLE);
                }
            });
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    setVisibility(View.GONE);
                }
            });
            return;
        }
        mSong = song;
        mRollingOffsetY = mTouchOffsetY = 0;
        // parse lyrics
        post(new Runnable() {
            @Override
            public void run() {
                progressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading));
            }
        });
        parsed = false;
        mLyrics.parse(song.lrc);
        mLineSpace = mLyrics.getCharSize() / 2;
        parsed = true;
        post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void progress(final Object... pars) {
        if (getVisibility() == View.VISIBLE && mLyrics.isFoundLrc() && parsed) {
            int progress = (Integer) pars[0];
            boolean newLine = mLyrics.setCurrentIndex(progress);
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
            post(new Runnable() {
                public void run() {
                    invalidate();
                }
            });
        }
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
