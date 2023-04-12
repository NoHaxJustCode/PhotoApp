package com.example.photoapp.model;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;

public class Photo {

    private String filename;
    private Bitmap image;
    private List<Tag> tags;

    public Photo(String filename, Bitmap image) {
        this.filename = filename;
        this.image = image;
        this.tags = new ArrayList<>();
    }

    public String getFilename() {
        return filename;
    }

    public Bitmap getImage() {
        return image;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
    }

    public List<Tag> getTags() {
        return tags;
    }

}

