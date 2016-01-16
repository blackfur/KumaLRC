package com.shirokuma.musicplayer.model;

import android.graphics.Bitmap;
import com.shirokuma.musicplayer.musiclib.Filter;

public interface Music {
   Bitmap icon();
   String head();
   String subhead();
   String remark();
   Filter.FilterType type();
}
