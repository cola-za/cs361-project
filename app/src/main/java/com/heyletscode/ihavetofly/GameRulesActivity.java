package com.heyletscode.ihavetofly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

public class GameRulesActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();

        // เรื่อง exit dialog add เพราะเตรียมปิด activity ตอนกด back หน้า menu
        MainActivity.activityList.add(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_rule);

        prefs = GameRulesActivity.this.getSharedPreferences("game", Context.MODE_PRIVATE);
        ImageButton imageButtonBack = findViewById(R.id.backButton);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameRulesActivity.this,MainActivity.class);
             GameRulesActivity.this.startActivity(intent);
             finish(); // Avoid stacking activities
            }
        });


        // การหาข้อความในแต่ละ TextView โดยการใช้ id ที่กำหนดใน XML
        TextView ruleTop = findViewById(R.id.rule_top);
        TextView ruleIntro = findViewById(R.id.rule_intro);
        TextView rule1 = findViewById(R.id.rule1);
        TextView rule2 = findViewById(R.id.rule2);
        TextView rule3 = findViewById(R.id.rule3);
        TextView rule4 = findViewById(R.id.rule4);
        TextView rule5 = findViewById(R.id.rule5);

        // กำหนดข้อความให้กับแต่ละ TextView
        ruleTop.setText(getString(R.string.game_rule));
        ruleIntro.setText(getString(R.string.intro_rule));
        rule1.setText(getString(R.string.rule1));
        rule2.setText(getString(R.string.rule2));
        rule3.setText(getString(R.string.rule3));
        rule4.setText(getString(R.string.rule4));
        rule5.setText(getString(R.string.rule5));

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

