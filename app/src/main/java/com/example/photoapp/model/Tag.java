package com.example.photoapp.model;

import java.io.Serializable;

public class Tag implements Serializable {
    private String type;
    private String value;
    private static final long serialVersionUID = 2345L;


    public Tag(String type, String value) {
        this.type = type.toLowerCase();
        this.value = value.toLowerCase();
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String toString()  {
        return type.toUpperCase()+": "+value.toUpperCase();
    }
}



