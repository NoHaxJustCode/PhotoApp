package com.example.photoapp.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.example.photoapp.PhotoActivity;
import com.example.photoapp.R;
import com.example.photoapp.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends BaseAdapter {
    private List<Photo> photos;
    private Context context;
    private String path;
    private List<Album> albums;
    private int currAlbum;

    private boolean belongsToSearch;
    public PhotoAdapter(Context context, int currAlbum, String path, List<Album> albums) {
        this.context = context;
        this.currAlbum = currAlbum;
        this.photos = albums.get(currAlbum).getPhotos();
        this.path = path;
        this.albums = albums;
        belongsToSearch = false;
    }

    public PhotoAdapter(Context context, ArrayList<Photo> photos, String path, List<Album> albums) {
        this.context=context;
        this.path=path;
        this.photos=photos;
        this.albums = albums;
        belongsToSearch = true;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(!belongsToSearch) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
            }

            ImageView imageView = view.findViewById(R.id.image_view);
            Button removeButton = view.findViewById(R.id.remove_button);
            Button openButton = view.findViewById(R.id.open_button);

            Bitmap bitmap = photos.get(position).getBitmap();
            imageView.setImageBitmap(bitmap);

            removeButton.setOnClickListener(v -> {
                removePhoto(position);
                notifyDataSetChanged();
            });

            openButton.setOnClickListener(v -> {
                openPhoto(position);
            });

            return view;
        }
        else {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.search_photo_item, parent, false);
            }

            ImageView imageView = view.findViewById(R.id.image_view);

            Bitmap bitmap = photos.get(position).getBitmap();
            imageView.setImageBitmap(bitmap);

            return view;
        }
    }

    public void openPhoto(int position) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("currAlbum", currAlbum);
        context.startActivity(intent);
    }


    private void removePhoto(int position) {
        photos.remove(position);
        SaveLoadHandler.saveData(albums, path);
    }

    public void clear() {
        photos.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Photo> matchingPhotos) {
        photos.addAll(matchingPhotos);
        notifyDataSetChanged();
    }

}
