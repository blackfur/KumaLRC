package com.shiro.memo.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Entries")
public class Entry extends Model {
    @Column(name = "Content", index = true)
    public String content;
    @Column(name = "Note", index = true)
    public String note;
    @Column(name = "Proficiency", index = true)
    public int proficiency;

    public Entry() {
        super();
    }

    public Entry(String cont) {
        super();
        content = cont;
    }

    public Entry(String cont, String n) {
        super();
        content = cont;
        note = n;
    }
}
