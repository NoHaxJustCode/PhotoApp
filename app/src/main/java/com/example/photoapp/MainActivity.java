package com.example.photoapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.photoapp.model.Album;
import com.example.photoapp.model.SaveLoadHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private List<Album> albums;
    private ScrollView scrollView;
    private LinearLayout albumListLayout;
    private static final String ALBUMS_PREFS_NAME = "AlbumsPrefs";
    private static final String ALBUMS_KEY = "Albums";

    private SaveLoadHandler saveLoadAlbums;
    private Context context = this;
    private String path;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.home_title);
        scrollView = findViewById(R.id.home_scroll_view);
        albumListLayout = findViewById(R.id.home_album_list);
        searchButton = findViewById(R.id.searchButton);

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

        saveLoadAlbums = new SaveLoadHandler();

        // Add each album to the scroll view
        for (Album album : albums) {
            addAlbumToScrollView(album);
        }

        Button createAlbumButton = findViewById(R.id.create_album_button);
        createAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateAlbumDialog();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("currAlbum", 0);
                startActivity(intent);
            }
        });
    }

    private void showCreateAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_album, null);
        builder.setView(dialogView);

        final TextView errorTextView = dialogView.findViewById(R.id.errorMessage);
        final TextView nameEditText = dialogView.findViewById(R.id.album_name_edit_text);

        builder.setPositiveButton("Create", null);
        builder.setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button createButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String albumName = nameEditText.getText().toString();
                        if (albumName.isEmpty()) {
                            errorTextView.setText("Album Name Required");
                        } else {
                            boolean albumExists = false;
                            for (Album album : albums) {
                                if (album.getName().equals(albumName)) {
                                    albumExists = true;
                                    break;
                                }
                            }
                            if (albumExists) {
                                errorTextView.setText("Album Name Already Exists");
                            } else {
                                Album newAlbum = new Album(albumName);
                                albums.add(newAlbum);
                                SaveLoadHandler.saveData(albums, path);
                                addAlbumToScrollView(newAlbum);
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void addAlbumToScrollView(Album album) {
        // Create a horizontal layout to hold the album name, delete button, and open button
        LinearLayout albumLayout = new LinearLayout(this);
        albumLayout.setOrientation(LinearLayout.HORIZONTAL);
        albumLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create a text view for the album name
        TextView albumTextView = new TextView(this);
        albumTextView.setText(album.getName());
        albumTextView.setTextSize(24);
        albumTextView.setPadding(0, 24, 0, 24);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        albumTextView.setLayoutParams(textParams);

        // Create a delete button
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        deleteButton.setLayoutParams(buttonParams);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlbumDialog(album);
            }
        });

        // Create a rename button
        Button renameButton = new Button(this);
        renameButton.setText("Rename");
        renameButton.setLayoutParams(buttonParams);
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show an input dialog to get the new name for the album
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Rename Album");

                // Set up the input
                final EditText input = new EditText(MainActivity.this);
                input.setText(album.getName());
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString();
                        albumTextView.setText(newName);
                        album.rename(newName);
                        SaveLoadHandler.saveData(albums, path);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        // Create an open button
        Button openButton = new Button(this);
        openButton.setText("Open");
        openButton.setLayoutParams(buttonParams);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AlbumHome.class);
                intent.putExtra("albumName", album.getName());
                intent.putExtra("album", album);
                startActivity(intent);
            }
        });


        // Add the views to the album layout
        albumLayout.addView(albumTextView);
        albumLayout.addView(openButton);
        albumLayout.addView(renameButton);
        albumLayout.addView(deleteButton);

        // Add the album layout to the scroll view
        albumListLayout.addView(albumLayout);
    }

    private void showDeleteAlbumDialog(Album album) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Album");
        builder.setMessage("Are you sure you want to delete this album?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                albums.remove(album);
                SaveLoadHandler.saveData(albums, path);
                albumListLayout.removeAllViews();
                for (Album album : albums) {
                    addAlbumToScrollView(album);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
