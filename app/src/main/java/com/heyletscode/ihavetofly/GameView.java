package com.heyletscode.ihavetofly;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private final int screenX;
    private final int screenY;
    private int score = 0;
    public static float screenRatioX, screenRatioY;
    private final Paint paint;
    private final Devil[] birds;
    private final SharedPreferences prefs;
    private final Random random;
    private final SoundPool soundPool;
    private final List<Bullet> bullets;
    private final int sound;
    private final Flight flight;
    private final GameActivity activity;
    private final Background background1;
    private final Background background2;
    private final Life life;
    private final MediaPlayer backgroundMusic; // MediaPlayer for background music
    private final boolean isMuteMusic ;
    // ตัวแปรสำหรับปุ่มยิง
    private final Bitmap shootButtonBitmap;
    private final Rect shootButtonRect;
    private final Bitmap jumpBut;
    private final Rect jumpButtonRect;



    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;
        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        sound = soundPool.load(activity, R.raw.shoot, 1);
//กำหนดขนาดหน้าจอ
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;
//สร้างพื้นหลัง
        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());
//สร้างแฮรี่
        flight = new Flight(this, screenY, getResources());

        bullets = new ArrayList<>();
        background2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);
//สร้างผู้คุมวิญญาณ
        birds = new Devil[4];
        for (int i = 0; i < 4; i++) {
            Devil bird = new Devil(getResources());
            birds[i] = bird;
        }

        random = new Random();

//สร้างชีวิต
        life = new Life(getResources(), screenX, screenY);

        // โหลดรูปภาพปุ่มยิง
        shootButtonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.but_shoot);
        int shootButtonWidth = 590;  // ความกว้างของปุ่มยิง
        int shootButtonHeight = 470; // ความสูงของปุ่มยิง
        shootButtonRect = new Rect(screenX - shootButtonWidth + 20, screenY - shootButtonHeight - 20, screenX - 20, screenY + 60);

// โหลดรูปภาพปุ่มกระโดด
        jumpBut = BitmapFactory.decodeResource(getResources(), R.drawable.but_jump);
        int jumpButtonWidth = 590;  // ความกว้างของปุ่มกระโดด
        int jumpButtonHeight = 470; // ความสูงของปุ่มกระโดด

// ปรับตำแหน่งของปุ่มกระโดดให้ไปอยู่ทางฝั่งซ้าย
        jumpButtonRect = new Rect(20, screenY - jumpButtonHeight - 20, jumpButtonWidth + 20, screenY + 60);


        // เรื่อง background music
        isMuteMusic = prefs.getBoolean("isMuteMusic", false);
        // Initialize MediaPlayer with the background music resource
        backgroundMusic = MediaPlayer.create(activity, R.raw.hsong); // Replace with your music file
        backgroundMusic.setLooping(true); // Loop the music
        backgroundMusic.setVolume(1.0f, 1.0f);
    }


//ฟังก์ชันรันเกม
    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

//ฟังก์ชันอัปเดตการทำงาน
    private void update() {
        background1.x -= (int) (10 * screenRatioX);
        background2.x -= (int) (10 * screenRatioX);
//check bg1 เลื่อนอกจากหน้าจอไหม
        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = background2.x + background2.background.getWidth();// วางที่ขวาของ background2
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = background1.x + background1.background.getWidth();// วางที่ขวาของ background1

        }

        if (flight.isGoingUp) {
            flight.y -= (int) (30 * screenRatioY);
        } else {
            flight.y += (int) (30 * screenRatioY);
        }

        if (flight.y < 0) {
            flight.y = 0;
        }

        if (flight.y >= screenY - flight.height) {
            flight.y = screenY - flight.height;
        }

        List<Bullet> trash = new ArrayList<>();

        for (Bullet bullet : bullets) {
            if (bullet.x > screenX)
                trash.add(bullet);
            bullet.x += (int) (50 * screenRatioX);

            for (Devil bird : birds) {
                if (Rect.intersects(bird.getCollisionShape(), bullet.getCollisionShape())) {
                    score++;
                    bird.x = -500;
                    bullet.x = screenX + 500;
                    bird.wasShot = true;
                }
            }
        }

        for (Bullet bullet : trash)
            bullets.remove(bullet);

        for (Devil bird : birds) {
            bird.x -= bird.speed;

            if (bird.x + bird.width < 0) {
                if (!bird.wasShot) {
                    life.decreaseHeartCounter();
                    if (life.isDead()) {
                        isGameOver = true;
                        return;
                    }
                    resetPositionAllBird();
                }
                int difficult = prefs.getInt(LevelSelectActivity.DIFFICULT,LevelSelectActivity.FLAG_EASY);

                if(difficult == LevelSelectActivity.FLAG_EASY){


                    int bound = (int) (30 * screenRatioX);
                    bird.speed = random.nextInt(bound);

                    if (bird.speed < 10 * screenRatioX)
                        bird.speed = (int) (10 * screenRatioX);

                }
                else if(difficult == LevelSelectActivity.FLAG_NORMAL){

                    int bound = (int) (30 * screenRatioX);
                    bird.speed = random.nextInt(bound);
                    if (bird.speed < 20 * screenRatioX)
                        bird.speed = (int) (20 * screenRatioX);


                }
                else if(difficult == LevelSelectActivity.FLAG_HARD){
                    /*birdLength = 4;
                    speedMultiple = 20;*/

                    int bound = (int) (30 * screenRatioX);
                    bird.speed = random.nextInt(bound);
                    if (bird.speed < 30 * screenRatioX)
                        bird.speed = (int) (40 * screenRatioX);


                }

                bird.x = screenX;
                bird.y = random.nextInt(screenY - bird.height);

                bird.wasShot = false;
            }

            if (Rect.intersects(bird.getCollisionShape(), flight.getCollisionShape())) {
                life.decreaseHeartCounter();
                if (life.isDead()) {
                    isGameOver = true;
                    return;
                }
                resetPositionAllBird();
            }
        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for (Devil bird : birds)
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);

            canvas.drawText(score + "", screenX / 2f, 164, paint);

            canvas.drawBitmap(life.getHeart(), life.getX(), life.getY(), paint);

            // วาดปุ่มยิง
            canvas.drawBitmap(shootButtonBitmap, null, shootButtonRect, paint);
            // วาดปุ่มกระโดด
            canvas.drawBitmap(jumpBut, null, jumpButtonRect, paint);
//ถ้าเกมจบ
            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                //waitBeforeExiting();
                activity.showGameOverDialog(score);
                if (backgroundMusic.isPlaying()) {
                    backgroundMusic.pause();
                }
                return;
            }

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void resetPositionAllBird() {
        for (Devil bird : birds) {
            bird.x = screenX + 1000;
            bird.y = random.nextInt(screenY - bird.height);
        }
    }

    private void saveIfHighScore() {
        if (prefs.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }
    }
//
    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;

        // รีสตาร์ทเธรดของเกม
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        thread = new Thread(this);
        thread.start();

        // ตั้งค่า MediaPlayer
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//        }
//        mediaPlayer = MediaPlayer.create(activity, R.raw.hsong);
//        mediaPlayer.setLooping(true); // ตั้งค่าให้เล่นซ้ำ
//        mediaPlayer.start(); // เริ่มเล่นเพลง

        // เล่นเพลงพื้นหลังหากไม่ปิดเสียง
        if (backgroundMusic != null && !backgroundMusic.isPlaying() && !isMuteMusic) {
            backgroundMusic.start();
        }
    }


    public void pause() {
        try {
            // หยุดการทำงานของเกม
            isPlaying = false;
            if (thread != null && thread.isAlive()) {
                thread.join(); // รอให้เธรดสิ้นสุดการทำงาน
            }

            // หยุดเพลงและปล่อยทรัพยากร
//            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//                mediaPlayer = null; // ตั้งค่าเป็น null เพื่อป้องกันการใช้งานซ้ำ
//            }

            // หยุดเพลงพื้นหลังชั่วคราว
            if (backgroundMusic != null && backgroundMusic.isPlaying()) {
                backgroundMusic.pause();
            }
        } catch (InterruptedException e) {
            e.printStackTrace(); // จัดการข้อผิดพลาดในกรณีที่เธรดถูกขัดจังหวะ
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // ตรวจจับการสัมผัสที่ปุ่มยิง
                if (shootButtonRect.contains((int) x, (int) y)) {
                    newBullet();  // เมื่อกดปุ่มยิงให้สร้างกระสุน
                    return true;  // ป้องกันการทำงานที่อื่น ๆ
                }

                // ตรวจจับการสัมผัสที่ปุ่มกระโดด
                if (jumpButtonRect.contains((int) x, (int) y)) {
                    flight.isGoingUp = true;  // ทำให้เครื่องบินกระโดด
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                // ตรวจจับเมื่อปล่อยนิ้วที่ปุ่มกระโดด
                if (jumpButtonRect.contains((int) x, (int) y)) {
                    flight.isGoingUp = false;  // หยุดกระโดดเมื่อปล่อยนิ้วจากปุ่มกระโดด
                }

                // ปล่อยการกระโดดเมื่อแตะออกจากส่วนอื่น ๆ
                if (x < (float) screenX / 2) {
                    flight.isGoingUp = false;  // ปล่อยนิ้วให้เครื่องบินหยุดบินขึ้น
                }
                break;
        }

        return true;
    }


    public void newBullet() {
        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y - flight.height / 7;
        bullets.add(bullet);

        // เล่นเสียงเมื่อยิง

        if (!prefs.getBoolean("isMute", false))
            soundPool.play(sound, 0.1f, 0.1f, 0, 0, 1f);
    }

    // Release resources when the game ends
    @Override
    protected void finalize() throws Throwable {
        if (backgroundMusic != null) {
            backgroundMusic.release(); // Release MediaPlayer resources
        }
        super.finalize();
    }
}
