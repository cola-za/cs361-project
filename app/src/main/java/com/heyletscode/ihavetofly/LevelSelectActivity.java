package com.heyletscode.ihavetofly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectActivity extends AppCompatActivity {

    public static final int FLAG_EASY=0,FLAG_NORMAL=1,FLAG_HARD=2;
    public static final String DIFFICULT="difficult";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // เรื่อง exit dialog add เพราะเตรียมปิด activity ตอนกด back หน้า menu
        MainActivity.activityList.add(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_level_select);

        prefs = LevelSelectActivity.this.getSharedPreferences("game", Context.MODE_PRIVATE);

        ImageButton imageButtonBack = findViewById(R.id.backButton);
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LevelSelectActivity.this,MainActivity.class);
                LevelSelectActivity.this.startActivity(intent);
            }
        });

        final Intent intent = new Intent(LevelSelectActivity.this,GameActivity.class);

        TableRow easy = findViewById(R.id.easy);
        TableRow normal = findViewById(R.id.normal);
        TableRow hard = findViewById(R.id.hard);

        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDifficultFlag(FLAG_EASY);
                LevelSelectActivity.this.startActivity(intent);
            }
        });

        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDifficultFlag(FLAG_NORMAL);
                LevelSelectActivity.this.startActivity(intent);
            }
        });

        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDifficultFlag(FLAG_HARD);
                LevelSelectActivity.this.startActivity(intent);
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

    private void saveDifficultFlag(int difficultFlag){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(DIFFICULT, difficultFlag);
        editor.apply();
    }
}
