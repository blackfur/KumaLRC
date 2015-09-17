package com.shirokuma.musicplayer;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.shirokuma.musicplayer.Setting.MediaSetting;

public class GuideActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        // Load the ImageView that will host the animation and
        // set its background to our AnimationDrawable XML resource.
        final ImageView img = (ImageView) findViewById(R.id.screenshots);
        img.setImageResource(R.drawable.screen_shots);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) img.getDrawable();
        // Start the animation (looped playback by default).
        frameAnimation.start();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                img.clearAnimation();
                MediaSetting.getInstance(getApplicationContext()).setFirstInstall(false);
                finish();
            }
        });
    }
}
