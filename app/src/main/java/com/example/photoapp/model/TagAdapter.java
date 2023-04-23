package com.example.photoapp.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.photoapp.R;

import java.util.List;

public class TagAdapter extends ArrayAdapter<Tag> {

    private int currAlbum;
    private List<Album> albums;
    private Context context;
    private String path;

    public TagAdapter(Context context, List<Tag> tags, List<Album> albums, int currAlbum, String path) {
        super(context, 0, tags);
        this.context = context;
        this.albums = albums;
        this.currAlbum = currAlbum;
        this.path = path;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tag tag = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tag_list_item, parent, false);
        }

        TextView tagTextView = convertView.findViewById(R.id.tag_textview);
        tagTextView.setText(tag.toString());

        Button deleteButton = convertView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete tag from photo
                Photo photo = albums.get(currAlbum).getPhotos().get(position);
                photo.deleteTag(tag);
                // Save data
                SaveLoadHandler.saveData(albums, path);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
}
