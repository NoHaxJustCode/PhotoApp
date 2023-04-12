package com.example.photoapp.model;
public class Tag {

    public static final String TYPE_PERSON = "Person";
    public static final String TYPE_LOCATION = "Location";

    private String type;
    private String value;

    public Tag(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

}

