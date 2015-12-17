package com.shiro.linguistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.os.Handler;
import com.shirokuma.musicplayer.musiclib.MusicListActivity;

public class MainActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewid = v.getId();
            if (viewid == R.id.listen) {
                startActivity(new Intent(getContext(), MusicListActivity.class));
            } else if (viewid == R.id.word) {
                startActivity(new Intent(getContext(), com.shiro.memo.FlashCardActivity.class));
            }
        }
    };

    private void initView() {
        View listen = findViewById(R.id.listen);
        View word = findViewById(R.id.word);
        for (View v : new View[]{listen, word}) {
            v.setOnClickListener(onClick);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private Activity getContext() {
        return this;
    }
}
