package com.shirokuma.musicplayer.model;

public class Category {
    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle == null ? "" : subtitle;
    }

    int icon;
    String title, subtitle;

    public Category(int i, String t, String s) {
        icon = i;
        title = t;
        subtitle = s;
    }
}
