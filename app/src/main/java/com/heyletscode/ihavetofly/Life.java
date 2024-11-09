package com.heyletscode.ihavetofly;

import static com.heyletscode.ihavetofly.GameView.screenRatioX;
import static com.heyletscode.ihavetofly.GameView.screenRatioY;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

// เรื่องหัวใจ
public class Life {

    int x ,y ,width, height, heartCounter = 3;
    private Bitmap heart1, heart2, heart3, heart4;


    Life (Resources res ,int screenX , int screenY) {

        heart1 = BitmapFactory.decodeResource(res, R.drawable.heart1);
        heart2 = BitmapFactory.decodeResource(res, R.drawable.heart2);
        heart3 = BitmapFactory.decodeResource(res, R.drawable.heart3);
        heart4 = BitmapFactory.decodeResource(res, R.drawable.heart4);

        width = heart1.getWidth();
        height = heart1.getHeight();

        width /= 8;
        height /= 8;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        heart1 = Bitmap.createScaledBitmap(heart1, width, height, false);
        heart2 = Bitmap.createScaledBitmap(heart2, width, height, false);
        heart3 = Bitmap.createScaledBitmap(heart3, width, height, false);
        heart4 = Bitmap.createScaledBitmap(heart4, width, height, false);

        x = screenX - width - 60;
        y = 60;
    }

    Bitmap getHeart () {

        if (heartCounter == 3) {
            return heart1;
        }

        if (heartCounter == 2) {
            return heart2;
        }

        if (heartCounter == 1) {
            return heart3;
        }
        return heart4;
    }

    public int getWidth(){
        return width;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public boolean isDead(){
        return heartCounter == 0;
    }

    public void decreaseHeartCounter(){
        heartCounter--;
    }

}
