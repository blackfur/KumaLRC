package com.shirokuma.musicplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.fortysevendeg.swipelistview.SwipeListView;

public class TouchSwipeListView extends SwipeListView {
    View.OnTouchListener mCustomOnTouch;

    public TouchSwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mCustomOnTouch != null)
            mCustomOnTouch.onTouch(this, event);
        return super.dispatchTouchEvent(event);
    }

    public void setCustomOnTouchListener(View.OnTouchListener listener) {
        mCustomOnTouch = listener;
    }
}
