package com.example.photoapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Photo implements Serializable {
    private SerializedImage image;
    private ArrayList<Tag> tags;
    private static final long serialVersionUID = 2345L;


    public Photo(Bitmap image, ArrayList<Tag> tags) {
        this.image = new SerializedImage(image);
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public Bitmap getBitmap() {
        return image.getBitmap();
    }
}
