package com.shiro.memo;

import android.app.Application;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.shiro.memo.model.Entry;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Memorize {
    static Pattern DELIMIT = Pattern.compile("[# \\s,.、，。。]");

    public static void init(Application a) {
        ActiveAndroid.initialize(a);
        Setting setting = new Setting(a.getApplicationContext());
        if (setting.isFirst()) {
            setting.setFirst(false);
            // insert default values
            if (new Select().from(Entry.class).count() == 0) {
                String[] words = a.getResources().getStringArray(R.array.words);
                for (String w : words)
                    new Entry(w).save();
            }
        }
    }

    public static boolean feed(String path) {
        File importDat = new File(path);
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

    public static boolean vomit(String path) {
        List<Entry> entries = new Select().from(Entry.class).execute();
        if (entries != null && entries.size() > 0) {
            try {
                // append mode
                BufferedWriter bw = new BufferedWriter(new FileWriter(path, true));
                for (Entry e : entries) {
                    bw.write(e.content + '#' + e.note);
                    bw.newLine();
                }
                bw.flush();
                bw.close();
                return true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return false;
    }
}