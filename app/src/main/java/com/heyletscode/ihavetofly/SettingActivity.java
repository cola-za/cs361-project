package com.heyletscode.ihavetofly;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();

        // เรื่อง exit dialog add เพราะเตรียมปิด activity ตอนกด back หน้า menu
        MainActivity.activityList.add(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);
        prefs = SettingActivity.this.getSharedPreferences("game", Context.MODE_PRIVATE);
        ImageButton imageButtonBack = findViewById(R.id.backButton);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,MainActivity.class);
                SettingActivity.this.startActivity(intent);
                finish(); // Avoid stacking activities
            }
        });
        SwitchCompat switchMusic = findViewById(R.id.switch_music);
        SwitchCompat switchSound = findViewById(R.id.switch_sound_effects);
        // Set initial states from SharedPreferences
        switchMusic.setChecked(!prefs.getBoolean("isMuteMusic", false));
        switchSound.setChecked(!prefs.getBoolean("isMute", false));
        // Listener for the Music Switch
        switchMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean("isMuteMusic", !b).apply();
                prefs.edit().apply();
            }
        });
        // Listener for the Sound Switch
        switchSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                prefs.edit().putBoolean("isMute", !b).apply();
                prefs.edit().apply();
            }
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