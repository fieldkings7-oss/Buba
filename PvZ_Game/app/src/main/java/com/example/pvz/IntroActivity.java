package com.example.pvz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Убрать title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        
        // Полный экран без системного UI (кнопки, статус бар)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        
        setContentView(R.layout.activity_intro);

        videoView = findViewById(R.id.video_view);

        // Получить видео из raw папки
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro);
        videoView.setVideoURI(videoUri);

        // Показать видео в полный экран
        videoView.setOnCompletionListener(mediaPlayer -> {
            // Когда видео закончилось, перейти в MenuActivity
            startActivity(new Intent(IntroActivity.this, MenuActivity.class));
            finish();
        });

        // Если видео не может быть воспроизведено, перейти в MenuActivity
        videoView.setOnErrorListener((mp, what, extra) -> {
            startActivity(new Intent(IntroActivity.this, MenuActivity.class));
            finish();
            return true;
        });

        videoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}
