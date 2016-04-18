package com.shirokuma.musicplayer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.shirokuma.musicplayer.common.BindMusicSrvActivity;
import com.shirokuma.musicplayer.lyrics.LyricsActivity;
import com.shirokuma.musicplayer.musiclib.ClassifyFragment;
import com.shirokuma.musicplayer.musiclib.Filter;
import com.shirokuma.musicplayer.musiclib.MusiclistFragment;
import com.shirokuma.musicplayer.musiclib.ScanActivity;
import com.shirokuma.musicplayer.setting.MediaSetting;
import com.shirokuma.musicplayer.setting.TimerActivity;

public class MainActivity extends BindMusicSrvActivity {
    static final String TAG = "#org.june.android#";
    //    Spinner mSpinner;
    ImageButton mBtnPlay, mBtnPause;
    TextView mName, mArtist;
    ProgressDialog progress;
    protected int setContentViewRes() {
        return R.layout.activity_kuma_list;
    }

    protected void initView() {
        super.initView();
        mName = (TextView) findViewById(R.id.name);
        mArtist = (TextView) findViewById(R.id.artist);
        mBtnPlay = (ImageButton) findViewById(R.id.simple_ctrl_play);
        mBtnPause = (ImageButton) findViewById(R.id.simple_ctrl_pause);
//        findViewById(R.id.btn_home).setVisibility(View.VISIBLE);
//        findViewById(R.id.btn_back).setVisibility(View.GONE);
//        mSpinner = (Spinner) findViewById(R.id.spinner);
//        mSpinner.setVisibility(View.VISIBLE);
        findViewById(R.id.info_layout).setOnClickListener(mOnClickListener);
        findViewById(R.id.simple_ctrl_prev).setOnClickListener(mOnClickListener);
        mBtnPlay.setOnClickListener(mOnClickListener);
        mBtnPause.setOnClickListener(mOnClickListener);
        findViewById(R.id.simple_ctrl_next).setOnClickListener(mOnClickListener);
//        mSpinner.setOnItemSelectedListener(mItemSelectListener);
        progress = ProgressDialog.show(this, "", "loading", false, true);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.info_layout) {
                startActivity(new Intent(getApplicationContext(), LyricsActivity.class));
            } else if (i == R.id.simple_ctrl_next) {
                mMusicSrv.playNext();
                if (mMusicSrv.getCurrentSong() != null) {
                    mName.setText(mMusicSrv.getCurrentSong().head());
                    mArtist.setText(mMusicSrv.getCurrentSong().subhead());
                    mBtnPause.setVisibility(View.VISIBLE);
                    mBtnPlay.setVisibility(View.GONE);
                }
            } else if (i == R.id.simple_ctrl_prev) {
                mMusicSrv.playPrev();
                if (mMusicSrv.getCurrentSong() != null) {
                    mName.setText(mMusicSrv.getCurrentSong().head());
                    mArtist.setText(mMusicSrv.getCurrentSong().subhead());
                    mBtnPause.setVisibility(View.VISIBLE);
                    mBtnPlay.setVisibility(View.GONE);
                }
            } else if (i == R.id.simple_ctrl_play) {
                mBtnPause.setVisibility(View.VISIBLE);
                mBtnPlay.setVisibility(View.GONE);
                mMusicSrv.play();
            } else if (i == R.id.simple_ctrl_pause) {
                mBtnPlay.setVisibility(View.VISIBLE);
                mBtnPause.setVisibility(View.GONE);
                mMusicSrv.pause();
            }
        }
    };

    public void setAnimEnd(int[] endxy) {
        mArtist.getLocationOnScreen(endxy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMusicSrv != null && mMusicSrv.getCurrentSong() != null) {
            mName.setText(mMusicSrv.getCurrentSong().head());
            mArtist.setText(mMusicSrv.getCurrentSong().subhead());
            if (mMusicSrv.isPlaying()) {
                mBtnPause.setVisibility(View.VISIBLE);
                mBtnPlay.setVisibility(View.GONE);
            } else {
                mBtnPause.setVisibility(View.GONE);
                mBtnPlay.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onMusicSrvConnected() {
        progress.dismiss();
        if (mMusicSrv != null) {
            Filter filter = MediaSetting.getInstance(this).getLastFilter();
            mMusicSrv.setPlaySongs(filter.fetch());
//            displayList(filter);
            if (mMusicSrv.getCurrentSong() != null) {
                mName.setText(mMusicSrv.getCurrentSong().head());
                mArtist.setText(mMusicSrv.getCurrentSong().subhead());
                // restore last playback state
                mMusicSrv.restore();
            }
// display categories
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            Fragment fragment = new ClassifyFragment();
            transaction.replace(R.id.music_list_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void displayList(Filter filter) {
        FragmentManager mgr = getSupportFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();
//        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        Fragment fragment = new MusiclistFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PlayerEnv.ARGUMENTS_KEY_FILTER, filter);
        fragment.setArguments(bundle);
        transaction.replace(R.id.music_list_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        super.onScanCompleted(path, uri);
        // when finish scanning, refresh song list
        displayList(new Filter(Filter.FilterType.Song));
    }

    @Override
    protected void onMusicNext() {
        if (mMusicSrv != null && mMusicSrv.getCurrentSong() != null) {
            mName.setText(mMusicSrv.getCurrentSong().head());
            mArtist.setText(mMusicSrv.getCurrentSong().subhead());
        }
    }

    @Override
    protected void onMusicPlay() {
        if (mMusicSrv != null && mMusicSrv.getCurrentSong() != null) {
            mBtnPlay.setVisibility(View.GONE);
            mBtnPause.setVisibility(View.VISIBLE);
            mName.setText(mMusicSrv.getCurrentSong().head());
            mArtist.setText(mMusicSrv.getCurrentSong().subhead());
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            super.onBackPressed();
    }

    AlertDialog menu;

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            android.util.Log.i(PlayerEnv.TAG, "onKeyDown: KeyEvent.KEYCODE_MENU");
            if (menu == null) {
                MenuListView m = new MenuListView(this);
                menu = new AlertDialog.Builder(this).setView(m).create();
            }
            menu.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    class MenuListView extends ListView {

        public MenuListView(Context c) {
            super(c);
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
                    convertView.setTag(holder);
                } else {
                    holder = (OptionHolder) convertView.getTag();
                }
                holder.content.setText((String) getItem(position));
                return convertView;
            }
        }

        AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menu.dismiss();
                Log.e(PlayerEnv.TAG, "==== on item click ====");
                if (position == 0) {
                    Log.e(PlayerEnv.TAG, "==== click scan ====");
                    if (getMusicSrv() != null && getMusicSrv().isPlaying())
                        getMusicSrv().stop();
                    startActivity(new Intent(getContext(), ScanActivity.class));
                } else if (position == 1) {
                    Log.e(PlayerEnv.TAG, "==== click play order ====");
                    MediaSetting setting = MediaSetting.getInstance(getContext());
                    Boolean oldConfig = setting.getShuffle();
                    getMusicSrv().setShuffle(!oldConfig);
                    setting.setShuffle(!oldConfig);
                } else if (position == 2) {
                    Log.e(PlayerEnv.TAG, "==== click sleep mode ====");
                    startActivity(new Intent(getContext(), TimerActivity.class));
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

    static class OptionHolder {
        TextView content;
    }
}
