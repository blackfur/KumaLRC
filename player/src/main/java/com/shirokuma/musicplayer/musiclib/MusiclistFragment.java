package com.shirokuma.musicplayer.musiclib;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import com.activeandroid.query.Delete;
import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.shiro.tools.Utils;
import com.shirokuma.musicplayer.KumaPlayer;
import com.shirokuma.musicplayer.R;
import com.shirokuma.musicplayer.model.Album;
import com.shirokuma.musicplayer.model.Artist;
import com.shirokuma.musicplayer.model.Folder;
import com.shirokuma.musicplayer.model.Song;
import com.shirokuma.musicplayer.view.TouchSwipeListView;

import java.io.File;
import java.util.ArrayList;

public class MusiclistFragment extends Fragment {
    MusicListActivity main;
    private TouchSwipeListView mListView;
    float[] mStartXY;
    int[] mEndXY;
    View mMusicNote;
    private ArrayList mDisplayMusic;
    Animation.AnimationListener mAnimListener;
    View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStartXY[0] = e.getX();
                    mStartXY[1] = e.getY();
                    break;
            }
            return false;
        }
    };
    private Filter filter;

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
            filter = getArguments().getParcelable(KumaPlayer.ARGUMENTS_KEY_FILTER);
            mDisplayMusic = filter.fetch();
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
            if (filter != null) {
                switch (filter.type) {
                    case Song:
                        // initial animation material
                        mMusicNote = root.findViewById(R.id.music_note);
                        break;
                }
            }
        }
        mListView = (TouchSwipeListView) root.findViewById(R.id.music_list);
        MusicAdapter adapter = new MusicAdapter(getActivity(), mDisplayMusic);
//        adapter.setOnTouchListener(mTouchListener);
        adapter.setCallback(this);
        mListView.setAdapter(adapter);
        return root;
    }

    private int mLastSelectedPosition;
    private BaseSwipeListViewListener mSwipeListener = new BaseSwipeListViewListener() {
        @Override
        public void onOpened(int position, boolean toRight) {
            mLastSelectedPosition = mListView.getFirstVisiblePosition();
        }

        @Override
        public void onClickFrontView(int position) {
            switch (filter.type) {
                case Song:
                    // set play list
                    main.getMusicSrv().setFilter(filter);
                    main.getMusicSrv().setPlaySongs(mDisplayMusic);
                    // play song
                    main.getMusicSrv().play(position);
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
                    main.displayList(new Filter(Filter.FilterType.Song, ((Album) mDisplayMusic.get(position)).title, null));
                    break;
                case Artist:
                    main.displayList(new Filter(Filter.FilterType.Song, null, ((Artist) mDisplayMusic.get(position)).name));
                    break;
                case Folder:
                    main.displayList(new Filter(((Folder) mDisplayMusic.get(position))));
                    break;
            }
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView.setCustomOnTouchListener(mTouchListener);
        mListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        mListView.setSwipeCloseAllItemsWhenMoveList(true);
        mListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL); //there are four swipe actions
        mListView.setOffsetLeft(Utils.dp2px(this.getActivity(), 256f)); // left side offset
        mListView.setAnimationTime(32); // Animation time
        mListView.setSwipeListViewListener(mSwipeListener);
    }

    public void delete(int position) {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading));
        final Song song = (Song) mDisplayMusic.get(position);
        // delete on database
//        getActivity().getContentResolver().delete(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.songid), null, null);
        new Delete().from(Song.class).where("path=?", song.path).execute();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList newDisplayMusic = filter.fetch();
                mListView.post(new Runnable() {
                    @Override
                    public void run() {
                        // adjust playback if current displayed list is playing list
                        if (main.getMusicSrv().getPlaySongs() == mDisplayMusic) {
                            if (main.getMusicSrv().isPlaying() && main.getMusicSrv().getCurrentSong().path.equals(song.path))
                                if (mDisplayMusic.size() > 1)
                                    main.getMusicSrv().playNext();
                                else
                                    main.getMusicSrv().stop();
                            main.getMusicSrv().setPlaySongs(newDisplayMusic);
                        }
                        mDisplayMusic = newDisplayMusic;
                        // update play list
                        MusicAdapter adapter = new MusicAdapter(getActivity(), mDisplayMusic);
                        adapter.setCallback(MusiclistFragment.this);
                        mListView.setAdapter(adapter);
                        mListView.setSelection(mLastSelectedPosition);
                        // delete on storage
                        File file = new File(song.path);
                        if (file.exists())
                            file.delete();
                        dialog.dismiss();
                    }
                });
            }
        }).start();
    }
}