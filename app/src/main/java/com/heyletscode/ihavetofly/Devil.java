package com.heyletscode.ihavetofly;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.heyletscode.ihavetofly.GameView.screenRatioX;
import static com.heyletscode.ihavetofly.GameView.screenRatioY;

public class Devil {

    public int speed = 50;  // ความเร็วเริ่มต้น (เพิ่มความเร็ว)
    public boolean wasShot = true;
    int x = 0, y, width, height, birdCounter = 1;
    Bitmap devil1, devil2, devil3, devil4;

    private long startTime;  // ตัวแปรเก็บเวลาเริ่มต้น

    Devil(Resources res) {

        devil1 = BitmapFactory.decodeResource(res, R.drawable.devil1);
        devil2 = BitmapFactory.decodeResource(res, R.drawable.devil2);
        devil3 = BitmapFactory.decodeResource(res, R.drawable.devil3);
        devil4 = BitmapFactory.decodeResource(res, R.drawable.devil4);

        width = devil1.getWidth();
        height = devil1.getHeight();

        width /= 5;
        height /=5;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        devil1 = Bitmap.createScaledBitmap(devil1, width, height, false);
        devil2 = Bitmap.createScaledBitmap(devil2, width, height, false);
        devil3 = Bitmap.createScaledBitmap(devil3, width, height, false);
        devil4 = Bitmap.createScaledBitmap(devil4, width, height, false);

        y = -height; // เริ่มต้นนอกหน้าจอที่ตำแหน่งบนสุด

        startTime = System.currentTimeMillis();  // เก็บเวลาเริ่มต้น
    }

    // เมธอดในการอัพเดตตำแหน่งของ Devil และเพิ่มความเร็ว
    public void update() {
        long elapsedTime = System.currentTimeMillis() - startTime;  // คำนวณเวลาที่ผ่านไป


        if (elapsedTime > 500) {  //
            speed += 5000;
            startTime = System.currentTimeMillis();  // รีเซ็ตเวลา
        }

        y += speed;  // เคลื่อนที่ Devil ลงตามความเร็ว
    }

    // การเปลี่ยนภาพอนิเมชั่นของ Devil
    Bitmap getBird() {
        Bitmap currentDevil;
        switch (birdCounter) {
            case 1:
                currentDevil = devil1;
                break;
            case 2:
                currentDevil = devil2;
                break;
            case 3:
                currentDevil = devil3;
                break;
            default:
                currentDevil = devil4;
                birdCounter = 0; // รีเซ็ตค่า birdCounter หลังจาก devil4
                break;
        }
        birdCounter++; // เพิ่มค่า birdCounter เพื่อเปลี่ยนภาพในการเรียกครั้งถัดไป
        return currentDevil;
    }

    // การตรวจจับการชน
    public Rect getCollisionShape() {
        // ลดพื้นที่การชนโดยเพิ่มระยะขอบ (margin) เข้าไปในกรอบ
        int margin = 100; // ระยะขอบที่ต้องการลด
        return new Rect(
                x + margin,         // ซ้ายขยับเข้ามา
                y + margin,         // บนขยับลงมา
                x + width - margin, // ขวาขยับเข้ามา
                y + height - margin // ล่างขยับขึ้นมา
        );
    }

}
