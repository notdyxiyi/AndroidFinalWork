package com.example.integration.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.integration.R;
import com.example.integration.entity.Song;

import java.util.ArrayList;

public class MySongListAdapter extends RecyclerView.Adapter<MySongListAdapter.MySongItemViewHolder> {

    private ArrayList<Song> mSongArrayList;
    private Context mContext;
    private LinearLayout llContainer;

    private OnItemClickListener mItemClickListener;

    public MySongListAdapter() {}

    public MySongListAdapter(ArrayList<Song> mSongArrayList, Context mContext) {
        this.mSongArrayList = mSongArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MySongItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载列表项布局
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.fragment_song_item_layout, parent, false);
        return new MySongItemViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MySongItemViewHolder holder, int position) {
        Log.d("MySongListAdapter", "onBindViewHolder position: " + position);

        if (mSongArrayList == null || position < 0 || position >= mSongArrayList.size()) {
            return;
        }

        Song song = mSongArrayList.get(position);
        Log.d("MySongListAdapter", "绑定歌曲: " + song.getSongName() + ", 位置: " + position);

        if (holder.mTvSongName != null) {
            holder.mTvSongName.setText(song.getSongName());
        } else {
            Log.e("MySongListAdapter", "TextView 为 null!");
        }

        holder.itemView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("MySongListAdapter", "getItemCount: " + (mSongArrayList != null ? mSongArrayList.size() : 0));
        return mSongArrayList != null ? mSongArrayList.size() : 0;
    }

    class MySongItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvSongName;

        public MySongItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvSongName = itemView.findViewById(R.id.tv_song_name);
            llContainer = itemView.findViewById(R.id.ll_song_item_container);
        }

        public void bind(Song song) {
            mTvSongName.setText(song.getSongName());
        }
    }

    public OnItemClickListener getmItemClickListener() {
        return mItemClickListener;
    }

    public void setmItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}