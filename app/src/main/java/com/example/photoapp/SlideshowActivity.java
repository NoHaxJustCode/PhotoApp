package com.example.photoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photoapp.model.Album;
import com.example.photoapp.model.Photo;
import com.example.photoapp.model.SaveLoadHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class SlideshowActivity extends AppCompatActivity {

    private ImageView imageView;
    private List<Photo> photoList;
    private int currentPosition;
    private String path;
    private List<Album> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideshow_open);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        path = this.getApplicationInfo().dataDir + "/data.dat";

        File data = new File(path);
        if (!data.exists() || !data.isFile()) {
            try {
                data.createNewFile();
                albums = new ArrayList<Album>();
                albums.add(new Album("stock"));
                SaveLoadHandler.saveData(albums, path);
            } catch (Exception exception) {
                albums = new ArrayList<>();
            }
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            albums = (ArrayList<Album>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (Exception exception) {
            albums = new ArrayList<>();
        }
        Bundle extras = getIntent().getExtras();
        int currAlbum = extras.getInt("currAlbum");
        currentPosition = 0;
        photoList = albums.get(currAlbum).getPhotos();

        // Initialize the ImageView with the first photo
        imageView = findViewById(R.id.slideshow_image_view);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        updateImage();

        // Set up the left and right buttons to navigate the slideshow
        Button leftButton = findViewById(R.id.slideshow_left_button);
        Button rightButton = findViewById(R.id.slideshow_right_button);

        leftButton.setOnClickListener(v -> {
            currentPosition = (currentPosition - 1 + photoList.size()) % photoList.size();
            updateImage();
        });

        rightButton.setOnClickListener(v -> {
            currentPosition = (currentPosition + 1) % photoList.size();
            updateImage();
        });
    }

    /**
     * Updates the ImageView to display the current photo.
     */
    private void updateImage() {
        Photo photo = photoList.get(currentPosition);
        Bitmap bitmap = photo.getBitmap();
        imageView.setImageBitmap(bitmap);
    }
}

