package com.shirokuma.musicplayer.musiclib;

import android.graphics.Bitmap;

public interface Music {
   Bitmap icon();
   String head();
   String subhead();
   String remark();
   Filter.FilterType type();
}
