package com.shiro.linguistics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.shirokuma.musicplayer.musiclib.MusicListActivity;

public class MainActivity extends AppCompatActivity {
    ProgressDialog progress;
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
            } else if (viewid == R.id.import_data) {
                progress = com.shiro.tools.Utils.loading(getContext(), progress, R.string.loading);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Processor.feed())
                            com.shiro.tools.Utils.uitoast(getContext(), getString(R.string.success));
                        else
                            com.shiro.tools.Utils.uitoast(getContext(), getString(R.string.fail));
                        com.shiro.tools.Utils.dismiss(getContext(), progress);
                    }
                }).start();
            } else if (viewid == R.id.backup) {
                if (Processor.backup()) Toast.makeText(getContext(), R.string.success, Toast.LENGTH_LONG).show();
                else Toast.makeText(getContext(), R.string.fail, Toast.LENGTH_LONG).show();
            } else if (viewid == R.id.restore) {
                if (Processor.recover()) Toast.makeText(getContext(), R.string.success, Toast.LENGTH_LONG).show();
                else Toast.makeText(getContext(), R.string.fail, Toast.LENGTH_LONG).show();
            } else if (viewid == R.id.redact) {
                startActivity(new Intent(getContext(), com.shiro.memo.RedactActivity.class));
            } else if (viewid == R.id.export) {
                if (Processor.vomit())
                    Toast.makeText(getContext(), R.string.success, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getContext(), R.string.fail, Toast.LENGTH_LONG).show();
            }
        }
    };

    private void initView() {
        View listen = findViewById(R.id.listen);
        View word = findViewById(R.id.word);
        for (View v : new View[]{listen, word, findViewById(R.id.import_data), findViewById(R.id.redact), findViewById(R.id.restore), findViewById(R.id.backup),findViewById(R.id.export)}) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Processor.backup();
    }

    private Activity getContext() {
        return this;
    }
}
