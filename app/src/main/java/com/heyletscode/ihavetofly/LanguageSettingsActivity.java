package com.heyletscode.ihavetofly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class LanguageSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        setContentView(R.layout.activity_language_settings);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // ปุ่มย้อนกลับ
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());

        // ปุ่มเปลี่ยนภาษาเป็นภาษาอังกฤษ
        findViewById(R.id.btnEnglish).setOnClickListener(view -> setLanguage("en"));

        // ปุ่มเปลี่ยนภาษาเป็นภาษาไทย
        findViewById(R.id.btnThai).setOnClickListener(view -> setLanguage("th"));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setLanguage(String langCode) {
        Locale newLocale = new Locale(langCode);
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(newLocale);
        } else {
            config.locale = newLocale;
        }
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // บันทึกการตั้งค่าภาษาใน SharedPreferences
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", langCode);
        editor.apply();

        // โหลด MainActivity ใหม่เพื่อให้ภาษาเปลี่ยนแปลงทันที
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
