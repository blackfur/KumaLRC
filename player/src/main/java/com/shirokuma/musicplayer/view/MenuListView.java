package com.shirokuma.musicplayer.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.shirokuma.musicplayer.MainActivity;
import com.shirokuma.musicplayer.PlayerEnv;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.musiclib.ScanActivity;
import com.shirokuma.musicplayer.setting.MediaSetting;
import com.shirokuma.musicplayer.setting.TimerActivity;

public class MenuListView extends ListView {
    MainActivity context;

    public MenuListView(Context c) {
        super(c);
        context = (MainActivity) c;
        // init menu
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setAdapter(new OptionAdapter());
        setOnItemClickListener(onItemClick);
    }

    class OptionAdapter extends BaseAdapter {
        String[] options;

        public OptionAdapter() {
            options = getResources().getStringArray(R.array.options);
        }

        @Override
        public int getCount() {
            return options.length;
        }

        @Override
        public Object getItem(int position) {
            if (position < options.length)
                return options[position];
            else return "";
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            OptionHolder holder;
            if (convertView == null) {
                holder = new OptionHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_item, parent, false);
                holder.content = (TextView) convertView.findViewById(R.id.content);
//                holder.content.setOnClickListener(onClick);
//                holder.content.setTag(R.id.TAG_OPTION, position);
                convertView.setTag(holder);
            } else {
                holder = (OptionHolder) convertView.getTag();
            }
            holder.content.setText((String) getItem(position));
            return convertView;
        }
    }

    static class OptionHolder {
        TextView content;
    }

    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e(PlayerEnv.TAG, "==== on item click ====");
            if (position == 0) {
                Log.e(PlayerEnv.TAG, "==== click scan ====");
                if (context.getMusicSrv() != null && context.getMusicSrv().isPlaying())
                    context.getMusicSrv().stop();
                context.startActivity(new Intent(context, ScanActivity.class));
            } else if (position == 1) {
                Log.e(PlayerEnv.TAG, "==== click play order ====");
                MediaSetting setting = MediaSetting.getInstance(context);
                Boolean oldConfig = setting.getShuffle();
                context.getMusicSrv().setShuffle(!oldConfig);
                setting.setShuffle(!oldConfig);
            } else if (position == 2) {
                Log.e(PlayerEnv.TAG, "==== click sleep mode ====");
                context.startActivity(new Intent(context, TimerActivity.class));
            } else if (position == 3) {
                Log.e(PlayerEnv.TAG, "==== click exit ====");
                PlayerEnv.exit();
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            // The great Android "hackatlon", the love, the magic.
            // The two leftmost bits in the height measure spec have
            // a special meaning, hence we can't use them to describe height.
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        } else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
