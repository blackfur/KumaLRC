package com.shirokuma.musicplayer.common;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Menu;

import java.io.*;
import java.lang.reflect.Method;

public class Utils {
    public static final String ARGUMENTS_KEY_FILTER = "filter";
    public static final int SEEK_INTERVAL = 1000;
    public static final String WEBCHAT_APP_ID = "wx88888888";

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

    public static String file2str(String path, String encode) {
        BufferedReader reader;
        try {
            FileInputStream stream = new FileInputStream(path);
            reader = new BufferedReader(new InputStreamReader(stream, encode == null ? "GB2312" : encode));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }
}
