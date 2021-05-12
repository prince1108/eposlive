package com.foodciti.foodcitipartener.utils;

import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ListToCSV {
    public static File For(List<List<String>> listList, List<String> headers, File parentFolder, String name) {
//        TextUtils.join(",", objects);
        File file = null;
        try {
            file = new File(parentFolder + File.separator + name);
            if (!file.exists())
                file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(TextUtils.join(",", headers));
            bufferedWriter.newLine();
            for (List<String> objects : listList) {
                bufferedWriter.write(TextUtils.join(",", objects));
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
