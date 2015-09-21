package com.shirokuma.musicplayer.lyrics;

import java.util.Objects;

public interface FollowPlayback {
    void reset(Object... pars);

    void progress(Object... pars);

    void stop();
}
