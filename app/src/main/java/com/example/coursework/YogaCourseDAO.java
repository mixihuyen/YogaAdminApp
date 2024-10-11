package com.example.coursework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class YogaCourseDAO {
    private SQLiteHelper dbHelper;
    private SQLiteDatabase database;

    public YogaCourseDAO(Context context) {
        dbHelper = new SQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long insertYogaCourse(String dayOfWeek, String time, int capacity, int duration, double price, String type, String description, String imageUrl) {
        ContentValues values = new ContentValues();
        values.put("dayOfWeek", dayOfWeek);
        values.put("time", time);
        values.put("capacity", capacity);
        values.put("duration", duration);
        values.put("price", price);
        values.put("type", type);
        values.put("description", description);
        values.put("imageUrl", imageUrl);

        return database.insert("YogaCourse", null, values);
    }

    public List<YogaCourse> getAllYogaCourses() {
        List<YogaCourse> yogaCourses = new ArrayList<>();
        Cursor cursor = database.query("YogaCourse", null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                YogaCourse yogaCourse = new YogaCourse(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("dayOfWeek")),
                        cursor.getString(cursor.getColumnIndexOrThrow("time")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("capacity")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("duration")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("type")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
                );
                yogaCourses.add(yogaCourse);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return yogaCourses;
    }

    public int updateYogaCourse(YogaCourse yogaCourse) {
        ContentValues values = new ContentValues();
        values.put("dayOfWeek", yogaCourse.getDayOfWeek());
        values.put("time", yogaCourse.getTime());
        values.put("capacity", yogaCourse.getCapacity());
        values.put("duration", yogaCourse.getDuration());
        values.put("price", yogaCourse.getPrice());
        values.put("type", yogaCourse.getType());
        values.put("description", yogaCourse.getDescription());
        values.put("imageUrl", yogaCourse.getImageUrl());

        return database.update("YogaCourse", values, "id = ?", new String[]{String.valueOf(yogaCourse.getId())});
    }

    public int deleteYogaCourse(int id) {
        return database.delete("YogaCourse", "id = ?", new String[]{String.valueOf(id)});
    }

    public YogaCourse getYogaCourseById(int id) {
        Cursor cursor = database.query("YogaCourse", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            YogaCourse yogaCourse = new YogaCourse(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("dayOfWeek")),
                    cursor.getString(cursor.getColumnIndexOrThrow("time")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("capacity")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("duration")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"))
            );
            cursor.close();
            return yogaCourse;
        }
        return null;
    }

    public void close() {
        database.close();
    }
}
