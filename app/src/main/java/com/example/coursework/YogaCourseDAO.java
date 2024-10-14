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
        openDatabase();
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
    private void openDatabase() {
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    // Cập nhật thông tin khóa học trong cơ sở dữ liệu
    public int updateYogaCourse(YogaCourse yogaCourse) {
        openDatabase();
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

    public List<ClassInstance> getClassInstancesByCourseId(int courseId) {
        List<ClassInstance> classInstanceList = new ArrayList<>();

        // Lấy readable database từ dbHelper
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Thực hiện truy vấn
        Cursor cursor = db.rawQuery("SELECT * FROM ClassInstances WHERE courseId = ? ORDER BY strftime('%Y-%m-%d', substr(date, 7, 4) || '-' || substr(date, 4, 2) || '-' || substr(date, 1, 2)) ASC", new String[]{String.valueOf(courseId)});

        if (cursor.moveToFirst()) {
            do {
                ClassInstance classInstance = new ClassInstance();
                classInstance.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                classInstance.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                classInstance.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                classInstance.setTeacher(cursor.getString(cursor.getColumnIndexOrThrow("teacher")));
                classInstance.setComments(cursor.getString(cursor.getColumnIndexOrThrow("comments")));
                classInstanceList.add(classInstance);
            } while (cursor.moveToNext());
        }

        // Đóng cursor sau khi dùng xong, nhưng không đóng database
        cursor.close();

        return classInstanceList;
    }


    public long addClassInstance(int courseId, String name, String date, String teacher, String comments) {
        ContentValues values = new ContentValues();
        values.put("courseId", courseId);
        values.put("name", name);
        values.put("date", date);
        values.put("teacher", teacher);
        values.put("comments", comments);

        // Thêm vào cơ sở dữ liệu
        return database.insert("ClassInstances", null, values);
    }

    public int updateClassInstance(ClassInstance classInstance) {
        openDatabase();
        ContentValues values = new ContentValues();

        values.put("name", classInstance.getName());
        values.put("date", classInstance.getDate());
        values.put("teacher", classInstance.getTeacher());
        values.put("comments", classInstance.getComments());

        // Update the row in the database where the ID matches
        return database.update("ClassInstances", values, "id = ?", new String[]{String.valueOf(classInstance.getId())});

    }
    public void deleteClassInstance(int classInstanceId) {
        openDatabase(); // Đảm bảo cơ sở dữ liệu được mở trước khi thao tác
        database.delete("ClassInstances", "id = ?", new String[]{String.valueOf(classInstanceId)});
    }
    // Phương thức để đánh dấu khóa học là chưa đồng bộ
    public void markCourseAsNotSynced(int courseId) {
        ContentValues values = new ContentValues();
        values.put("isSynced", 0); // 0 là chưa đồng bộ
        database.update("YogaCourse", values, "id = ?", new String[]{String.valueOf(courseId)});
    }





    // Đóng kết nối cơ sở dữ liệu
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

}
