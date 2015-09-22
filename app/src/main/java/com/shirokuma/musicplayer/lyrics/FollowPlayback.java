package com.shirokuma.musicplayer.lyrics;

// update UI according to playback state
public interface FollowPlayback {
    void reset(Object... pars);

    void progress(Object... pars);
}
