package com.example.photoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photoapp.R;
import com.example.photoapp.model.Album;
import com.example.photoapp.model.Photo;
import com.example.photoapp.model.PhotoAdapter;
import com.example.photoapp.model.SaveLoadHandler;
import com.example.photoapp.model.Tag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AlbumHome extends AppCompatActivity {

    private ListView photoListView;
    private Button addPhotoButton;
    private Button slideshowButton;
    private Button removeButton;
    private Button openButton;

    private Album album;
    private List<Album> albums;
    private List<Photo> photoList;

    private Gson gson;
    private File albumFile;
    ActivityResultLauncher<String> selectPhotoLauncher;
    private static final String FILE_NAME_FORMAT = "album_%s.json";

    // Define the name of the shared preference
    private static final String SHARED_PREFS_NAME = "photo_app_prefs";

    private SaveLoadHandler saveLoadAlbum;
    private Context context = this;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_open);

        // Initialize views
        photoListView = findViewById(R.id.photoListView);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        slideshowButton = findViewById(R.id.slideshowButton);

        path = this.getApplicationInfo().dataDir + "/data.dat";
        File data = new File(path);
        // Load Data File
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            albums = (ArrayList<Album>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        // Get the album from the intent or shared preferences
        String albumName = getIntent().getStringExtra("albumName");
        int albumNumber = 0;
        for(int i = 0; i<albums.size(); i++) {
            if(albums.get(i).getName().equals(albumName))
                albumNumber = i;
        }
        for(Album a : albums) {
            if(a.getName().equals(albumName))
                album = a;
        }
        photoList = album.getPhotos();
        PhotoAdapter adapter = new PhotoAdapter(this, albumNumber, path, albums);
        photoListView.setAdapter(adapter);
        if (!photoList.isEmpty()) {
            photoListView.setSelection(0);
        }

        // Initialize the activity result launcher
        selectPhotoLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            // Handle the result
            if (uri != null) {
                // Do something with the selected photo URI
                addPhotoToAlbum(uri, adapter);
            }
        });

        // Set button click listeners
        addPhotoButton.setOnClickListener(view -> {
            // Start the SELECT_PHOTO activity
            selectPhotoLauncher.launch("image/*");
        });

        slideshowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the SlideshowActivity with the selected album name
                Intent intent = new Intent(AlbumHome.this, SlideshowActivity.class);
                intent.putExtra("albumName", album.getName());
                startActivity(intent);
            }
        });
    }
    private void addPhotoToAlbum(Uri photoUri, PhotoAdapter adapter) {
        if(album == null) {
            Toast.makeText(this, "Album is null", Toast.LENGTH_SHORT).show();
            return;
        }
        // Get the bitmap of the selected photo
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a new Photo object and add it to the album
        if (bitmap != null) {
            Photo photo = new Photo(bitmap, new ArrayList<>());
            album.addPhoto(photo);
            adapter.notifyDataSetChanged();
            SaveLoadHandler.saveData(albums, path);
        }
    }
}