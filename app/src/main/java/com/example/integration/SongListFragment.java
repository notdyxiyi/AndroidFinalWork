package com.example.integration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.integration.Adapter.MySongListAdapter;
import com.example.integration.entity.Song;

import java.util.ArrayList;

public class SongListFragment extends Fragment {
    private RecyclerView mRCVSongList;
    private MySongListAdapter mySongListAdapter;
    private ArrayList<Song> mSongArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_list, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        mRCVSongList = view.findViewById(R.id.rcv_song_list);
        mRCVSongList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initData() {
        mSongArrayList = new ArrayList<>();
        mSongArrayList.add(new Song("我会等.mp3", "i_will_wait.png"));
        mSongArrayList.add(new Song("如约而至.mp3", "promise.png"));
        mSongArrayList.add(new Song("凤凰花开的路口.mp3", "road.png"));
        mSongArrayList.add(new Song("山水之间.mp3", "landscapes.png"));

        mySongListAdapter = new MySongListAdapter(mSongArrayList, getContext());
        mySongListAdapter.setmItemClickListener(position -> {
            // 点击歌曲跳转到播放页面
            MusicPlayFragment playFragment = MusicPlayFragment.newInstance(mSongArrayList, position);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, playFragment)
                    .addToBackStack(null)
                    .commit();
        });

        mRCVSongList.setAdapter(mySongListAdapter);
    }
}