package com.shiro.memo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.activeandroid.query.Select;
import com.shiro.memo.model.Entry;
import com.shiro.tools.Listener;

public class FlashCardActivity extends AppCompatActivity {
    int count;
    static final int DAILY_TASK = 32;
    Animation out2right, leftIn;
    View easy, hard, next;
    TextView content, answer;
    Entry currentEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);
        // init data
        count = new Setting(this).getCount();
//        Entry entr = new Select().from(Entry.class).orderBy("RANDOM()").executeSingle();
        currentEntry = new Select().from(Entry.class).orderBy("proficiency asc").executeSingle();
        out2right = AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.out2right);
        out2right.setAnimationListener(onAnimat);
        leftIn = AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.left_in);
        // init view
        content = (TextView) findViewById(R.id.content);
        answer= (TextView) findViewById(R.id.answer);
        easy = findViewById(R.id.easy);
        hard = findViewById(R.id.hard);
        next = findViewById(R.id.next);
        if (currentEntry != null) {
            content.setText(currentEntry.content);
            answer.setText(currentEntry.content + "\n");
        }
        for (View v : new View[]{easy, hard, next})
            v.setOnClickListener(onClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            if (++count == DAILY_TASK) {
                com.shiro.tools.Utils.alert(getContext(), "Daily task was completed, exit?", new Listener() {
                    @Override
                    public Object process(Object... msg) {
                        finish();
                        return null;
                    }
                });
                return;
            }
            int i = v.getId();
            if (i == R.id.content) {
            } else if (i == R.id.easy) {
                currentEntry.proficiency += 2;
                currentEntry.save();
                answer.setVisibility(View.VISIBLE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
            } else if (i == R.id.hard) {
                currentEntry.proficiency += 1;
                currentEntry.save();
                answer.setVisibility(View.VISIBLE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
            } else if (i == R.id.next) {
                next.setVisibility(View.GONE);
                easy.setVisibility(View.VISIBLE);
                hard.setVisibility(View.VISIBLE);
                answer.setVisibility(View.INVISIBLE);
                content.startAnimation(out2right);
            }
        }
    };
    Animation.AnimationListener onAnimat = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            currentEntry = new Select().from(Entry.class).orderBy("proficiency asc").executeSingle();
//            final Entry entr = new Select().from(Entry.class).orderBy("RANDOM()").executeSingle();
            if (currentEntry != null) {
                content.setText(currentEntry.content);
                answer.setText(currentEntry.content + "\n" + currentEntry.note);
            }
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
