package com.example.photoapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.photoapp.model.Album;
import com.example.photoapp.model.SaveLoadHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {

    private ImageView imageView;
    private List<Album> albums;
    private int currAlbum;
    private int position;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_open);

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
        // Load saved albums at startup
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            albums = (ArrayList<Album>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (Exception exception) {
            albums = new ArrayList<>();
        }

        imageView = findViewById(R.id.photo_image);

        // Extract extras from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currAlbum = extras.getInt("currAlbum");
            position = extras.getInt("position");
        }

        // Load the selected photo into the image view
        Bitmap bitmap = albums.get(currAlbum).getPhotos().get(position).getBitmap();
        imageView.setImageBitmap(bitmap);

        // Add items to the tag_type_spinner
        Spinner tagTypeSpinner = findViewById(R.id.tag_type_spinner);
        List<String> tagTypes = new ArrayList<>();
        tagTypes.add("Person");
        tagTypes.add("Location");
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tagTypes);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagTypeSpinner.setAdapter(tagAdapter);

        // Add items to the album_spinner
        Spinner albumSpinner = findViewById(R.id.album_spinner);
        List<String> albumNames = new ArrayList<>();
        for (Album album : albums) {
            albumNames.add(album.getName());
        }
        ArrayAdapter<String> albumAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, albumNames);
        albumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        albumSpinner.setAdapter(albumAdapter);
    }
}
