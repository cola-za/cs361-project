package com.heyletscode.ihavetofly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Bird[] birds;
    private SharedPreferences prefs;
    private Random random;
    private SoundPool soundPool;
    private List<Bullet> bullets;
    private int sound;
    private Flight flight;
    private GameActivity activity;
    private Background background1, background2;
    private Heart heart;
    private int birdLength = 4;
    private double speedMultiple = 1;

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

        } else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        sound = soundPool.load(activity, R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        flight = new Flight(this, screenY, getResources());

        bullets = new ArrayList<>();

        background2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        // set ระดับความยาก
        int difficult = prefs.getInt(LevelSelectActivity.DIFFICULT,LevelSelectActivity.FLAG_EASY);
        if(difficult == LevelSelectActivity.FLAG_EASY){
            birdLength = 4;
            speedMultiple = 0.5;
        }
        else if(difficult == LevelSelectActivity.FLAG_NORMAL){
            birdLength = 4;
            speedMultiple = 1;
        }
        else if(difficult == LevelSelectActivity.FLAG_HARD){
            birdLength = 4;
            speedMultiple = 2;
        }

        birds = new Bird[birdLength];

        for (int i = 0;i < birds.length;i++) {

            Bird bird = new Bird(getResources());
            birds[i] = bird;

        }

        random = new Random();

        // เรื่องหัวใจ
        heart = new Heart(getResources() , screenX , screenY);
    }

    @Override
    public void run() {

        while (isPlaying) {

            update ();
            draw ();
            sleep ();

        }

    }

    private void update () {

        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }

        if (flight.isGoingUp)
            flight.y -= 30 * screenRatioY;
        else
            flight.y += 30 * screenRatioY;

        if (flight.y < 0)
            flight.y = 0;

        if (flight.y >= screenY - flight.height)
            flight.y = screenY - flight.height;

        List<Bullet> trash = new ArrayList<>();

        for (Bullet bullet : bullets) {

            if (bullet.x > screenX)
                trash.add(bullet);

            bullet.x += 50 * screenRatioX;

            for (Bird bird : birds) {

                if (Rect.intersects(bird.getCollisionShape(),
                        bullet.getCollisionShape())) {

                    score++;
                    bird.x = -1000;
                    bullet.x = screenX + 1500;
                    bird.wasShot = true;

                }

            }

        }

        for (Bullet bullet : trash)
            bullets.remove(bullet);

        for (Bird bird : birds) {

            bird.x -= bird.speed;

            if (bird.x + bird.width < 0) {

                // เรื่องหัวใจ นกบินผ่านไปได้หัวใจลด
                if (!bird.wasShot) {
                    heart.decreaseHeartCounter();
                    if(heart.isDead()){
                        isGameOver = true;
                        return;
                    }
                    resetPositionAllBird();
                }

                // นกคูณ speed
                int bound = (int) (30 * screenRatioX * speedMultiple);
                bird.speed = random.nextInt(bound);

                if (bird.speed < 10 * screenRatioX * speedMultiple)
                    bird.speed = (int) (10 * screenRatioX * speedMultiple);

                bird.x = screenX + 500;
                bird.y = random.nextInt(screenY - bird.height);

                bird.wasShot = false;
            }

            // เรื่องหัวใจ โดนนกชน
            if (Rect.intersects(bird.getCollisionShape(), flight.getCollisionShape())) {
                heart.decreaseHeartCounter();;
                if(heart.isDead()){
                    isGameOver = true;
                    return;
                }
                resetPositionAllBird();
            }
        }

    }

    private void draw () {

        if (getHolder().getSurface().isValid()) {

            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for (Bird bird : birds)
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);

            canvas.drawText(score + "", screenX / 2f, 164, paint);

            // เรื่องหัวใจ วาดหัวใจมุมขวาบน
            canvas.drawBitmap(heart.getHeart(), heart.getX(), heart.getY(), paint);


            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
//                waitBeforeExiting ();
                activity.showGameOverDialog(score);
                return;
            }

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);

        }

    }

    //reset position ของนกถ้า หัวใจลด
    private  void resetPositionAllBird(){
        for(Bird bird : birds ){
            bird.x = screenX + 1000;
            bird.y = random.nextInt(screenY - bird.height);
        }
    }

    private void waitBeforeExiting() {

        try {
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void saveIfHighScore() {

        if (prefs.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }

    }

    private void sleep () {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume () {

        isPlaying = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause () {

        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //แก้เรื่องกด บินกับยิงพร้อมกันไม่ได้ chat gpt
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int pointerIndex = event.getActionIndex();  // ดึง index ของ pointer ที่ทำการสัมผัส
        int pointerId = event.getPointerId(pointerIndex);  // ใช้ pointerId ในการแยกการกดหลายจุด
        float x = event.getX(pointerIndex);

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (x < screenX / 2) {
                    flight.isGoingUp = true;  // ถ้ากดด้านซ้ายให้เครื่องบินบินขึ้น
                } else {
                    flight.toShoot++;  // ถ้ากดด้านขวาให้ยิงทันที
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (x < screenX / 2) {
                    flight.isGoingUp = false;  // ปล่อยนิ้วด้านซ้ายให้เครื่องบินหยุดบินขึ้น
                }
                break;
        }

        return true;
    }


    public void newBullet() {

        if (!prefs.getBoolean("isMute", false))
            soundPool.play(sound, 1, 1, 0, 0, 1);

        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height / 2);
        bullets.add(bullet);

    }
}
