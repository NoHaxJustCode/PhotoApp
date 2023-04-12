package com.example.photoapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.photoapp.model.Album;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.home_title);
        scrollView = findViewById(R.id.home_scroll_view);
        albumListLayout = findViewById(R.id.home_album_list);

        // Load saved albums at startup
        albums = loadAlbums();

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

        Button deleteAlbumButton = findViewById(R.id.delete_album_button);
        deleteAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete album button click
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
                                saveAlbums();
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
        TextView albumTextView = new TextView(this);
        albumTextView.setText(album.getName());
        albumTextView.setTextSize(24);
        albumTextView.setPadding(0, 24, 0, 24);

        albumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle album click
            }
        });

        albumListLayout.addView(albumTextView);
    }
    private void saveAlbums() {
        SharedPreferences prefs = getSharedPreferences(ALBUMS_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(albums);
        editor.putString(ALBUMS_KEY, json);
        editor.apply();
    }

    private List<Album> loadAlbums() {
        SharedPreferences prefs = getSharedPreferences(ALBUMS_PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(ALBUMS_KEY, null);
        Type type = new TypeToken<List<Album>>() {}.getType();
        if(json == null)
            return new ArrayList<Album>();
        return gson.fromJson(json, type);
    }


}
