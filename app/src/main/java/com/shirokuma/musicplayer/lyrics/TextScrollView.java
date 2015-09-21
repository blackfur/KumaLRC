package com.shirokuma.musicplayer.lyrics;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.shirokuma.musicplayer.common.Utils;
import com.shirokuma.musicplayer.musiclib.Song;

public class TextScrollView extends ScrollView implements FollowPlayback {
    TextView content;
    int halfScreenHeightPx;
    boolean found;

    public TextScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        content = new TextView(context);
        content.setTextSize(24);
        content.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(content);
        found = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        halfScreenHeightPx = h / 2;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void reset(Object... pars) {
        final Song song = (Song) pars[0];
        String txt = new StringBuilder().append(song.path.substring(0, song.path.lastIndexOf('.'))).append(".txt").toString();
        final String str = Utils.file2str(txt, null);
        post(new Runnable() {
            @Override
            public void run() {
                if (str != null) {
                    content.setText(str);
                    found = true;
                } else {
                    // if no txt found then show the title
                    content.setText(song.head());
                    found = false;
                }
                scrollTo(0, 0);
                setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void progress(Object... pars) {
        // if txt lyrics found, auto scroll depend on playback progress
        if (getVisibility() == View.VISIBLE && found) {
            int duration = (Integer) pars[0];
            int progress = (Integer) pars[1];
            int y = 0;
            if (progress > 0)
                y = content.getHeight() / (duration / progress);
            if (y > halfScreenHeightPx) {
                final int finalY = y;
                post(new Runnable() {
                    @Override
                    public void run() {
                        scrollTo(0, finalY - halfScreenHeightPx);
                    }
                });
            }
        }
    }
}
