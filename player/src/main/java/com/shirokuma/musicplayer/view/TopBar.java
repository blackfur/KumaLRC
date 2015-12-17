package com.shirokuma.musicplayer.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.shiro.tools.Utils;
import com.shirokuma.musicplayer.R;

public class TopBar extends LinearLayout {
    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.top_bar, this);
        // set container attributes
        setBackgroundResource(R.drawable.general_title_two);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dp2px(getContext(), 56)));
        int padding = com.shiro.tools.Utils.dp2px(getContext(), 9);
        setPadding(padding, padding, padding, padding);
        setGravity(Gravity.CENTER_VERTICAL);
        // set child view
        ImageView icon = (ImageView) findViewById(R.id.icon);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TopBar,
                0, 0);
        try {
            Drawable drawable = a.getDrawable(R.styleable.TopBar_android_src);
            if (drawable != null) {
                icon.setImageDrawable(drawable);
                icon.setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException ignored) {
        } finally {
            a.recycle();
        }
    }
}
