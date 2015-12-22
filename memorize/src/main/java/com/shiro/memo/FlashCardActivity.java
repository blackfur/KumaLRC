package com.shiro.memo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.activeandroid.query.Select;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;
import com.nineoldandroids.animation.AnimatorSet;
import com.shiro.memo.model.Entry;
import com.shiro.tools.Listener;

public class FlashCardActivity extends Activity {
    int count;
    static final int DAILY_TASK = 32;
    Animation out2right, leftIn;
    AnimatorSet z0toz90, z90toz0;
    View easy, hard;
    TextView content;
    Entry currentEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);
        // init data
        count = new Setting(this).getCount();
        currentEntry = new Select().from(Entry.class).orderBy("proficiency asc").executeSingle();
        out2right = AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.out2right);
        out2right.setAnimationListener(onAnimat);
        leftIn = AnimationUtils.loadAnimation(FlashCardActivity.this, R.anim.left_in);
        // init view
        content = (TextView) findViewById(R.id.content);
        z0toz90 = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.turn_over_shrink);
        z0toz90.setTarget(content);
        z0toz90.addListener(nineoldOnAnimat);
        z90toz0 = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.turn_over_manify);
        z90toz0.setTarget(content);
        easy = findViewById(R.id.easy);
        hard = findViewById(R.id.hard);
        if (currentEntry != null) {
            content.setText(currentEntry.content);
        }
        for (View v : new View[]{easy, hard, content})
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
            if (count == DAILY_TASK) {
                count++;
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
                if (nineoldOnAnimat.callback == null || nineoldOnAnimat.callback == callNext || nineoldOnAnimat.callback == callQuestion) {
                    nineoldOnAnimat.callback = callAnswer;
                    z0toz90.start();
                } else if (nineoldOnAnimat.callback == callAnswer) {
                    nineoldOnAnimat.callback = callQuestion;
                    z0toz90.start();
                }
            } else if (i == R.id.easy) {
                currentEntry.proficiency += 2;
                next();
            } else if (i == R.id.hard) {
                currentEntry.proficiency += 1;
                next();
            }
        }
    };

    private void next() {
        currentEntry.save();
        count++;
        onAnimat.callback = callNext;
        content.startAnimation(out2right);
    }

    NineOldAnimatorListenerWithCallback nineoldOnAnimat = new NineOldAnimatorListenerWithCallback();

    class NineOldAnimatorListenerWithCallback implements Animator.AnimatorListener {
        public Listener callback;

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (callback != null)
                callback.process();
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    }

    AnimationListenerWithCallback onAnimat = new AnimationListenerWithCallback();

    class AnimationListenerWithCallback implements Animation.AnimationListener {
        public Listener callback;

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (callback != null)
                callback.process();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    private Listener callNext = new Listener() {
        @Override
        public Object process(Object... pars) {
            currentEntry = new Select().from(Entry.class).orderBy("proficiency asc").executeSingle();
//            final Entry entr = new Select().from(Entry.class).orderBy("RANDOM()").executeSingle();
            if (currentEntry != null) {
                content.setText(currentEntry.content);
            }
            content.startAnimation(leftIn);
            return null;
        }
    };
    private Listener callAnswer = new Listener() {
        @Override
        public Object process(Object... pars) {
            content.append(" " + currentEntry.note);
            z90toz0.start();
            return null;
        }
    };
    private Listener callQuestion = new Listener() {
        @Override
        public Object process(Object... pars) {
            content.setText(currentEntry.content);
            z90toz0.start();
            return null;
        }
    };

    private Activity getContext() {
        return this;
    }
}
