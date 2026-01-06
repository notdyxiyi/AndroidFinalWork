package com.example.integration;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.integration.Adapter.MySongListAdapter;
import com.example.integration.entity.GlobalConstants;
import com.example.integration.entity.Song;
import com.example.integration.service.MyMusicService;

import java.util.ArrayList;

public class MusicPlayFragment extends Fragment {
    private View view;
    private ImageView ivPlayOrPause, ivPre, ivNext, ivCover;
    private TextView tvTitle;
    private RecyclerView mRCVSongList;


    private Song mCurSong;
    private ArrayList<Song> mSongArrayList;
    private int curSongIndex;
    private MyMusicService mMusicService;
    private MyMusicService.MyMusicBind myMusicBind;

    public static MusicPlayFragment newInstance(ArrayList<Song> songList, int songIndex) {
        MusicPlayFragment fragment = new MusicPlayFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(GlobalConstants.KEY_SONG_LIST, songList);
        args.putInt(GlobalConstants.KEY_SONG_INDEX, songIndex);
        fragment.setArguments(args);
        return fragment;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myMusicBind = (MyMusicService.MyMusicBind) service;
            mMusicService = myMusicBind.getService();

            if (mMusicService != null) {
                if (mSongArrayList != null) {
                    ArrayList<Song> validSongs = new ArrayList<>();
                    for (Song song : mSongArrayList) {
                        if (song != null && song.getSongName() != null) {
                            validSongs.add(song);
                        }
                    }

                    if (!validSongs.isEmpty()) {
                        mMusicService.updateMusicList(validSongs);
                        int validIndex = Math.max(0, Math.min(curSongIndex, validSongs.size() - 1));
                        mMusicService.updateCurrentMusicIndex(validIndex);
                        mCurSong = myMusicBind.getCurSong();
                        updateUI();
                        myMusicBind.play();
                        ivPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
                    } else {
                        Log.e("MusicPlayActivity", "没有有效的歌曲数据");
                        Toast.makeText(requireContext(), "歌曲数据异常", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Log.e("MusicPlayActivity", "Service为null");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMusicService = null;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_play, container, false);
        this.view = view;

        initView();
        if (getArguments() != null) {
            curSongIndex = getArguments().getInt(GlobalConstants.KEY_SONG_INDEX, 0);
            mSongArrayList = getArguments().getParcelableArrayList(GlobalConstants.KEY_SONG_LIST);
        } else {
            initData();
        }

        if (mSongArrayList != null && curSongIndex < mSongArrayList.size()) {
            mCurSong = mSongArrayList.get(curSongIndex);
        }

        Log.d("MusicPlayFragment", "接收到的索引: " + curSongIndex);
        if (mSongArrayList != null) {
            Log.d("MusicPlayFragment", "接收到的歌曲列表大小: " + mSongArrayList.size());
            for (int i = 0; i < mSongArrayList.size(); i++) {
                Song song = mSongArrayList.get(i);
                if (song != null) {
                    Log.d("MusicPlayFragment", "歌曲 " + i + ": " + song.getSongName());
                } else {
                    Log.e("MusicPlayFragment", "歌曲 " + i + ": null");
                }
            }
        } else {
            Log.e("MusicPlayFragment", "歌曲列表为null");
        }

        updateTitle();
        startMusicService();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivPlayOrPause.setOnClickListener(v -> playOrPause());
        ivPre.setOnClickListener(v -> preMusic());
        ivNext.setOnClickListener(v -> nextMusic());
    }

    private void initData() {
        mSongArrayList = new ArrayList<>();
        mSongArrayList.add(new Song("我会等.mp3", "i_will_wait.png"));
        mSongArrayList.add(new Song("如约而至.mp3", "promise.png"));
        mSongArrayList.add(new Song("凤凰花开的路口.mp3", "road.png"));
        mSongArrayList.add(new Song("山水之间.mp3", "landscapes.png"));
        for (int i = 0; i < mSongArrayList.size(); i++) {
            Song song = mSongArrayList.get(i);
            if (song == null || song.getSongName() == null) {
                Log.e("MainActivity", "第 " + i + " 首歌曲数据异常");
            }
        }
    }

    private void updateTitle() {
        if (mCurSong != null) {
            tvTitle.setText(mCurSong.getSongName());
        }
    }

    private void updateUI() {
        if (mCurSong != null) {
            tvTitle.setText(mCurSong.getSongName());
            String coverFileName = mCurSong.getCoverFileName();
            if (coverFileName != null && !coverFileName.isEmpty()) {
                setCoverImage(coverFileName);
            } else {
                ivCover.setImageResource(R.drawable.i_will_wait);
            }
        }
    }

    private void setCoverImage(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            String imageName = fileName.replace(".png", "")
                    .replace(".jpg", "")
                    .replace(".jpeg", "");

            int resId = getResources().getIdentifier(imageName, "drawable", requireContext().getPackageName());

            if (resId != 0) {
                ivCover.setImageResource(resId);
            } else {
                Log.e("MusicPlayFragment", "找不到图片资源: " + imageName);
                ivCover.setImageResource(R.drawable.i_will_wait);
            }
        } else {
            ivCover.setImageResource(R.drawable.i_will_wait);
        }
    }

    public void nextMusic() {
        if (myMusicBind != null) {
            myMusicBind.next();
            mCurSong = myMusicBind.getCurSong();
            ivPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
            tvTitle.setText(mCurSong.getSongName());
            setCoverImage(mCurSong.getCoverFileName());
        }
    }

    private void initView() {
        ivPlayOrPause = view.findViewById(R.id.playOrpause);
        ivNext = view.findViewById(R.id.next);
        ivPre = view.findViewById(R.id.previous);
        tvTitle = view.findViewById(R.id.tv_music_title);
        ivCover = view.findViewById(R.id.cover_image);
        mRCVSongList = view.findViewById(R.id.rcv_song_list);

        if (mRCVSongList != null) {
            mRCVSongList.setVisibility(View.GONE);
        }
    }

    public void preMusic() {
        if (myMusicBind != null) {
            myMusicBind.previous();
            mCurSong = myMusicBind.getCurSong();
            ivPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
            tvTitle.setText(mCurSong.getSongName());
            setCoverImage(mCurSong.getCoverFileName());
        }
    }

    public void playOrPause() {
        if (myMusicBind.isPlaying()) {
            myMusicBind.pause();
            ivPlayOrPause.setImageResource(android.R.drawable.ic_media_play);
        } else {
            myMusicBind.play();
            ivPlayOrPause.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void startMusicService() {
        Intent intent = new Intent(requireContext(), MyMusicService.class);
        requireContext().bindService(intent, conn, requireContext().BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            requireContext().unbindService(conn);
        }
    }
}