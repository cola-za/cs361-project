package com.heyletscode.ihavetofly;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.heyletscode.ihavetofly.GameView.screenRatioX;
import static com.heyletscode.ihavetofly.GameView.screenRatioY;

public class Flight {

    int toShoot = 0;
    boolean isGoingUp = false;
    int x, y, width, height, wingCounter = 0, shootCounter = 1;
    Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, dead;
    private GameView gameView;

    Flight (GameView gameView, int screenY, Resources res) {

        this.gameView = gameView;

        // โหลดและปรับขนาด flight1
        flight1 = BitmapFactory.decodeResource(res, R.drawable.harry1);

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 7;
        height /= 7;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);

        // โหลดและปรับขนาด shoot1 ให้มีขนาดแตกต่างจาก flight1
        shoot1 = BitmapFactory.decodeResource(res, R.drawable.harry_shoot);

        int widthShoot = shoot1.getWidth();
        int heightShoot = shoot1.getHeight();

        widthShoot /=4.145 ;
        heightShoot /=4.145;

        widthShoot = (int) (widthShoot * screenRatioX);
        heightShoot = (int) (heightShoot * screenRatioY);

        shoot1 = Bitmap.createScaledBitmap(shoot1, widthShoot, heightShoot, false);

        // โหลดและปรับขนาดภาพ dead
        dead = BitmapFactory.decodeResource(res, R.drawable.harrydead);
        dead = Bitmap.createScaledBitmap(dead, width, height, false);

        y = screenY / 2;
        x = (int) (64 * screenRatioX);
    }

    Bitmap getFlight () {
        if (toShoot != 0) {
            // ถ้ามีการยิง
            toShoot--; // ลดจำนวนการยิง
            gameView.newBullet(); // สร้างกระสุนใหม่
            return shoot1; // ใช้ภาพยิงเดียว
        }
/*
        if (wingCounter == 0) {
            wingCounter++;
            return flight1; // ภาพบินแรก
        }
        wingCounter--;*/

        return flight1; // ภาพบินที่สอง
    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getDead () {
        return dead;
    }

}
