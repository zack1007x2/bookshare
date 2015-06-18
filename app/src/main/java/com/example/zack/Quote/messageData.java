package com.example.zack.Quote;

import android.graphics.Bitmap;

/**
 * Created by Zack on 15/6/8.
 */
public class messageData {

    private String title;
    private Bitmap bmp;

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
