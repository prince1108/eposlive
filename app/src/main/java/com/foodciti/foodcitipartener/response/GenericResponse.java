package com.foodciti.foodcitipartener.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by quflip1 on 16-02-2017.
 */

public class GenericResponse<T> {

    @JsonProperty("status")
    private String status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private T data;

    public T getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
