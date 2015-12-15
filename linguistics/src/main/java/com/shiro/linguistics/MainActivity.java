package com.shiro.linguistics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.shirokuma.musicplayer.musiclib.MusicListActivity;

public class MainActivity extends AppCompatActivity {
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
            }else if(viewid == R.id.word)
            {

                startActivity(new Intent(getContext(), MusicListActivity.class));
            }
        }
    };

    private void initView() {
        View listen = findViewById(R.id.listen);
        for (View v : new View[]{listen}) {
            listen.setOnClickListener(onClick);
        }
    }

    private Activity getContext() {
        return this;
    }
}
