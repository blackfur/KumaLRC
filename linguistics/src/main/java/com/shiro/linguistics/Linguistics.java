package com.shiro.linguistics;

import android.app.Application;
import com.shiro.memo.Memorize;
import com.shirokuma.musicplayer.KumaPlayer;

public class Linguistics {
    public static void init(Application a) {
        KumaPlayer.init(a);
        Memorize.init(a);
        // create shortcuts
    }
}
