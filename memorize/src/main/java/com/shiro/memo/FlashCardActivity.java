package com.shiro.memo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.activeandroid.query.Select;
import com.shiro.memo.model.Entry;

public class FlashCardActivity extends AppCompatActivity {
    int count;
    static final int DAILY_TASK = 32;
    Animation out2right, leftIn;
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);
        // init data
        count = new Setting(this).getCount();
        Entry entr = new Select().from(Entry.class).orderBy("RANDOM()").executeSingle();
        out2right = AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.out2right);
        out2right.setAnimationListener(onAnimat);
        leftIn = AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.left_in);
        // init view
        content = (TextView) findViewById(R.id.content);
        if (entr != null)
            content.setText(entr.content);
        for (View v : new View[]{content, findViewById(R.id.func)})
            v.setOnClickListener(onClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // backup
        com.shiro.memo.model.Util.export();
        // save task progress
        if (count < DAILY_TASK)
            new Setting(getContext()).setCount(count);
        else
            new Setting(getContext()).setCount(0);
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
        public void onClick(final View v) {
            int i = v.getId();
            if (i == R.id.content) {
                if (++count == DAILY_TASK) {
                    Util.alert(getContext(), "Daily task was completed, exit?", new Util.Listener() {
                        @Override
                        public Object process(Object msg) {
                            finish();
                            return null;
                        }
                    });
                    return;
                }
                v.startAnimation(out2right);
            } else if (i == R.id.func) {
                startActivity(new Intent(FlashCardActivity.this, FuncActivity.class));
            }
        }
    };
    Animation.AnimationListener onAnimat = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            final Entry entr = new Select().from(Entry.class).orderBy("RANDOM()").executeSingle();
            if (entr != null)
                content.setText(entr.content);
            content.startAnimation(leftIn);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    private Activity getContext() {
        return this;
    }
}
