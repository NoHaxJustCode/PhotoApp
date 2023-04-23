package com.example.photoapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photoapp.model.Album;
import com.example.photoapp.model.Photo;
import com.example.photoapp.model.SaveLoadHandler;
import com.example.photoapp.model.Tag;
import com.example.photoapp.model.TagAdapter;

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
    private ListView tagListView;
    private Button moveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_open);

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
        tagListView = findViewById(R.id.tag_listview);
        moveButton = findViewById(R.id.moveButton);


        // Extract extras from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currAlbum = extras.getInt("currAlbum");
            position = extras.getInt("position");
        }

        Button addTagButton = findViewById(R.id.add_tag_button);

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

        Photo photo = albums.get(currAlbum).getPhotos().get(position);
        List<Tag> tags = photo.getTags();
        TagAdapter tagAdapter2 = new TagAdapter(PhotoActivity.this, tags, albums, currAlbum, path);
        tagListView.setAdapter(tagAdapter2);

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected tag type and value
                String tagType = tagTypeSpinner.getSelectedItem().toString();
                EditText tagValueEditText = findViewById(R.id.tag_value_edittext);
                String tagValue = tagValueEditText.getText().toString();

                // Add tag to photo
                Photo photo = albums.get(currAlbum).getPhotos().get(position);
                Tag tag = new Tag(tagType, tagValue);

                // Check if tag already exists
                if (!photo.containsTag(tag)) {
                    photo.addTag(tag);

                    // Save data
                    SaveLoadHandler.saveData(albums, path);

                    // Update tag list view
                    List<Tag> tags = photo.getTags();
                    TagAdapter tagAdapter = new TagAdapter(PhotoActivity.this, tags, albums, currAlbum, path);
                    tagListView.setAdapter(tagAdapter);
                }
            }
        });
        Button moveButton = findViewById(R.id.moveButton);
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected album name
                Spinner albumSpinner = findViewById(R.id.album_spinner);
                String selectedAlbumName = albumSpinner.getSelectedItem().toString();

                // Move photo to selected album and delete from current album
                Album selectedAlbum = null;
                for (Album album : albums) {
                    if (album.getName().equals(selectedAlbumName)) {
                        selectedAlbum = album;
                        break;
                    }
                }
                if (selectedAlbum != null) {
                    Photo photo = albums.get(currAlbum).getPhotos().get(position);
                    selectedAlbum.addPhoto(photo);
                    albums.get(currAlbum).removePhoto(photo);
                    SaveLoadHandler.saveData(albums, path);

                    // Return to AlbumHome scene
                    Intent intent = new Intent(PhotoActivity.this, AlbumHome.class);
                    intent.putExtra("albumName", albums.get(currAlbum).getName());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
