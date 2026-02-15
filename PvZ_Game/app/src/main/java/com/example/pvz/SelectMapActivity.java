package com.example.pvz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SelectMapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Убрать title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        
        setContentView(R.layout.activity_select_map);
        
        Button buttonDefaultMap = findViewById(R.id.button_default_map);
        Button buttonPreparedYard = findViewById(R.id.button_prepared_yard);
        ImageButton buttonBack = findViewById(R.id.button_back_to_menu);
        
        // Проверить куплена ли Prepared yard
        SharedPreferences prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        boolean hasPreparedYard = prefs.getBoolean("has_prepared_yard", false);
        
        // Отключить кнопку если не куплена
        buttonPreparedYard.setEnabled(hasPreparedYard);
        buttonPreparedYard.setAlpha(hasPreparedYard ? 1.0f : 0.5f);
        
        buttonDefaultMap.setOnClickListener(v -> {
            Intent intent = new Intent(SelectMapActivity.this, MainActivity.class);
            intent.putExtra("map_type", "default");
            startActivity(intent);
            finish();
        });
        
        buttonPreparedYard.setOnClickListener(v -> {
            Intent intent = new Intent(SelectMapActivity.this, MainActivity.class);
            intent.putExtra("map_type", "prepared_yard");
            startActivity(intent);
            finish();
        });
        
        buttonBack.setOnClickListener(v -> {
            startActivity(new Intent(SelectMapActivity.this, MenuActivity.class));
            finish();
        });
    }
}
