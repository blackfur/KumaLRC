package com.shiro.linguistics;

import android.app.Application;
import com.shiro.memo.Memorize;
import com.shirokuma.musicplayer.PlayerEnv;

public class Linguistics {
    public static void init(Application a) {
        PlayerEnv.init(a);
        Memorize.init(a);
        // create shortcuts
    }
}
