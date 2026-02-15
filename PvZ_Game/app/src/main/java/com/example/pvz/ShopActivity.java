package com.example.pvz;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private TextView coinsDisplay;
    private boolean hasBoomCherry = false;
    private boolean hasPreparedYard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Убрать title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        
        setContentView(R.layout.activity_shop);
        
        prefs = getSharedPreferences("game_data", MODE_PRIVATE);
        
        coinsDisplay = findViewById(R.id.coins_display);
        updateCoinsDisplay();
        
        hasBoomCherry = prefs.getBoolean("has_boomcherry", false);
        hasPreparedYard = prefs.getBoolean("has_prepared_yard", false);
        
        // Close button
        ImageButton closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> finish());
        
        // Promo code button
        Button promoButton = findViewById(R.id.promo_button);
        promoButton.setOnClickListener(v -> showPromoDialog());
        
        // BoomCherry card
        Button boomcherryButton = findViewById(R.id.boomcherry_button);
        if (hasBoomCherry) {
            boomcherryButton.setText("КУПЛЕНО");
            boomcherryButton.setEnabled(false);
        } else {
            boomcherryButton.setText("КУПИТЬ (100 монет)");
            boomcherryButton.setOnClickListener(v -> {
                int coins = prefs.getInt("coins", 0);
                if (coins >= 100) {
                    // Покупка
                    prefs.edit()
                            .putInt("coins", coins - 100)
                            .putBoolean("has_boomcherry", true)
                            .commit();
                    
                    hasBoomCherry = true;
                    boomcherryButton.setText("КУПЛЕНО");
                    boomcherryButton.setEnabled(false);
                    updateCoinsDisplay();
                    Toast.makeText(ShopActivity.this, "Успешно куплено!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopActivity.this, "Недостаточно монет!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        
        // Prepared Yard card
        Button preparedYardButton = findViewById(R.id.prepared_yard_button);
        if (hasPreparedYard) {
            preparedYardButton.setText("КУПЛЕНО");
            preparedYardButton.setEnabled(false);
        } else {
            preparedYardButton.setText("КУПИТЬ (50 монет)");
            preparedYardButton.setOnClickListener(v -> {
                int coins = prefs.getInt("coins", 0);
                if (coins >= 50) {
                    // Покупка
                    prefs.edit()
                            .putInt("coins", coins - 50)
                            .putBoolean("has_prepared_yard", true)
                            .commit();
                    
                    hasPreparedYard = true;
                    preparedYardButton.setText("КУПЛЕНО");
                    preparedYardButton.setEnabled(false);
                    updateCoinsDisplay();
                    Toast.makeText(ShopActivity.this, "Успешно куплено!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ShopActivity.this, "Недостаточно монет!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    private void showPromoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите промокод");
        
        EditText input = new EditText(this);
        input.setHint("Код");
        builder.setView(input);
        
        builder.setPositiveButton("Применить", (dialog, which) -> {
            String code = input.getText().toString().trim();
            if (code.equals("ADMINONLY666")) {
                int coins = prefs.getInt("coins", 0);
                prefs.edit().putInt("coins", coins + 200).commit();
                updateCoinsDisplay();
                Toast.makeText(ShopActivity.this, "+200 монет! Код применен!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ShopActivity.this, "Неверный код!", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        
        builder.show();
    }
    
    private void updateCoinsDisplay() {
        int coins = prefs.getInt("coins", 0);
        coinsDisplay.setText("Монеты: " + coins);
    }
}
