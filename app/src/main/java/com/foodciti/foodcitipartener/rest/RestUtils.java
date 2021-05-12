package com.foodciti.foodcitipartener.rest;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by quflip1 on 30-03-2017.
 */

public class RestUtils {
    public static RequestBody TypedString(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    public static RequestBody TypedImageFile(File file) {
        return RequestBody.create(MediaType.parse("image/*"), file);
    }

}