package com.heyletscode.ihavetofly;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();

        // เรื่อง exit dialog add เพราะเตรียมปิด activity ตอนกด back หน้า menu
        MainActivity.activityList.add(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        gameView = new GameView(this, point.x, point.y);

        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // เรื่อง exit dialog ลบเพราะ destroy อยู่แล้ว
        MainActivity.activityList.remove(this);
    }

    public void showGameOverDialog(final int score) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.end_dialog, null);
                builder.setView(dialogView);

                // Set the dialog as non-cancelable
                builder.setCancelable(false);

                TextView textViewScore = dialogView.findViewById(R.id.eng_lang);
                textViewScore.setText(String.format("%1s %2s",textViewScore.getText().toString(),score));

                // Create the dialog instance
                final AlertDialog dialog = builder.create();

                // Set up the positive button action
                Button positiveButton = dialogView.findViewById(R.id.buttonBackEndGame);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(GameActivity.this, MainActivity.class));
                        finish();
                    }
                });
                // Show the dialog
                dialog.show();
            }
        });
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
