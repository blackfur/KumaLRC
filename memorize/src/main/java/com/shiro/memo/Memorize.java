package com.shiro.memo;

import android.app.Application;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.shiro.memo.model.Entry;

public class Memorize {
    public static void init(Application a) {
        ActiveAndroid.initialize(a);
        Setting setting = new Setting(a.getApplicationContext());
        if (setting.isFirst()) {
            setting.setFirst(false);
            // insert default values
            if (new Select().from(Entry.class).count() == 0) {
                String[] words = a.getResources().getStringArray(R.array.words);
                for (String w : words)
                    new Entry(w).save();
            }
        }
    }
}
