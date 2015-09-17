package com.shirokuma.musicplayer.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.Music;

import java.util.ArrayList;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 * 
 * Sue Smith - February 2014
 */

public class MusicAdapter extends BaseAdapter {
    //head list and layout
    private ArrayList data;
    private LayoutInflater mInflater;
    private View.OnTouchListener mOnTouchListener;

    //constructor
    public MusicAdapter(Context c, ArrayList theSongs) {
        data = theSongs;
        mInflater = LayoutInflater.from(c);
    }

    public void setOnTouchListener(View.OnTouchListener listener) {
        mOnTouchListener = listener;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //get head using position
        Music item = (Music) data.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            //map to head layout
            convertView = mInflater.inflate(R.layout.music, parent, false);
            //get title and subhead views
            holder.head = (TextView) convertView.findViewById(R.id.head);
            holder.subhead = (TextView) convertView.findViewById(R.id.subhead);
            if (mOnTouchListener != null)
                convertView.setOnTouchListener(mOnTouchListener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //get title and subhead strings
        holder.head.setText(item.head());
        if (item.subhead() != null) {
            holder.subhead.setText(item.subhead());
            holder.subhead.setVisibility(View.VISIBLE);
        } else
            holder.subhead.setVisibility(View.INVISIBLE);
        //set position as tag
        return convertView;
    }

    public static class ViewHolder {
        TextView head, subhead;
    }
}