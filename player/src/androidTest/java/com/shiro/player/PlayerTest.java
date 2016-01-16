package com.shiro.player;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import com.shirokuma.musicplayer.KumaPlayer;
import com.shirokuma.musicplayer.lyrics.SimpleLyrics;
import com.shirokuma.musicplayer.model.Song;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileFilter;

@RunWith(AndroidJUnit4.class)
public class PlayerTest {
    @Test
    public void testMain() {
        Log.e(KumaPlayer.TAG, "---- test main ----");
        testScan();
    }

    private void testLrc() {
        String lyricPath = "";
        SimpleLyrics mLyrics = new SimpleLyrics();
//        mLyrics.parse(song.lrc);
    }

    void testScan() {
        FileFilter audioFilter = new FileFilter() {
            public boolean accept(File file) {
                Log.e(KumaPlayer.TAG, "checking file type: " + file.getName());
                if (file.isFile()) {
                    String name = file.getName();
                    String extension = name.substring(name.lastIndexOf('.') + 1);
                    if (extension.equalsIgnoreCase("mp3")) {
                        Log.e(KumaPlayer.TAG, "check passed");
                        return true;
                    }
                }
                return false;
            }
        };
        Log.e(KumaPlayer.TAG, "==== read then store audio file meta data ====");
        try {
            File searchDir = new File("/storage/sdcard1/music");
            Log.e(KumaPlayer.TAG, searchDir.getName());
            for (File f : searchDir.listFiles(audioFilter)) {
                String path = f.getAbsolutePath();
                Log.e(KumaPlayer.TAG, "reading: " + path);
                MusicMetadataSet src_set = new MyID3().read(f);
                Log.e(KumaPlayer.TAG, "MusicMetadataSet: " + src_set);
                IMusicMetadata metadata = src_set.getSimplified();
                Log.e(KumaPlayer.TAG, "IMusicMetadata: " + metadata);
                Song newSong = new Song(metadata.getSongTitle(), metadata.getArtist(), metadata.getAlbum(), f.getAbsolutePath());
                Log.e(KumaPlayer.TAG, "finding lyrics: "+ path);
                String lrc = path.substring(0, path.lastIndexOf('.')) + ".lrc";
                if (new File(lrc).exists()) {
                    Log.e(KumaPlayer.TAG, lrc);
                    newSong.lrc = lrc;
                }
                newSong.save();
                Log.e(KumaPlayer.TAG, "saved: " + path);
            }
        } catch (Exception e) {
            Log.e(KumaPlayer.TAG, e.getMessage() == null ? "Null Pointer" : e.getMessage());
        }
    }
}
