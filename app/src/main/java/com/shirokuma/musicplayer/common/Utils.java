package com.shirokuma.musicplayer.common;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Menu;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Scanner;

public class Utils {
    public static final int STANDARD_SCREEN_WIDTH = 480;
    //    public static final int STANDARD_SCREEN_HEIGHT = 800;
    public static final int STANDARD_WEST_FONT_SIZE = 22;
    public static final int STANDARD_EAST_FONT_SIZE = 20;
    public static final int STANDARD_WEST_FONT_LINE_LENGTH = 36;
    public static final int STANDARD_EAST_FONT_LINE_LENGTH = 21;
    public static final String ARGUMENTS_KEY_FILTER = "filter";
    public static final int SEEK_INTERVAL = 1000;
    public static final String ACTION_MEDIA_SCANNER_SCAN_DIR = "android.intent.action.MEDIA_SCANNER_SCAN_DIR";

    public static void setOptionMenuIconEnable(Menu menu, boolean enable) {
        try {
            //未知的类
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int dp2px(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public static String file2str(String path) {
        String content = null;
        try {
            content = new Scanner(new File(path)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return content;
    }
}
