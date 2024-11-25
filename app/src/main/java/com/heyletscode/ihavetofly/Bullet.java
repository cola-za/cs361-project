package com.heyletscode.ihavetofly;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.heyletscode.ihavetofly.GameView.screenRatioX;
import static com.heyletscode.ihavetofly.GameView.screenRatioY;

public class Bullet {

    int x, y, width, height;
    Bitmap bullet;

    Bullet (Resources res) {

        bullet = BitmapFactory.decodeResource(res, R.drawable.magic);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 10;
        height /= 10;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);

    }

    Rect getCollisionShape() {
        int margin = 60; // ลดขนาดพื้นที่การชนลง 20 พิกเซลจากขอบ
        return new Rect(
                x + (int)(margin * screenRatioX),            // ซ้ายขยับเข้ามา
                y + (int)(margin * screenRatioY),            // บนขยับลงมา
                x + width - (int)(margin * screenRatioX),    // ขวาขยับเข้ามา
                y + height - (int)(margin * screenRatioY)    // ล่างขยับขึ้นมา
        );
    }
}
