package com.shirokuma.musicplayer.common;

import android.os.Bundle;
import android.view.View;
import com.shirokuma.musicplayer.R;

// activity with back navigation
public abstract class NavBackActivity extends BaseActivity {
    View mBtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected View.OnClickListener mBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void initView() {
        mBtnBack = findViewById(R.id.btn_back);
        mBtnBack.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBtnBack.setOnClickListener(mBtnListener);
            }
        }, 200);
    }
}
