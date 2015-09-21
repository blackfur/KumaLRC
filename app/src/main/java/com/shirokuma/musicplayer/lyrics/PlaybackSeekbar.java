package com.shirokuma.musicplayer.lyrics;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class PlaybackSeekbar extends SeekBar implements FollowPlayback {
    public PlaybackSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void reset(Object... pars) {
        final Integer max = (Integer) pars[0];
        post(new Runnable() {
            @Override
            public void run() {
                setMax(max);
            }
        });
    }

    @Override
    public void progress(Object... pars) {
        final Integer progress = (Integer) pars[0];
        post(new Runnable() {
            @Override
            public void run() {
                setProgress(progress);
            }
        });
    }
}
