package com.shiro.memo.model;

import android.os.Environment;

import java.io.IOException;

public class Util {
    private static final String BACKUP = Environment.getExternalStorageDirectory() + "/ShiroMemo.dat";
    private static final String DATABASE = "/data/data/com.shiro.memo/databases/memo.db";

    public static boolean export() {
        try {
            if (com.shiro.memo.Util.copy(DATABASE, BACKUP)) return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean restore() {
        try {
            if (com.shiro.memo.Util.copy(BACKUP, DATABASE)) return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
