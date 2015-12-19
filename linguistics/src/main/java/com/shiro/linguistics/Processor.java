package com.shiro.linguistics;

import android.os.Environment;
import com.shiro.memo.Memorize;

import java.io.IOException;

public class Processor {
    private static final String BACKUP = Environment.getExternalStorageDirectory() + "/Linguistics/database.dat";
    private static final String DATABASE = "/data/data/com.shiro.linguistics/databases/linguistics.db";
    private static final String IMPORT_DATA = Environment.getExternalStorageDirectory() + "/Linguistics/import.dat";

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

    public static boolean feed() {
        return Memorize.feed(IMPORT_DATA);
    }

    public static boolean vomit() {
        return Memorize.vomit(IMPORT_DATA);
    }
}
