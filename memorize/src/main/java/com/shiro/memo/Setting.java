package com.shiro.memo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Setting {
    private final String KEY_FIRST = "first";
    // daily task, initial as 32
    private final String KEY_COUNT = "count";
    private SharedPreferences mSharedPre;
    private SharedPreferences.Editor editor;

    public Setting(Context context) {
        mSharedPre = context.getSharedPreferences(context.getPackageName(), Application.MODE_PRIVATE);
    }

    public void setFirst(boolean b) {
        setValue(KEY_FIRST, b);
    }

    public void setCount(int num) {
        setValue(KEY_COUNT, num);
    }

    public Boolean isFirst() {
        return mSharedPre.getBoolean(KEY_FIRST, true);
    }

    public int getCount() {
        return mSharedPre.getInt(KEY_COUNT, 0);
    }

    private Object getValue(String key, Class c) {
        if (c.equals(Boolean.class))
            return mSharedPre.getBoolean(key, false);
        else if (c.equals(String.class)) {
            return mSharedPre.getString(key, null);
        }
        return null;
    }

    private void setValue(String key, Object value) {
        editor = mSharedPre.edit();
        if (value instanceof String)
            editor.putString(key, (String) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);
        else if (value instanceof Integer)
            editor.putInt(key, (Integer) value);
        editor.commit();
    }
}
