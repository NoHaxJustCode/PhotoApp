package com.example.photoapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {
    private String name;
    private List<Photo> photos;
    private static final long serialVersionUID = 2345L;


    public Album(String name) {
        this.name = name;
        photos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void rename(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void deletePhoto(Photo photo) {
        photos.remove(photo);
    }

    public void removePhotos(List<Photo> selectedPhotos) {
        photos.remove(selectedPhotos);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
    }
}


