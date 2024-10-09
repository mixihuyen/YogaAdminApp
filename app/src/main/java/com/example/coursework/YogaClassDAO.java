package com.example.coursework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class YogaClassDAO {
    private SQLiteHelper dbHelper;
    private SQLiteDatabase database;

    public YogaClassDAO(Context context) {
        dbHelper = new SQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insertYogaClass(String dayOfWeek, String time, int capacity, int duration, double price, String type, String description) {
        ContentValues values = new ContentValues();
        values.put("dayOfWeek", dayOfWeek);
        values.put("time", time);
        values.put("capacity", capacity);
        values.put("duration", duration);
        values.put("price", price);
        values.put("type", type);
        values.put("description", description);

        return database.insert("YogaClass", null, values);
    }

    public List<YogaCourse> getAllYogaClasses() {
        List<YogaCourse> yogaClasses = new ArrayList<>();
        Cursor cursor = database.query("YogaClass", null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                YogaCourse yogaClass = new YogaCourse(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("dayOfWeek")),
                        cursor.getString(cursor.getColumnIndexOrThrow("time")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("capacity")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("duration")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description"))
                );
                yogaClasses.add(yogaClass);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return yogaClasses;
    }

    public int deleteYogaClass(int id) {
        return database.delete("YogaClass", "id = ?", new String[]{String.valueOf(id)});
    }

    public void close() {
        database.close();
    }
}

