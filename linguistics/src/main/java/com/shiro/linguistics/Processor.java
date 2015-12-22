package com.shiro.linguistics;

import android.os.Environment;

import java.io.IOException;

public class Processor {
    private static final String BACKUP = Environment.getExternalStorageDirectory() + "/Linguistics/database.dat";
    private static final String DATABASE = "/data/data/com.shiro.linguistics/databases/linguistics.db";

    public static boolean backup() {
        try {
            if (com.shiro.tools.Utils.copy(DATABASE, BACKUP)) return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean recover() {
        try {
            if (com.shiro.tools.Utils.copy(BACKUP, DATABASE)) return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
