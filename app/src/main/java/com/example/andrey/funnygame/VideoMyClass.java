package com.example.andrey.funnygame;


import android.graphics.Bitmap;
import android.widget.Toast;

public class VideoMyClass {
    String title;
    Bitmap preview;
    String videoId;
    public static int counter = 0;

    VideoMyClass(String title, Bitmap preview, String videoId) {
        this.title = title;
        this.preview = preview;
        this.videoId = videoId;
        counter += 1;
        Toast.makeText(MainActivity.context, "Created VideoMyClass", Toast.LENGTH_SHORT).show();
    }
}