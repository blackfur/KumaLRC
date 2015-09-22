package com.shirokuma.musicplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;
import com.shirokuma.musicplayer.lyrics.FollowPlayback;

public class PlaybackSeekbar extends SeekBar implements FollowPlayback {
    public PlaybackSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void reset(Object... pars) {
        final Integer max = (Integer) pars[0];
        // make sure update UI in UI thread
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
        // make sure update UI in UI thread
        post(new Runnable() {
            @Override
            public void run() {
                setProgress(progress);
            }
        });
    }
}
