package com.shirokuma.musicplayer.lyrics;

import com.shirokuma.musicplayer.common.CheckLanguage;
import com.shirokuma.musicplayer.common.Utils;
import com.shirokuma.musicplayer.musiclib.Song;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lyrics {
    private static TreeMap<Integer, Sentence> mSentences;
    private final Pattern mTimePattern, mLyricPattern;

    public Integer prev(int index) {
        return mSentences.lowerKey(index);
    }

    public Integer next(int index) {
        return mSentences.higherKey(index);
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    private int mCurrentIndex;    //保存歌词TreeMap的下标

    public void setScreenWidth(float screenWidth) {
        // divide 1.1 to leave space as margin
//        mLrcLineLen = (int) (mScreenWidth / 1.1 / mCharSize);
    }

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

    public float getCharSize() {
        return mCharSize;
    }

    private float mCharSize = 32;
    // how many characters in one line
    private int mLrcLineLen = 20;
    private int mWestFontSize, mEastFontSize, mWestFontLineLength, mEastFontLineLength;

    public Lyrics(int widthPixels) {
        // calculate size
        float ratio = widthPixels / (float) Utils.STANDARD_SCREEN_WIDTH;
        mWestFontSize = Math.round(Utils.STANDARD_WEST_FONT_SIZE * ratio);
        mWestFontLineLength = Utils.STANDARD_WEST_FONT_LINE_LENGTH;
        mEastFontSize = Math.round(Utils.STANDARD_EAST_FONT_SIZE * ratio);
        mEastFontLineLength = Utils.STANDARD_EAST_FONT_LINE_LENGTH;
        // initial array
        mSentences = new TreeMap();
        // match time
        mTimePattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\]");
        // match multiple time
        mLyricPattern = Pattern.compile("((\\[\\d{2}:\\d{2}\\.\\d{2}\\])+)([^\\[]+)");
        // match character
        mCharPattern = Pattern.compile("\\w");
    }

    public Sentence getCurrentSentence() {
        return mSentences.get(mCurrentIndex);
    }

    public Sentence getSentence(Integer index) {
        return mSentences.get(index);
    }

    /**
     * 读取歌词文件
     *
     * @param file 歌词的路径
     */
    public void parse(String file) {
        // the reason of using tree map is that it's keyset method would return a set already sorted
        mSentences.clear();
        String data;
        try {
            File lrcFile = new File(file);
            if (!lrcFile.isFile()) {
                mFoundLrc = false;
                return;
            }
            FileInputStream stream = new FileInputStream(lrcFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "GB2312"));
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
                        Sentence sentence = new Sentence();
                        sentence.begin = currTime;
                        sentence.lrc = content;
                        mSentences.put(currTime, sentence);
                    }
                }
            }
            stream.close();
        } catch (FileNotFoundException ignored) {
        } catch (IOException ignored) {
        }
        // calculate duration of every lyric and change key value(use index instead of time)
        Iterator<Integer> iterator = mSentences.keySet().iterator();
        Sentence prev = null;
        while (iterator.hasNext()) {
            Object ob = iterator.next();
            Sentence current = mSentences.get(ob);
            if (prev == null)
                prev = current;
            else {
                prev.timeline = current.begin - prev.begin;
                prev = current;
            }
        }
        mCurrentIndex = mSentences.firstKey();
        // draw parameters
        // obtain random one to check locale
        Sentence obj = mSentences.get(mCurrentIndex);
        if (CheckLanguage.guessFullNameStyle(obj.lrc) == CheckLanguage.FullNameStyle.WESTERN) {
            mAllowTruncateWord = false;
            mLrcLineLen = mWestFontLineLength;
            mCharSize = mWestFontSize;
        } else {
            mAllowTruncateWord = true;
            mLrcLineLen = mEastFontLineLength;
            mCharSize = mEastFontSize;
        }
        // add line break
        for (Sentence sentence : mSentences.values()) {
            sentence.lrcs = breakIntoMutipleLine(sentence.lrc);
        }
        mFoundLrc = true;
    }

    boolean mAllowTruncateWord = false;
    Pattern mCharPattern;

    private String[] breakIntoMutipleLine(String src) {
        try {
            if (src.length() > mLrcLineLen) {
                ArrayList<String> temp = new ArrayList<String>();
                int end = 0, start;
                while (true) {
                    start = end;
                    end = start + mLrcLineLen;
                    if (end < src.length()) {
                        if (!mAllowTruncateWord) {
                            int origin = end;
                            while (mCharPattern.matcher(String.valueOf(src.charAt(end))).find() && end > 0) {
                                end--;
                            }
                            // it's a long word!!!
                            if (end == 1)
                                end = origin;
                            else
                                end++;
                        }
                        temp.add(src.substring(start, end));
                    } else {
                        temp.add(src.substring(start));
                        break;
                    }
                }
                return temp.toArray(new String[temp.size()]);
            } else {
                return new String[]{src};
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return new String[]{""};
    }

    /**
     * 按当前的歌曲的播放时间，从歌词里面获得那一句
     *
     * @param time 当前歌曲的播放时间
     * @return whether jump to new line
     */
    public boolean setCurrentIndex(int time) {
        if (!isFoundLrc()) {
            return false;
        }
        Integer index = mSentences.floorKey(time);
        // illegal time
        if (index == null)
            return false;
        boolean newLine = false;
        // if jump to new line, reset rolling y offset and step
        if (mCurrentIndex != index) {
            newLine = true;
        }
        mCurrentIndex = index;
        return newLine;
    }

    public class Sentence {
        public int begin; // 开始时间
        public int timeline; // 单句歌词用时
        public String lrc; // 单句歌词
        // if the length of this line is out of limit, break into multiple lines
        public String[] lrcs;
    }
}
