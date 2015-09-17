package com.shirokuma.musicplayer.list;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.common.Filter;
import com.shirokuma.musicplayer.common.Utils;
import com.shirokuma.musicplayer.playback.Album;
import com.shirokuma.musicplayer.playback.Artist;

import java.util.ArrayList;

public class MusiclistFragment extends Fragment {
    MusicListActivity main;
    private ListView mListView;
    float[] mStartXY;
    int[] mEndXY;
    View mMusicNote;
    private ArrayList mDisplayMusic;
    Animation.AnimationListener mAnimListener;
    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            switch (v.getId()) {
                case R.id.music_list:
                    mStartXY[0] = e.getX();
                    mStartXY[1] = e.getY();
                    break;
            }
            return false;
        }
    };
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.music_list:
                    switch ((Filter.FilterType) view.getTag(R.id.TAG_KEY_TYPE)) {
                        case Song:
                            // set play list
                            main.getMusicSrv().setPlaySongs(mDisplayMusic);
                            // play song
                            main.getMusicSrv().playSong(position);
                            // animation
                            AnimationSet mAnimDrop = new AnimationSet(true);
                            mAnimDrop.setFillAfter(false);
                            mAnimDrop.setFillEnabled(false);
                            mAnimDrop.setAnimationListener(mAnimListener);
                            TranslateAnimation translateAnimationX = new TranslateAnimation(mStartXY[0], mEndXY[0], 0, 0);
                            translateAnimationX.setDuration(1000);
                            TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0, mStartXY[1], mEndXY[1]);
                            translateAnimationY.setDuration(1000);
                            AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.0f);
                            alphaAnim.setDuration(800);
                            mAnimDrop.addAnimation(translateAnimationY);
                            mAnimDrop.addAnimation(translateAnimationX);
                            mAnimDrop.addAnimation(alphaAnim);
                            mMusicNote.startAnimation(mAnimDrop);
                            break;
                        case Album:
                            main.displayList(new Filter(Filter.FilterType.Song, ((Album) mDisplayMusic.get(position)).album, null));
                            break;
                        case Artist:
                            main.displayList(new Filter(Filter.FilterType.Song, null, ((Artist) mDisplayMusic.get(position)).artist));
                            break;
                    }
                    break;
            }
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        main = (MusicListActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEndXY = new int[2];
        main.setAnimEnd(mEndXY);
        mStartXY = new float[2];
        if (getArguments() != null) {
            Filter filter = getArguments().getParcelable(Utils.ARGUMENTS_KEY_FILTER);
            mDisplayMusic = filter.fetch(getActivity());
            switch (filter.type) {
                case Song:
                    mAnimListener = new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mMusicNote.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mMusicNote.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    };
                    break;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_music_list, container, false);
        // filtrate media
        if (getArguments() != null) {
            Filter filter = getArguments().getParcelable(Utils.ARGUMENTS_KEY_FILTER);
            if (filter != null) {
                switch (filter.type) {
                    case Song:
                        mMusicNote = root.findViewById(R.id.music_note);
                        break;
                }
            }
        }
        mListView = (ListView) root.findViewById(R.id.music_list);
        mListView.setAdapter(new MusicAdapter(getActivity(), mDisplayMusic));
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.setOnTouchListener(mTouchListener);
        return root;
    }
}