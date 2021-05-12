package com.foodciti.foodcitipartener.gson;

import java.io.Serializable;

public class TableJson implements Serializable {
    private Long id;
    private String name;
    private long lastupdated;

    public long getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(long lastupdated) {
        this.lastupdated = lastupdated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
