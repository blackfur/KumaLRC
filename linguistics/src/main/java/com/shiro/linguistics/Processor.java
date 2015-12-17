package com.shiro.linguistics;

import android.os.Environment;
import android.text.TextUtils;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.shiro.memo.model.Entry;

import java.io.*;

public class Processor {
    private static final String BACKUP = Environment.getExternalStorageDirectory() + "/Linguistics/database.dat";
    private static final String DATABASE = "/data/data/com.shiro.linguistics/databases/linguistics.db";
    private static final String IMPORT_DATA = Environment.getExternalStorageDirectory() + "/Linguistics/import.dat";
    private static final String DELIMIT = "#";

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

    public static boolean importDat() {
        File importDat = new File(IMPORT_DATA);
        if (importDat.exists()) {
            ActiveAndroid.beginTransaction();
            try {
                BufferedReader br = new BufferedReader(new FileReader(importDat));
                for (String line; (line = br.readLine()) != null; ) {
                    if (line.length() < 2 || TextUtils.equals(line, DELIMIT))
                        continue;
                    From from = new Select().from(Entry.class).where("content = ?", line);
                    if (from.count() == 0) new Entry(line).save();
                }
                br.close();
                ActiveAndroid.setTransactionSuccessful();
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
        return false;
    }
}
