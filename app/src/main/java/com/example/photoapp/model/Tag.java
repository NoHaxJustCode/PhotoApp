package com.example.photoapp.model;

import java.io.Serializable;

public class Tag implements Serializable {
    private String type;
    private String value;
    private static final long serialVersionUID = 2345L;


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



