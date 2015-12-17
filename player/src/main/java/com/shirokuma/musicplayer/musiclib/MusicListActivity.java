package com.shirokuma.musicplayer.musiclib;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.shirokuma.musicplayer.KumaPlayer;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.BindSrvOpMenusActivity;
import com.shirokuma.musicplayer.lyrics.LyricsActivity;
import com.shirokuma.musicplayer.model.Filter;
import com.shirokuma.musicplayer.setting.MediaSetting;

public class MusicListActivity extends BindSrvOpMenusActivity {
    Spinner mSpinner;
    ImageButton mBtnPlay, mBtnPause;
    TextView mName, mArtist;

    @Override
    protected int setContentViewRes() {
        return R.layout.activity_kuma_list;
    }

    @Override
    protected void initData() {
        super.initData();
    }

    private AdapterView.OnItemSelectedListener mItemSelectListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            int i1 = adapterView.getId();
            if (i1 == R.id.spinner) {
                if (view == null && i == -1 && l == -1) {
                    i = adapterView.getSelectedItemPosition();
                }
                switch (i) {
                    // song
                    case 0:
                        displayList(new Filter(Filter.FilterType.Song, null, null));
                        break;
                    // artist
                    case 1:
                        displayList(new Filter(Filter.FilterType.Artist, null, null));
                        break;
                    // album
                    case 2:
                        displayList(new Filter(Filter.FilterType.Album, null, null));
                        break;
                    // playlist
                    case 3:
                        displayList(new Filter(Filter.FilterType.Playlist, null, null));
                        break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            onItemSelected(adapterView, null, -1, -1);
        }
    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.info_layout) {
                startActivity(new Intent(getApplicationContext(), LyricsActivity.class));
            } else if (i == R.id.simple_ctrl_next) {
                mMusicSrv.playNext();
                if (mMusicSrv.getCurrentSong() != null) {
                    mName.setText(mMusicSrv.getCurrentSong().title);
                    mArtist.setText(mMusicSrv.getCurrentSong().artist);
                    mBtnPause.setVisibility(View.VISIBLE);
                    mBtnPlay.setVisibility(View.GONE);
                }
            } else if (i == R.id.simple_ctrl_prev) {
                mMusicSrv.playPrev();
                if (mMusicSrv.getCurrentSong() != null) {
                    mName.setText(mMusicSrv.getCurrentSong().title);
                    mArtist.setText(mMusicSrv.getCurrentSong().artist);
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

    @Override
    protected void initView() {
        super.initView();
        mName = (TextView) findViewById(R.id.name);
        mArtist = (TextView) findViewById(R.id.artist);
        mBtnPlay = (ImageButton) findViewById(R.id.simple_ctrl_play);
        mBtnPause = (ImageButton) findViewById(R.id.simple_ctrl_pause);
        findViewById(R.id.btn_home).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_back).setVisibility(View.GONE);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setVisibility(View.VISIBLE);
        mSpinner.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.info_layout).setOnClickListener(mOnClickListener);
                findViewById(R.id.simple_ctrl_prev).setOnClickListener(mOnClickListener);
                mBtnPlay.setOnClickListener(mOnClickListener);
                mBtnPause.setOnClickListener(mOnClickListener);
                findViewById(R.id.simple_ctrl_next).setOnClickListener(mOnClickListener);
                mSpinner.setOnItemSelectedListener(mItemSelectListener);
            }
        }, 200);
    }

    public void setAnimEnd(int[] endxy) {
        mArtist.getLocationOnScreen(endxy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMusicSrv != null && mMusicSrv.getCurrentSong() != null) {
            mName.setText(mMusicSrv.getCurrentSong().title);
            mArtist.setText(mMusicSrv.getCurrentSong().artist);
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
        if (mMusicSrv != null) {
            Filter filter = MediaSetting.getInstance(this).getLastFilter();
            mMusicSrv.setPlaySongs(filter.fetch(this));
            displayList(filter);
            if (mMusicSrv.getCurrentSong() != null) {
                mName.setText(mMusicSrv.getCurrentSong().title);
                mArtist.setText(mMusicSrv.getCurrentSong().artist);
                // restore last playback state
                mMusicSrv.restore();
            }
        }
    }

    public void displayList(Filter filter) {
        FragmentManager mgr = getFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        Fragment fragment = new MusiclistFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KumaPlayer.ARGUMENTS_KEY_FILTER, filter);
        fragment.setArguments(bundle);
        transaction.replace(R.id.music_list_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        super.onScanCompleted(path, uri);
        // when finish scanning, refresh song list
        displayList(new Filter(Filter.FilterType.Song, null, null));
    }

    @Override
    protected void onMusicNext() {
        if (mMusicSrv != null && mMusicSrv.getCurrentSong() != null) {
            mName.setText(mMusicSrv.getCurrentSong().title);
            mArtist.setText(mMusicSrv.getCurrentSong().artist);
        }
    }

    @Override
    protected void onMusicPlay() {
        if (mMusicSrv != null && mMusicSrv.getCurrentSong() != null) {
            mBtnPlay.setVisibility(View.GONE);
            mBtnPause.setVisibility(View.VISIBLE);
            mName.setText(mMusicSrv.getCurrentSong().title);
            mArtist.setText(mMusicSrv.getCurrentSong().artist);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1)
            super.onBackPressed();
    }
}
