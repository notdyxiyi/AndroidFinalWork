package com.example.integration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.integration.entity.Song;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment musicPlayFragment, calcuatorFragment, badHabitsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化Fragment
        musicPlayFragment = MusicPlayFragment.newInstance(getDefaultSongs(), 0);
        calcuatorFragment = new CacluatorFragment();
        badHabitsFragment = new BadHabitsFragment();

        // 默认显示首页
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SongListFragment())
                .commit();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        int itemId = item.getItemId();
                        if (itemId == R.id.music_play) {
                            selectedFragment = musicPlayFragment;
                        } else if (itemId == R.id.calcuate) {
                            selectedFragment = calcuatorFragment;
                        } else if (itemId == R.id.records) {
                            selectedFragment = badHabitsFragment;

                        }

                        if (selectedFragment != null) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, selectedFragment)
                                    .commit();
                            return true;
                        }
                        return false;
                    }
                });
    }

    private ArrayList<Song> getDefaultSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song("我会等.mp3", "i_will_wait.png"));
        songs.add(new Song("如约而至.mp3", "promise.png"));
        songs.add(new Song("凤凰花开的路口.mp3", "road.png"));
        songs.add(new Song("山水之间.mp3", "landscapes.png"));
        return songs;
    }
}