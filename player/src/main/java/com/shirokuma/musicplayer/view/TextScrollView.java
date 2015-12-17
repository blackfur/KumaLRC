package com.shirokuma.musicplayer.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.shiro.tools.Utils;
import com.shirokuma.musicplayer.lyrics.FollowPlayback;
import com.shirokuma.musicplayer.model.Song;

public class TextScrollView extends ScrollView implements FollowPlayback {
    TextView content;
    int halfScreenHeightPx, screenHeightPx;
    boolean found, touching;

    public TextScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        content = new TextView(context);
        content.setPadding(16, 16, 16, 16);
        content.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        content.setLineSpacing(0.0f, 1.3f);
        content.setTextColor(Color.WHITE);
        content.setTextSize(18);
        content.setGravity(Gravity.CENTER);
        addView(content);
        touching = found = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touching = true;
                break;
            case MotionEvent.ACTION_UP:
                touching = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenHeightPx = h;
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
                    ((LayoutParams) content.getLayoutParams()).gravity = Gravity.TOP;
                    content.setText(str);
                    found = true;
                } else {
                    // if no txt found then show the title, show title in center of parent
                    ((LayoutParams) content.getLayoutParams()).gravity = Gravity.CENTER;
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
        int duration = (Integer) pars[0];
        if (getVisibility() == View.VISIBLE && found && !touching) {
            int progress = (Integer) pars[1];
            int y = 0;
            if (progress > 0 && duration > 0) {
                int propotion = (duration / progress);
                if (propotion > 0)
                    y = content.getHeight() / propotion;
            }
            if (y > halfScreenHeightPx) {
                // if the progress has not hit that point or view has not yet scroll to that point
                if (y < content.getHeight() - halfScreenHeightPx || getScrollY() < content.getHeight() - screenHeightPx) {
                    final int finalY = y;
                    post(new Runnable() {
                        @Override
                        public void run() {
                            scrollTo(0, finalY - halfScreenHeightPx);
                        }
                    });
                }
            } else if (getScrollY() > 0)
                post(new Runnable() {
                    @Override
                    public void run() {
                        scrollTo(0, 0);
                    }
                });
        }
    }
}
