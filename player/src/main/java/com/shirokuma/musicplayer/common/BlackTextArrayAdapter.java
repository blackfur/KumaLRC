package com.shirokuma.musicplayer.common;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BlackTextArrayAdapter extends ArrayAdapter<String> {
    public BlackTextArrayAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView text = (TextView) view.findViewById(android.R.id.text1);
        text.setTextColor(Color.BLACK);
        return view;
    }
}
