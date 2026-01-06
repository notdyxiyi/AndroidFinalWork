package com.example.integration.service;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


import com.example.integration.entity.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MyMusicService extends Service {
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mSongArrayList;
    private int curSongIndex = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        mSongArrayList = new ArrayList<>();

        // 设置播放完成监听器，实现自动播放下一首
        mMediaPlayer.setOnCompletionListener(mp -> {
            next(); // 当前歌曲播放完成后自动播放下一首
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyMusicBind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public class MyMusicBind extends Binder {
        public MyMusicService getService() {
            return MyMusicService.this;
        }

        public void updateMusicList(ArrayList<Song> songList) {
            MyMusicService.this.updateMusicList(songList);
        }

        public void updateCurrentMusicIndex(int idx) {
            MyMusicService.this.updateCurrentMusicIndex(idx);
        }

        public boolean isPlaying() {
            return MyMusicService.this.isPlaying();
        }

        public void pause() {
            MyMusicService.this.pause();
        }

        public void play() {
            MyMusicService.this.play();
        }

        public void previous() {
            MyMusicService.this.previous();
        }

        public void next() {
            MyMusicService.this.next();
        }


        public Song getCurSong() {
            return MyMusicService.this.getCurSong();
        }
    }


    public Song getCurSong() {
        if (mSongArrayList == null || mSongArrayList.isEmpty() ||
                curSongIndex < 0 || curSongIndex >= mSongArrayList.size()) {
            return null;
        }
        return mSongArrayList.get(curSongIndex);
    }

    public void previous() {
        if (mSongArrayList == null || mSongArrayList.isEmpty()) {
            return;
        }
        curSongIndex = (curSongIndex - 1 + mSongArrayList.size()) % mSongArrayList.size();
        updateCurrentMusicIndex(curSongIndex);
    }

    public void next() {
        if (mSongArrayList == null || mSongArrayList.isEmpty()) {
            return;
        }
        curSongIndex = (curSongIndex + 1) % mSongArrayList.size();
        updateCurrentMusicIndex(curSongIndex);
    }

    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public void updateMusicList(ArrayList<Song> songArrayList) {
        if (songArrayList == null) {
            this.mSongArrayList = new ArrayList<>();
        } else {
            this.mSongArrayList = new ArrayList<>(songArrayList);
        }
    }

    public void updateCurrentMusicIndex(int idx) {
        // 安全检查
        if (mSongArrayList == null || mSongArrayList.isEmpty()) {
            Log.e("MyMusicService", "歌曲列表为空或未初始化");
            return;
        }

        if (idx < 0 || idx >= mSongArrayList.size()) {
            Log.e("MyMusicService", "索引越界: " + idx + ", 列表大小: " + mSongArrayList.size());
            idx = 0; // 重置为0避免崩溃
        }

        curSongIndex = idx;
        Song song = mSongArrayList.get(curSongIndex);
        if (song == null) {
            Log.e("MyMusicService", "歌曲对象为null，索引: " + idx);
            return;
        }

        String songName = song.getSongName();
        Log.d("MyMusicService", "准备播放歌曲: " + songName);

        AssetManager assetManager = getAssets();
        try {
            // 重置播放器
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();

            // 从assets加载音乐文件
            AssetFileDescriptor fileDescriptor = assetManager.openFd(songName);
            mMediaPlayer.setDataSource(
                    fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength()
            );
            fileDescriptor.close();

            mMediaPlayer.prepare();
            mMediaPlayer.start();
            Log.d("MyMusicService", "开始播放: " + songName);

        } catch (IOException e) {
            Log.e("MyMusicService", "播放音乐失败: " + songName, e);
        }
    }
}