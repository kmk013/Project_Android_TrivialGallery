package com.example.kimmingyu.androidproject_trivialgallery;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class Singleton {
    private int img_cnt;

    private int select_img_num;

    public int getCnt() {
        return img_cnt;
    }
    public void setCnt(int cnt) {
        this.img_cnt = cnt;
    }

    public int getSelect_img_num() {
        return select_img_num;
    }
    public void setSelect_img_num(int num) {
        this.select_img_num = num;
    }

    private static Singleton instance = null;

    public static synchronized Singleton getInstance() {
        if(instance == null)
            instance = new Singleton();
        return instance;
    }

    public int ExifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
            return 90;
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
            return 180;
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
            return 270;
        return 0;
    }

    public Bitmap Rotate(Bitmap src, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
