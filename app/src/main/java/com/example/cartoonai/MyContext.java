package com.example.cartoonai;

import android.content.Context;

import java.io.File;

public class MyContext {
    public static Context context;
    public static MainActivity main;
    public static String upload_url;
    public static FirstFragment first;
    public static File fileToSave;

    public MyContext(Context c, MainActivity m) {
        context = c;
        main = m;
        upload_url = c.getResources().getString(R.string.upload_url);
    }
}
