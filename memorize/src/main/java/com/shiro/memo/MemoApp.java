package com.shiro.memo;

import android.app.Application;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.shiro.memo.model.Entry;

public class MemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
        Setting setting = new Setting(getApplicationContext());
        if (setting.isFirst()) {
            com.shiro.memo.model.Util.restore();
            setting.setFirst(false);
            // insert default values
            From from = new Select().from(Entry.class);
            final int count = from.count();
            if (count == 0) {
                String[] words = getResources().getStringArray(R.array.words);
                for (String w : words)
                    new Entry(w).save();
            }
        }
    }
}
