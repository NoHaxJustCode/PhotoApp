package com.example.photoapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class SerializedImage implements Serializable {
    private byte[] bytes;
    private static final long serialVersionUID = 2345L;


    public SerializedImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bytes = stream.toByteArray();
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
