package com.shirokuma.musicplayer.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Menu;

import java.io.*;
import java.lang.reflect.Method;

public class Utils {
    public static final String ARGUMENTS_KEY_FILTER = "filter";
    public static final int SEEK_INTERVAL = 1000;
    public static final String WEBCHAT_APPID = "wx8c97b24c8801739a";
    public static final String MORSE_CODE_BOOK[] = {"·-", "-···", "-·-·", "-··", "·", "··-·",
            "--·", "····", "··", "·---", "-·-", "·-··", "--", "-·",
            "---", "·--·", "--·-", "·-·", "···", "-", "··-", "···-",
            "·--", "-··-", "-·--", "--··"
    };

    public static void setOptionMenuIconEnable(Menu menu, boolean enable) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static String str2morse(String text) {
        StringBuffer buf = new StringBuffer(text.length() * 3);
        int count = 0;
        for (char c : text.toUpperCase().toCharArray()) {
            if (count++ > 0) buf.append(", ");
            buf.append(MORSE_CODE_BOOK[c - 'A']);
        }
        return buf.toString();
    }
}
