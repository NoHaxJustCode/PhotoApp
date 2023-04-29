package com.example.photoapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photoapp.model.Album;
import com.example.photoapp.model.Photo;
import com.example.photoapp.model.PhotoAdapter;
import com.example.photoapp.model.SaveLoadHandler;
import com.example.photoapp.model.Tag;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner categorySpinner;
    private Spinner categorySpinner2;
    private Spinner criteriaSpinner;
    private AutoCompleteTextView autoCompleteTextView;
    private AutoCompleteTextView autoCompleteTextView2;
    private ListView searchResultListView;
    private Button searchButton;
    private PhotoAdapter photoAdapter;

    private ArrayList<String> personValues;
    private ArrayList<String> locationValues;

    public ArrayAdapter<String> autoCompleteAdapter;
    public ArrayAdapter<String> autoCompleteAdapter2;
    private List<Album> albums;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_photo);

        categorySpinner = findViewById(R.id.search_category_spinner);
        categorySpinner2 = findViewById(R.id.search_category_spinner2);
        criteriaSpinner = findViewById(R.id.search_criteria);
        autoCompleteTextView = findViewById(R.id.search_auto_complete_text_view);
        autoCompleteTextView2 = findViewById(R.id.search_auto_complete_text_view2);
        searchResultListView = findViewById(R.id.search_result_list_view);
        searchButton = findViewById(R.id.searchButton);

        path = this.getApplicationInfo().dataDir + "/data.dat";

        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            albums = (ArrayList<Album>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (Exception exception) {
            albums = new ArrayList<>();
        }

        // Set up the category spinner
        List<String> tagTypes = new ArrayList<>();
        tagTypes.add("location");
        tagTypes.add("person");
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tagTypes);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(tagAdapter);
        categorySpinner2.setAdapter(tagAdapter);

        List<String> criteria = new ArrayList<>();
        criteria.add("");
        criteria.add("and");
        criteria.add("or");
        ArrayAdapter<String> tagAdapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, criteria);
        tagAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        criteriaSpinner.setAdapter(tagAdapter2);

        // Set up the auto complete text view
        personValues = new ArrayList<>();
        locationValues = new ArrayList<>();
        for(Album a : albums) {
            for(Photo p : a.getPhotos()) {
                for(Tag t : p.getTags()) {
                    if(t.getType().equalsIgnoreCase("person") && !personValues.contains(t.getValue())) {
                        personValues.add(t.getValue());
                    }
                    else {
                        if(!locationValues.contains(t.getValue()))
                            locationValues.add(t.getValue());
                    }
                }
            }
        }
        autoCompleteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, locationValues);
        autoCompleteTextView.setAdapter(autoCompleteAdapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteAdapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, locationValues);
        autoCompleteTextView2.setAdapter(autoCompleteAdapter2);
        autoCompleteTextView2.setThreshold(1);

        // Add listener to update the autoCompleteAdapter based on the selected category
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                if (selectedCategory.equalsIgnoreCase("person")) {
                    autoCompleteAdapter = new ArrayAdapter<>(SearchActivity.this,
                            android.R.layout.simple_dropdown_item_1line, personValues);
                } else {
                    autoCompleteAdapter = new ArrayAdapter<>(SearchActivity.this,
                            android.R.layout.simple_dropdown_item_1line, locationValues);
                }
                autoCompleteTextView.setAdapter(autoCompleteAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        categorySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                if (selectedCategory.equalsIgnoreCase("person")) {
                    autoCompleteAdapter2 = new ArrayAdapter<>(SearchActivity.this,
                            android.R.layout.simple_dropdown_item_1line, personValues);
                } else {
                    autoCompleteAdapter2 = new ArrayAdapter<>(SearchActivity.this,
                            android.R.layout.simple_dropdown_item_1line, locationValues);
                }
                autoCompleteTextView2.setAdapter(autoCompleteAdapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchPhotos();
            }
        });

        // Set up the search result list view
        photoAdapter = new PhotoAdapter(this, new ArrayList<Photo>(), path, albums);
        searchResultListView.setAdapter(photoAdapter);
    }


    private void searchPhotos() {
        String value = autoCompleteTextView.getText().toString().trim().toLowerCase();
        String value2 = autoCompleteTextView2.getText().toString().trim().toLowerCase();
        String criteria = criteriaSpinner.getSelectedItem().toString();

        if(!value.isEmpty() && !value2.isEmpty()) {
            if(criteria.equalsIgnoreCase("and")) {
                List<Photo> matchingPhotos = new ArrayList<>();
                for (Album album : albums) {
                    for (Photo photo : album.getPhotos()) {
                        if(photo.hasTag(categorySpinner.getSelectedItem().toString(), value) && photo.hasTag(categorySpinner2.getSelectedItem().toString(), value2))
                        {
                            matchingPhotos.add(photo);
                        }
                    }
                }
                photoAdapter.clear();
                photoAdapter.addAll(matchingPhotos);
            }
            else {
                List<Photo> matchingPhotos = new ArrayList<>();
                for (Album album : albums) {
                    for (Photo photo : album.getPhotos()) {
                        if(photo.hasTag(categorySpinner.getSelectedItem().toString(), value) || photo.hasTag(categorySpinner2.getSelectedItem().toString(), value2))
                        {
                            matchingPhotos.add(photo);
                        }
                    }
                }
                photoAdapter.clear();
                photoAdapter.addAll(matchingPhotos);
            }
        }
        if (!value.isEmpty() && criteriaSpinner.getSelectedItem().toString().equals("")) {
            List<Photo> matchingPhotos = new ArrayList<>();
            for (Album album : albums) {
                for (Photo photo : album.getPhotos()) {
                    if(photo.hasTag(categorySpinner.getSelectedItem().toString(), value))
                    {
                        matchingPhotos.add(photo);
                    }
                }
            }
            photoAdapter.clear();
            photoAdapter.addAll(matchingPhotos);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}


