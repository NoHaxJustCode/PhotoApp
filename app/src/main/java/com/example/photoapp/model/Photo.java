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

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void deleteTag(Tag tag) {
        tags.remove(tag);
    }

    public boolean containsTag(Tag tag) {
        for(Tag t : tags) {
            if(t.toString().equals(tag.toString()))
                return true;
        }
        return false;
    }

    public ArrayList<String> getTagValues(String selectedCategory) {
        ArrayList<String> values = new ArrayList<>();
        for(Tag t : tags) {
            if(t.getType().equals(selectedCategory)) {
                values.add(t.getValue());
            }
        }
        return values;
    }

    public boolean hasTagType(String selectedCategory) {
        return true;
    }

    public boolean hasTag(String selectedCategory, String value) {
        for(Tag t : tags) {
            if(t.getType().equalsIgnoreCase(selectedCategory) && t.getValue().equalsIgnoreCase(value)) {
               return true;
            }
        }
        return false;
    }
}
