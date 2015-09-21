package com.shirokuma.musicplayer.playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicBroadcast extends BroadcastReceiver {
    public enum Playback {
        Next(0), Previous(1), Stop(2), Pause(3), Play(4), Rewind(5), FastForward(6);
        int index;

        Playback(int i) {
            index = i;
        }

        static Playback valueOfIndex(int index) {
            switch (index) {
                case 0:
                    return Next;
                case 1:
                    return Previous;
                case 2:
                    return Stop;
                case 3:
                    return Pause;
                case 4:
                    return Play;
                case 5:
                    return Rewind;
                case 6:
                    return FastForward;
            }
            return null;
        }

        public int getIndex() {
            return index;
        }
    }

    public static final String MUSIC_BROADCAST_ACTION_PLAYBACK = "com.shirokuma.musicplayer.playback.MusicBroadcast.playback";
    public static final String MUSIC_BROADCAST_EXTRA = "com.shirokuma.musicplayer.playback.FollowPlayback";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(MUSIC_BROADCAST_ACTION_PLAYBACK)) {
            onReceivePlayback(Playback.valueOfIndex(intent.getExtras().getInt(MUSIC_BROADCAST_EXTRA)));
        }
    }

    protected void onReceivePlayback(Playback action) {
    }
}
