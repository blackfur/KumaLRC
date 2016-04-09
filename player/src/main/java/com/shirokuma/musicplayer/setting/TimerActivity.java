package com.shirokuma.musicplayer.setting;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.shirokuma.musicplayer.PlayerEnv;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BlackTextArrayAdapter;

public class TimerActivity extends Activity {
    int mMinutes = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        ListView timerList = (ListView) findViewById(R.id.timer_list);
        timerList.setAdapter(new BlackTextArrayAdapter(this, android.R.layout.simple_list_item_single_choice, getResources().getStringArray(R.array.timer_list)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        });
        timerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                switch (i) {
                    case 1:
                        mMinutes = 10;
                        break;
                    case 2:
                        mMinutes = 30;
                        break;
                    case 3:
                        mMinutes = 60;
                        break;
                    case 4:
                        mMinutes = 90;
                        break;
                    default:
                        mMinutes = -1;
                }
                PlayerEnv.sleepMode(mMinutes);
                if (mMinutes > 0)
                    Toast.makeText(getApplicationContext(), getString(R.string.shutdown) + String.valueOf(mMinutes), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), getString(R.string.cancel_sleep), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}