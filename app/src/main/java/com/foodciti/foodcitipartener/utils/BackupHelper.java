package com.foodciti.foodcitipartener.utils;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

import io.realm.Realm;

public abstract class BackupHelper {
    private static final String TAG = "BackupHelper";
    protected Realm realm;
    private String parentFolder;

    public void setParentFolder(String parentFolder) {
        this.parentFolder = parentFolder;
    }

    public BackupHelper(Activity activity) {
        realm = RealmManager.getLocalInstance();
    }

    public <T> List<T> deserialize(Type type, String fileName) {
        List<T> objects = null;
        String parentfolderName = "foodciti";
        String menuFolderName = "menu";
        /*File folder = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName + File.separator + menuFolderName);
        if (!folder.exists())
            folder.mkdirs();*/

//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName + File.separator + menuFolderName + File.separator + fileName);
        File file = new File(parentFolder + File.separator + fileName);
        if (!file.exists()) {
            return null;
        }

        InputStream ins = null;
        try {
            ins = new FileInputStream(file);
            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

            objects = gson.fromJson(jsonReader, type);
            jsonReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return objects;
    }


    public <T> File serialize(List<T> list, String fileName, boolean append) {
        Log.e(TAG, "------size: " + list.size());
        File result = null;
        try {

            String parentfolderName = "foodciti";
            String menuFolderName = "menu";
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName + File.separator + menuFolderName);
            if (!folder.exists())
                folder.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName + File.separator + menuFolderName + File.separator + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            Writer writer = new BufferedWriter(new FileWriter(file, append));
            JsonWriter jsonWriter = new JsonWriter(writer);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type type = new TypeToken<T>() {
            }.getType();
            jsonWriter.beginArray();
            for (T t : list) {
                gson.toJson(t, type, jsonWriter);
                jsonWriter.flush();
            }
            jsonWriter.endArray();
            jsonWriter.close();
            result = file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public abstract List<File> backup();

    public abstract List<File> backup(boolean append);

    public abstract Boolean restore();
}
