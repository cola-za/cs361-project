package com.heyletscode.ihavetofly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LevelSelectActivity extends AppCompatActivity {

    public static final int FLAG_EASY=0,FLAG_NORMAL=1,FLAG_HARD=2;
    public static final String DIFFICULT="difficult";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();

        MainActivity.activityList.add(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_level_select);

        prefs = getSharedPreferences("game", Context.MODE_PRIVATE);

        ImageButton imageButtonBack = findViewById(R.id.backButton);
        imageButtonBack.setOnClickListener(view -> {
            startActivity(new Intent(LevelSelectActivity.this, MainActivity.class));
            finish(); // Avoid stacking activities
        });

        final Intent intent = new Intent(LevelSelectActivity.this, GameActivity.class);

        findViewById(R.id.easy).setOnClickListener(view -> {
            saveDifficultFlag(FLAG_EASY);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.normal).setOnClickListener(view -> {
            saveDifficultFlag(FLAG_NORMAL);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.hard).setOnClickListener(view -> {
            saveDifficultFlag(FLAG_HARD);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // เรื่อง exit dialog ลบเพราะ destroy อยู่แล้ว
        MainActivity.activityList.remove(this);
    }

    private void saveDifficultFlag(int difficultFlag) {
        new Thread(() -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(DIFFICULT, difficultFlag);
            editor.apply();
        }).start();
    }

    // Method to load the language from SharedPreferences
    private void loadLanguage() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String langCode = prefs.getString("language", ""); // Default to English if no language is set
        Locale newLocale = new Locale(langCode);
        Configuration config = getResources().getConfiguration();
        if(!langCode.isEmpty()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(newLocale);
            } else {
                config.locale = newLocale;
            }
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
    }
}