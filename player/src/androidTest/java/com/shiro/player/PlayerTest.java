package com.shiro.player;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.shirokuma.musicplayer.KumaPlayer;
import com.shirokuma.musicplayer.lyrics.SimpleLyrics;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PlayerTest {
    @Test
    public void main() {
        Log.e(KumaPlayer.TAG, "---- test main ----");
    }

    private void testLrc() {
        String lyricPath = "";
        SimpleLyrics mLyrics = new SimpleLyrics();
//        mLyrics.parse(song.lrc);
    }
}
