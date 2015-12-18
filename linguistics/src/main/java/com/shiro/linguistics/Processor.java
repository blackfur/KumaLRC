package com.shiro.linguistics;

import android.os.Environment;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.shiro.memo.model.Entry;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processor {
    private static final String BACKUP = Environment.getExternalStorageDirectory() + "/Linguistics/database.dat";
    private static final String DATABASE = "/data/data/com.shiro.linguistics/databases/linguistics.db";
    private static final String IMPORT_DATA = Environment.getExternalStorageDirectory() + "/Linguistics/import.dat";
    static Pattern DELIMIT = Pattern.compile("[# \\s,.、，。。]");

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
                    if (line.length() < 2)
                        continue;
                    Matcher matcher = DELIMIT.matcher(line);
                    String content = null, note = null;
                    if (matcher.find()) {
                        content = line.substring(0, matcher.start());
                        note = line.substring(matcher.start() + 1);
                    }
                    if (content != null) {
                        From from = new Select().from(Entry.class).where("content = ?", content);
                        if (from.count() == 0) new Entry(content, note).save();
                    }
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
