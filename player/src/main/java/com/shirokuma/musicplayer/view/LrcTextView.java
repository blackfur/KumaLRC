package com.shirokuma.musicplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.shirokuma.musicplayer.lyrics.FollowPlayback;

public class LrcTextView extends RelativeLayout implements FollowPlayback {
    public LrcTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void reset(Object... pars) {
    }

    @Override
    public void progress(Object... pars) {
    }
}
