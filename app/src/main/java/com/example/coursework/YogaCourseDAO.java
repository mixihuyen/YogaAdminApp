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

    // Thêm khóa học vào cơ sở dữ liệu với trạng thái isSynced mặc định là false
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
        values.put("isSynced", 0); // Đặt trạng thái chưa đồng bộ

        return database.insert("YogaCourse", null, values);
    }


    // Lấy tất cả khóa học từ cơ sở dữ liệu
    public List<YogaCourse> getAllYogaCourses() {
        List<YogaCourse> yogaCourses = new ArrayList<>();
        Cursor cursor = database.query("YogaCourse", null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Lấy dữ liệu và chuyển đổi isSynced thành boolean
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow("dayOfWeek"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));
                boolean isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced")) == 1; // Chuyển đổi giá trị số thành boolean

                YogaCourse yogaCourse = new YogaCourse(id, dayOfWeek, time, capacity, duration, price, type, description, imageUrl, isSynced);
                yogaCourses.add(yogaCourse);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return yogaCourses;
    }

    // Cập nhật thông tin khóa học trong cơ sở dữ liệu
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
        values.put("isSynced", yogaCourse.isSynced() ? 1 : 0); // Cập nhật trạng thái isSynced

        return database.update("YogaCourse", values, "id = ?", new String[]{String.valueOf(yogaCourse.getId())});
    }


    public int deleteYogaCourse(int id) {
        return database.delete("YogaCourse", "id = ?", new String[]{String.valueOf(id)});
    }

    // Lấy khóa học theo ID
    public YogaCourse getYogaCourseById(int id) {
        Cursor cursor = database.query("YogaCourse", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            // Lấy dữ liệu và chuyển đổi isSynced thành boolean
            String dayOfWeek = cursor.getString(cursor.getColumnIndexOrThrow("dayOfWeek"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
            int capacity = cursor.getInt(cursor.getColumnIndexOrThrow("capacity"));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));
            boolean isSynced = cursor.getInt(cursor.getColumnIndexOrThrow("isSynced")) == 1; // Chuyển đổi giá trị số thành boolean

            YogaCourse yogaCourse = new YogaCourse(id, dayOfWeek, time, capacity, duration, price, type, description, imageUrl, isSynced);
            cursor.close();
            return yogaCourse;
        }
        return null;
    }

    // Đóng kết nối cơ sở dữ liệu
    public void close() {
        database.close();
    }
}
