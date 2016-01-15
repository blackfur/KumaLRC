package com.shirokuma.musicplayer.lyrics;

import android.util.Log;
import com.shirokuma.musicplayer.KumaPlayer;
import com.shirokuma.musicplayer.model.Song;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleLyrics {
    private static TreeMap<Integer, Line> mLines;
    private ArrayList<Line> mSequence;
    private final Pattern mTimePattern, mLyricPattern;
    private boolean parsed;

    public SimpleLyrics() {
        // initial array
        mLines = new TreeMap();
        mSequence = new ArrayList<Line>();
        // match time
        mTimePattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\]");
        // match multiple time
        mLyricPattern = Pattern.compile("((\\[\\d{2}:\\d{2}\\.\\d{2}\\])+)([^\\[]+)");
        // match character
        mCharPattern = Pattern.compile("\\w");
        parsed = false;
    }

    public int getCurrentTime() {
        return mCurrentTime;
    }

    private int mCurrentTime;    //保存歌词TreeMap的下标

    public boolean findLrc(Song s) {
        if (!new File(s.lrc).exists()) {
            mFoundLrc = false;
            return false;
        } else {
            mFoundLrc = true;
            return true;
        }
    }

    public boolean isFoundLrc() {
        return mFoundLrc;
    }

    private boolean mFoundLrc = false;

    public void parse(String file) {
        Log.e(KumaPlayer.TAG, "---- parsing lyric file ----");
        parsed = false;
        // the reason of using tree map is that it's keyset method would return a set already sorted
        mLines.clear();
        mSequence.clear();
        String data;
        try {
            File lrcFile = new File(file);
            if (!lrcFile.isFile()) {
                mFoundLrc = false;
                return;
            }
            FileInputStream stream = new FileInputStream(lrcFile);
            //BufferedReader br = new BufferedReader(new InputStreamReader(stream, "GB2312"));
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
            while ((data = br.readLine()) != null) {
                Matcher lyricMatcher = mLyricPattern.matcher(data);
                while (lyricMatcher.find()) {
                    // content is in the last group
                    String content = lyricMatcher.group(lyricMatcher.groupCount());
                    Matcher timeMatcher = mTimePattern.matcher(lyricMatcher.group(1));
                    while (timeMatcher.find()) {
                        int m = Integer.parseInt(timeMatcher.group(1));  //分
                        int s = Integer.parseInt(timeMatcher.group(2));  //秒
                        int ms = Integer.parseInt(timeMatcher.group(3)); //毫秒
                        int currTime = (m * 60 + s) * 1000 + ms * 10;
                        Line line = new Line();
                        line.begin = currTime;
                        line.lrc = content;
                        mLines.put(currTime, line);
                    }
                }
            }
            stream.close();
        } catch (FileNotFoundException ignored) {
        } catch (IOException ignored) {
        }
        // calculate duration of every lyric and change key value(use index instead of time)
        Iterator<Integer> iterator = mLines.keySet().iterator();
        Line prev = null;
        while (iterator.hasNext()) {
            Object ob = iterator.next();
            Line current = mLines.get(ob);
            if (prev == null)
                prev = current;
            else {
                prev.timeline = current.begin - prev.begin;
                prev = current;
            }
        }
        Integer i = mCurrentTime = mLines.firstKey();
        // arrange lyrics by time
        while (i != null) {
            Line line = mLines.get(i);
            mSequence.add(line);
            line.position = mSequence.size() - 1;
            i = mLines.higherKey(i);
        }
        parsed = true;
        mFoundLrc = true;
    }

    Pattern mCharPattern;

    /**
     * 按当前的歌曲的播放时间，从歌词里面获得那一句
     *
     * @param time 当前歌曲的播放时间
     * @return whether jump to new line
     */
    public void setCurrentTime(int time) {
        if (!mFoundLrc) {
            return;
        }
        Integer index = mLines.floorKey(time);
        // illegal time
        if (index == null)
            return;
        mCurrentTime = index;
    }

    public int getCount() {
        if (mLines != null)
            return mLines.size();
        else return 0;
    }

    public Line getItem(int position) {
        if (position < mSequence.size())
            return mSequence.get(position);
        else return null;
    }

    public boolean getParsed() {
        return parsed;
    }

    public int getCurrentPosition() {
        Line line = mLines.get(mCurrentTime);
        if (line != null)
            return line.position;
        else
            return 0;
    }

    public class Line {
        public int begin;
        public int timeline;
        // content
        public String lrc;
        // sequence position
        public int position = 0;
    }
}
