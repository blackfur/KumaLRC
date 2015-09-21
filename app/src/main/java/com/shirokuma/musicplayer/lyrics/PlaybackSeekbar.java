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
        Integer max = (Integer) pars[0];
        setMax(max);
    }

    @Override
    public void progress(Object... pars) {
        Integer progress = (Integer) pars[0];
        setProgress(progress);
    }

    @Override
    public void stop() {
    }
}
