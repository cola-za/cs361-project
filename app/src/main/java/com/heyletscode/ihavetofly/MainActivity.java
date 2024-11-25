package com.heyletscode.ihavetofly;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;
import android.net.Uri;
import android.view.ViewGroup;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
public class MainActivity extends AppCompatActivity {
    public static List<Activity> activityList = new ArrayList<>();
    private boolean isMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        activityList.add(this);

        // ซ่อนแถบสถานะ (Status Bar)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        // ปุ่มตั้งค่าภาษา
        Button lang = findViewById(R.id.button);
        lang.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LanguageSettingsActivity.class)));

        // ปุ่มไปที่หน้า Setting
        Button setting = findViewById(R.id.btn_sound_settings);
        setting.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SettingActivity.class)));

        // การแสดงข้อความชื่อเกมด้วยสีเกรเดียนต์
        TextView textView = findViewById(R.id.gameTitle);
        textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int colorStart = ContextCompat.getColor(MainActivity.this, R.color.gold);
                int colorEnd = ContextCompat.getColor(MainActivity.this, R.color.red_tomato);
                LinearGradient gradient = new LinearGradient(0, 0, textView.getWidth(), 0, new int[]{colorStart, colorEnd}, null, Shader.TileMode.CLAMP);
                textView.getPaint().setShader(gradient);
                textView.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        // ปุ่มเปิดคู่มือเกม
        findViewById(R.id.rule).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GameRulesActivity.class);
            startActivity(intent);
        });

        // ปุ่มเล่นเกม
        findViewById(R.id.play).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LevelSelectActivity.class)));

        // การแสดงคะแนนสูงสุดจาก SharedPreferences
        TextView highScoreTxt = findViewById(R.id.highScoreTxt);
        SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        String highScoreText = getString(R.string.high_score) + prefs.getInt("highscore", 0);
        highScoreTxt.setText(highScoreText);

        isMute = prefs.getBoolean("isMute", false);

        // การตั้งค่า VideoView สำหรับวิดีโอพื้นหลัง
        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.backgroundd);
        videoView.setVideoURI(videoUri);
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        videoView.setLayoutParams(layoutParams);

        videoView.setOnPreparedListener(mp -> {
            videoView.start();
            mp.setLooping(true);
            if (isMute) {
                mp.setVolume(0, 0);
            } else {
                mp.setVolume(1, 1);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        isMute = prefs.getBoolean("isMute", false);
    }

    @Override
    public void onBackPressed() {
        // แสดง Dialog ยืนยันการออกจากแอป
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.exit_dialog_custom, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        Button positiveButton = dialogView.findViewById(R.id.btnPositive);
        positiveButton.setOnClickListener(v -> {
            for (Activity activity : activityList) {
                activity.finish();
            }
        });

        Button negativeButton = dialogView.findViewById(R.id.btnNegative);
        negativeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        // ตั้งค่าให้ dialog ขึ้นตรงกลาง
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);  // ตั้งให้อยู่ตรงกลาง
            WindowManager.LayoutParams params = window.getAttributes();
            params.y = 0;  // ปรับแกน Y เพื่อให้ dialog ขึ้นตรงกลาง
            window.setAttributes(params);
            window.setWindowAnimations(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
