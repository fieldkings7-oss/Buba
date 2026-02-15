package com.example.pvz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Убрать title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        
        setContentView(R.layout.activity_menu);
        
        // Получить и отобразить монеты
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        int coins = prefs.getInt("coins", 0);
        
        TextView coinsDisplay = findViewById(R.id.coins_display);
        coinsDisplay.setText("Монеты: " + coins);
        
        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, SelectMapActivity.class);
            startActivity(intent);
        });
        
        Button shopButton = findViewById(R.id.shop_button);
        shopButton.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ShopActivity.class);
            startActivity(intent);
        });
        
        // Запустить музыку
        startMusic();
    }
    
    private void startMusic() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.menu_music);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
