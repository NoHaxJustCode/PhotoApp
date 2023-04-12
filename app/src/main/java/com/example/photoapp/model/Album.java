package com.example.photoapp.model;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private String name;
    private List<Photo> photos;

    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    public Album(String albumName, ArrayList<Photo> photos, ArrayList<Tag> tags) {
    }

    public String getName() {
        return name;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }

    public List<Photo> getPhotos() {
        return photos;
    }

}

