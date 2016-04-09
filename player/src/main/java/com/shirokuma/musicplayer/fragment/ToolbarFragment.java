package com.shirokuma.musicplayer.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.shiro.tools.Utils;
import com.shirokuma.musicplayer.PlayerEnv;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BindMusicSrvActivity;
import com.shirokuma.musicplayer.musiclib.ScanActivity;
import com.shirokuma.musicplayer.setting.MediaSetting;
import com.shirokuma.musicplayer.setting.TimerActivity;

public class ToolbarFragment extends Fragment {
    ListView list;
    BindMusicSrvActivity hostActivity;
    PopupWindow popupWindow;

    public void onAttach(Context context) {
        super.onAttach(context);
        hostActivity = (BindMusicSrvActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // initial view
        Log.e(PlayerEnv.TAG, "==== toolbar fragment: onCreateView ====");
        View body = inflater.inflate(R.layout.fragment_toolbar, container, false);
        View tools = body.findViewById(R.id.tool);
        tools.setOnClickListener(onClick);
        return body;
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e(PlayerEnv.TAG, "==== on click ====");
            if (v.getId() == R.id.tool) {
                Log.e(PlayerEnv.TAG, "==== show options ====");
                if (list == null) {
                    list = new ListView(getActivity());
//                    String[] options = getResources().getStringArray(R.array.options);
//                    list.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, options));
                    list.setAdapter(new OptionAdapter());
//                    list.setClickable(true);
                    Log.e(PlayerEnv.TAG, "==== list created ====");
//                    list.setFocusable(true);
                    list.setOnItemClickListener(onItemClick);
                }
                if (popupWindow == null) {
                    popupWindow = Utils.showPopupWindow(list, v);
                    ViewGroup.LayoutParams params = list.getLayoutParams();
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    list.setLayoutParams(params);
//                    Utils.setListViewHeightBasedOnChildren(list);
                } else {
                    popupWindow.showAtLocation(v.getRootView(), Gravity.CENTER, 0, 0);
                }
            }
//            else if (v.getId() == R.id.content) {
//                Log.e(PlayerEnv.TAG, "==== click options ====");
//                int position = (Integer) v.getTag(R.id.TAG_OPTION);
//                if (position == 0) {
//                    Log.e(PlayerEnv.TAG, "==== click scan ====");
//                    if (hostActivity.getMusicSrv() != null && hostActivity.getMusicSrv().isPlaying())
//                        hostActivity.getMusicSrv().stop();
//                    startActivity(new Intent(getActivity(), ScanActivity.class));
//                } else if (position == 1) {
//                    Log.e(PlayerEnv.TAG, "==== click play order ====");
//                    MediaSetting setting = MediaSetting.getInstance(getActivity());
//                    Boolean oldConfig = setting.getShuffle();
//                    hostActivity.getMusicSrv().setShuffle(!oldConfig);
//                    setting.setShuffle(!oldConfig);
//                } else if (position == 2) {
//                    Log.e(PlayerEnv.TAG, "==== click sleep mode ====");
//                    startActivity(new Intent(getActivity(), TimerActivity.class));
//                } else if (position == 3) {
//                    Log.e(PlayerEnv.TAG, "==== click exit ====");
//                    PlayerEnv.exit();
//                }
//                popupWindow.dismiss();
//            }
        }
    };
    AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e(PlayerEnv.TAG, "==== on item click ====");
            if (position == 0) {
                Log.e(PlayerEnv.TAG, "==== click scan ====");
                if (hostActivity.getMusicSrv() != null && hostActivity.getMusicSrv().isPlaying())
                    hostActivity.getMusicSrv().stop();
                startActivity(new Intent(getActivity(), ScanActivity.class));
            } else if (position == 1) {
                Log.e(PlayerEnv.TAG, "==== click play order ====");
                MediaSetting setting = MediaSetting.getInstance(getActivity());
                Boolean oldConfig = setting.getShuffle();
                hostActivity.getMusicSrv().setShuffle(!oldConfig);
                setting.setShuffle(!oldConfig);
            } else if (position == 2) {
                Log.e(PlayerEnv.TAG, "==== click sleep mode ====");
                startActivity(new Intent(getActivity(), TimerActivity.class));
            } else if (position == 3) {
                Log.e(PlayerEnv.TAG, "==== click exit ====");
                PlayerEnv.exit();
            }
            popupWindow.dismiss();
        }
    };

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
}
