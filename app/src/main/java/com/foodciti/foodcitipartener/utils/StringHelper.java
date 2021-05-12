package com.foodciti.foodcitipartener.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class StringHelper {
    public static String capitalizeEachWord(String str) {
        StringBuilder result = new StringBuilder(str.length());
        String words[] = str.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() > 0)
                result.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1).trim()).append(" ");

        }
        return result.toString();
    }

    private static boolean inspect(char c) {
        boolean success = false;
        if(c==9) {
            success = true;
        } else if(c==10) {
            success = true;
        } else if(c==12) {
            success = true;
        } else if(c==13){
            success = true;
        } else if(c==32) {
            success = true;
        }

        return success;
    }

    public static String capitalizeEachWordAfterComma(String str) {
        StringBuilder result = new StringBuilder(str.length());
        String words[] = str.split(",");
        for (int i = 0; i < words.length; i++) {
//            result.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1)).append(", ");
            if (i == words.length - 1) {
                String capitalizedWord = capitalizeEachWord(words[i].trim());
                if (!capitalizedWord.isEmpty())
                    result.append(capitalizedWord.trim());
            } else {
                String capitalizedWord = capitalizeEachWord(words[i].trim());
                if (!capitalizedWord.isEmpty())
                    result.append(capitalizedWord.trim()).append(", ");
            }
        }
        return result.toString();
    }
}
