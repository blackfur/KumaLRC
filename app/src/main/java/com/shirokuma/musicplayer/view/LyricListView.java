package com.shirokuma.musicplayer.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.lyrics.FollowPlayback;
import com.shirokuma.musicplayer.lyrics.SimpleLyrics;
import com.shirokuma.musicplayer.musiclib.Song;

public class LyricListView extends ListView implements FollowPlayback {
    private SimpleLyrics mLyrics;
    private Song mSong;
    private ProgressDialog progressDialog;

    public LyricListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLyrics = new SimpleLyrics();
    }

    public boolean isFoundLrc() {
        return mLyrics.isFoundLrc();
    }

    @Override
    public void reset(Object... pars) {
        post(new Runnable() {
            @Override
            public void run() {
                setVisibility(View.GONE);
            }
        });
        final Song song = (Song) pars[0];
        if (!mLyrics.findLrc(song)) {
            return;
        }
        mSong = song;
        // parse lyrics
        post(new Runnable() {
            @Override
            public void run() {
                progressDialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.loading));
            }
        });
        mLyrics.parse(song.lrc);
        post(new Runnable() {
            @Override
            public void run() {
                setAdapter(new LyricAdapter());
                setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void progress(Object... pars) {
        if (getVisibility() == View.VISIBLE && mLyrics.isFoundLrc() && mLyrics.getParsed()) {
            int progress = (Integer) pars[0];
            mLyrics.setCurrentTime(progress);
            post(new Runnable() {
                public void run() {
                    final int position = mLyrics.getCurrentPosition();
                    smoothScrollToPosition(position + 2);
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // refresh color
                            int totalVisible = getLastVisiblePosition() - getFirstVisiblePosition();
                            for (int i = 0; i <= totalVisible; i++)
                                ((TextView) getChildAt(i)).setTextColor(Color.WHITE);
                            // current line
                            int index = position - getFirstVisiblePosition();
                            TextView textView = (TextView) getChildAt(index);
                            if (textView != null)
                                textView.setTextColor(Color.BLUE);
                        }
                    }, 128);
                }
            });
        }
    }

    private class LyricAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mLyrics.getCount();
        }

        @Override
        public Object getItem(int position) {
            return mLyrics.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            SimpleLyrics.Line item = mLyrics.getItem(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.line, parent, false);
                holder.line = (TextView) convertView.findViewById(R.id.line);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (item != null) {
                holder.line.setText(item.lrc);
                if (item.begin == mLyrics.getCurrentTime())
                    holder.line.setTextColor(Color.BLUE);
                else
                    holder.line.setTextColor(Color.WHITE);
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView line;
    }
}
