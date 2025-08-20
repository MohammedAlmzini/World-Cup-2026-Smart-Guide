package com.ahmmedalmzini783.wcguide.data.local.converter;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    private static Gson gson = new Gson();

    @TypeConverter
    public static String fromStringList(List<String> value) {
        if (value == null) {
            return null;
        }
        return gson.toJson(value);
    }

    @TypeConverter
    public static List<String> toStringList(String value) {
        if (value == null) {
            return null;
        }
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType);
    }
}