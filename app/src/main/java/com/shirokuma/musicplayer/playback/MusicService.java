package com.shirokuma.musicplayer.playback;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.musiclib.Filter;
import com.shirokuma.musicplayer.setting.MediaSetting;
import com.shirokuma.musicplayer.musiclib.Song;
import com.shirokuma.musicplayer.lyrics.LyricsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 * 
 * Sue Smith - February 2014
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    //media mPlayer
    private MediaPlayer mPlayer;
    //song list
    private ArrayList<Song> mPlaySongs;
    //current position
    private int mPlaySongIndex;
    //binder
    private final IBinder musicBind = new MusicBinder();
    //title of current song
    private String songTitle = "";
    //notification id
    private static final int NOTIFY_ID = 1;
    //shuffle flag and random
    private boolean shuffle;
    private Random rand;
    private State mCurrentState = State.Stopped;
    private Filter currentFilter;

    public void onCreate() {
        //initialize position
        mPlaySongIndex = MediaSetting.getInstance(getApplicationContext()).getLastPlayIndex();
        //random
        rand = new Random();
        //create mPlayer
        mPlayer = new MediaPlayer();
        //initialize mPlayer
        //set mPlayer properties
        mPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        shuffle = MediaSetting.getInstance(this).getShuffle();
    }

    public void setFilter(Filter f) {
        if (currentFilter == null || currentFilter != f)
            currentFilter = f;
    }

    public ArrayList getPlaySongs() {
        return mPlaySongs;
    }

    public Song getCurrentSong() {
        if (mPlaySongIndex < mPlaySongs.size())
            return mPlaySongs.get(mPlaySongIndex);
        else return null;
    }

    public void pause() {
        mPlayer.pause();
        mCurrentState = State.Paused;
        sendBroadcast(new Intent(MusicBroadcast.MUSIC_BROADCAST_ACTION_PLAYBACK).putExtra(MusicBroadcast.MUSIC_BROADCAST_EXTRA, MusicBroadcast.Playback.Pause.getIndex()));
    }

    public void stop() {
        mPlayer.stop();
        mCurrentState = State.Stopped;
    }

    //binder
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when all bound activities unbind
    @Override
    public boolean onUnbind(Intent intent) {
        MediaSetting.getInstance(getApplicationContext()).setLastPlayIndex(mPlaySongIndex);
        MediaSetting.getInstance(getApplicationContext()).setLastFilter(currentFilter);
        if (MediaSetting.getInstance(getApplicationContext()).getSaveLast() && (mCurrentState == State.Started || mCurrentState == State.Paused)) {
            MediaSetting.getInstance(getApplicationContext()).setLastPlayProgress(mPlayer.getCurrentPosition());
        } else
            MediaSetting.getInstance(getApplicationContext()).setLastPlayProgress(0);
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        return false;
    }

    public enum State {
        Paused, Stopped, Started
    }

    public void play() {
        if (mPlayer.isPlaying()) {
        } else if (mCurrentState == State.Paused) {
            mPlayer.start();
            mCurrentState = State.Started;
        } else if (mCurrentState == State.Stopped) {
            playSong(mPlaySongIndex);
        }
        sendBroadcast(new Intent(MusicBroadcast.MUSIC_BROADCAST_ACTION_PLAYBACK).putExtra(MusicBroadcast.MUSIC_BROADCAST_EXTRA, MusicBroadcast.Playback.Play.getIndex()));
    }

    public void restore() {
        //reset
        mPlayer.reset();
        //get song
        Song playSong = mPlaySongs.get(mPlaySongIndex);
        if (!new File(playSong.path).exists())
            return;
        //get title
        songTitle = playSong.title;
        //get id
        long currSong = playSong.id;
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        //set the data source
        try {
            mPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        // will seek to last progress on onPrepared callback
        mPlayer.prepareAsync();
        mCurrentState = State.Paused;
    }

    //reset a song
    private void playSong(int index) {
        if (index < mPlaySongs.size()) {
            mPlaySongIndex = index;
            //reset
            mPlayer.reset();
            //get song
            Song playSong = mPlaySongs.get(mPlaySongIndex);
            if (!new File(playSong.path).exists())
                return;
            //get title
            songTitle = playSong.title;
            //get id
            long currSong = playSong.id;
            //set uri
            Uri trackUri = ContentUris.withAppendedId(
                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    currSong);
            //set the data source
            try {
                mPlayer.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            mPlayer.prepareAsync();
            mCurrentState = State.Started;
        }
    }

    public void play(int index) {
        playSong(index);
        sendBroadcast(new Intent(MusicBroadcast.MUSIC_BROADCAST_ACTION_PLAYBACK).putExtra(MusicBroadcast.MUSIC_BROADCAST_EXTRA, MusicBroadcast.Playback.Play.getIndex()));
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        if (mp != null && mp.getCurrentPosition() > 0 && mCurrentState == State.Started) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "FollowPlayback Error");
        mp.reset();
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mCurrentState == State.Started)
            //start playback
            mp.start();
            // restore last progress when application start
        else if (mCurrentState == State.Paused) {
            mp.seekTo(MediaSetting.getInstance(this).getLastPlayProgress());
        }
        //notification
        Intent notIntent = new Intent(this, LyricsActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.button_general_mini_playing_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle(getString(R.string.playing))
                .setContentText(songTitle);
        Notification not = builder.build();
        startForeground(NOTIFY_ID, not);
    }

    //skip to previous track
    public void playPrev() {
        mPlaySongIndex--;
        if (mPlaySongIndex < 0) mPlaySongIndex = mPlaySongs.size() - 1;
        playSong(mPlaySongIndex);
        sendBroadcast(new Intent(MusicBroadcast.MUSIC_BROADCAST_ACTION_PLAYBACK).putExtra(MusicBroadcast.MUSIC_BROADCAST_EXTRA, MusicBroadcast.Playback.Previous.getIndex()));
    }

    //skip to next
    public void playNext() {
        if (shuffle) {
            mPlaySongIndex = rand.nextInt(mPlaySongs.size());
        } else {
            mPlaySongIndex++;
        }
        if (mPlaySongIndex >= mPlaySongs.size()) mPlaySongIndex = 0;
        playSong(mPlaySongIndex);
        sendBroadcast(new Intent(MusicBroadcast.MUSIC_BROADCAST_ACTION_PLAYBACK).putExtra(MusicBroadcast.MUSIC_BROADCAST_EXTRA, MusicBroadcast.Playback.Next.getIndex()));
    }

    @Override
    public void onDestroy() {
//        MediaSetting.getInstance(getApplicationContext()).setLastPlayIndex(mPlaySongIndex);
        stopForeground(true);
    }

    public void setPlaySongs(ArrayList songs) {
        if (mPlaySongs == null || mPlaySongs != songs)
            mPlaySongs = songs;
    }

    //toggle shuffle
    public void setShuffle(boolean b) {
        shuffle = b;
    }

    public void rewind() {
        // if the player is stopped, do seeking will cause illegal state exception
        if (mCurrentState == State.Stopped) {
            return;
        }
        int pos = mPlayer.getCurrentPosition();
        pos -= 5000;
        mPlayer.seekTo(pos);
        sendBroadcast(new Intent(MusicBroadcast.MUSIC_BROADCAST_ACTION_PLAYBACK).putExtra(MusicBroadcast.MUSIC_BROADCAST_EXTRA, MusicBroadcast.Playback.Rewind.getIndex()));
    }

    public void fastForward() {
        // if the player is stopped, do seeking will cause illegal state exception
        if (mCurrentState == State.Stopped) {
            return;
        }
        int pos = mPlayer.getCurrentPosition();
        pos += 15000; // milliseconds
        mPlayer.seekTo(pos);
        sendBroadcast(new Intent(MusicBroadcast.MUSIC_BROADCAST_ACTION_PLAYBACK).putExtra(MusicBroadcast.MUSIC_BROADCAST_EXTRA, MusicBroadcast.Playback.FastForward.getIndex()));
    }

    public boolean isPlaying() {
        if (mPlayer != null)
            return mPlayer.isPlaying();
        else
            return false;
    }

    public int getCurrentPosition() {
        if (mPlayer != null)
            return mPlayer.getCurrentPosition();
        return 0;
    }

    public int getDuration() {
        if (mPlayer != null)
            return mPlayer.getDuration();
        return 0;
    }

    public void seekTo(int position) {
        if (mPlayer != null && position >= 0 && position < mPlayer.getDuration())
            mPlayer.seekTo(position);
    }

    public State getCurrentState() {
        return mCurrentState;
    }
}
