package com.example.integration.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Song implements  Parcelable {

    // 添加获取文件名（去掉扩展名）的方法
    public String getSongNameWithoutExtension() {
        if (songName == null) return "";
        int lastDot = songName.lastIndexOf('.');
        if (lastDot > 0) {
            return songName.substring(0, lastDot);
        }
        return songName;
    }

    // 如果封面路径为空，默认使用歌曲名.png
    public String getCoverFileName() {
        if (coverPath != null && !coverPath.isEmpty()) {
            return coverPath;
        }
        return getSongNameWithoutExtension() + ".png";
    }


    private String songName;
    private String coverPath;

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public Song(String songName, String coverPath) {
        this.songName = songName;
        this.coverPath = coverPath;
    }

    public Song(){}

    protected Song(Parcel in) {
        songName = in.readString();
        coverPath = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", coverPath='" + coverPath + '\'' +
                '}';
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeString(songName);
        dest.writeString(coverPath);
    }
}
