package com.shirokuma.musicplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashHandle implements java.lang.Thread.UncaughtExceptionHandler {
    Context context;

    public CrashHandle(Context c) {
        context = c;
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        log(Log.getStackTraceString(ex));
        new AlertDialog.Builder(context).setMessage(Log.getStackTraceString(ex)).setPositiveButton(android.R.string.ok, null).create().show();
    }

    // log
    static final String LOG_FILE = Environment.getExternalStorageDirectory() + "/org.june.android/log.txt";

    static {
        File logFile = new File(LOG_FILE);
        if (!logFile.exists()) {
            if (!logFile.getParentFile().exists())
                logFile.getParentFile().mkdirs();
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void log(String s) {
        File logFile = new File(LOG_FILE);
        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Date curDate = new Date(System.currentTimeMillis());
            buf.append(formatter.format(curDate));
            buf.newLine();
            buf.append(s);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
