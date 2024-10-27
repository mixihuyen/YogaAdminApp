package com.example.coursework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    public void syncDataFromFirestoreIfNeeded(Runnable onComplete) {
        openDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM YogaCourse", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();

        if (count == 0) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Log.d("SyncStatus", "Starting sync from Firestore.");

            firestore.collection("courses").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Log.d("SyncStatus", "Fetched " + queryDocumentSnapshots.size() + " documents.");
                        } else {
                            Log.d("SyncStatus", "No documents found in Firestore collection.");
                        }

                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            String dayOfWeek = document.getString("dayOfWeek");
                            String time = document.getString("time");
                            Long capacity = document.getLong("capacity");
                            Long duration = document.getLong("duration");
                            Double price = document.getDouble("price");
                            String type = document.getString("type");
                            String description = document.getString("description");
                            String imageUrl = document.getString("imageUrl");


                            if (dayOfWeek != null && time != null && capacity != null && duration != null && price != null && type != null) {
                                long courseId = insertYogaCourse(dayOfWeek, time, capacity.intValue(), duration.intValue(), price, type, description, imageUrl);
                                Log.d("SyncStatus", "Inserted course: " + type);

                                // Synchronize class instances for each course
                                document.getReference().collection("class_instances").get()
                                        .addOnSuccessListener(classInstances -> {
                                            for (DocumentSnapshot classDoc : classInstances) {
                                                String name = classDoc.getString("name");
                                                String date = classDoc.getString("date");
                                                String teacher = classDoc.getString("teacher");
                                                String comments = classDoc.getString("comments");

                                                if (name != null && date != null && teacher != null) {
                                                    addClassInstance((int) courseId, name, date, teacher, comments);
                                                    Log.d("SyncStatus", "Inserted class instance: " + name + " for course " + type);
                                                } else {
                                                    Log.d("SyncStatus", "Class instance missing some fields, skipping.");
                                                }
                                            }
                                            YogaCourse course = getYogaCourseById((int) courseId);
                                            course.setSynced(true);
                                            updateYogaCourse(course); // Lưu lại trạng thái đồng bộ vào SQLite
                                        })
                                        .addOnFailureListener(e -> Log.e("SyncError", "Failed to fetch class instances", e));
                            } else {
                                Log.d("SyncStatus", "Document missing some fields, skipping.");
                            }
                        }

                        // Call onComplete callback after synchronization is finished
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("SyncError", "Failed to sync data from Firestore", e);
                        // Ensure onComplete is called even if sync fails
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    });
        } else {
            Log.d("SyncStatus", "Database already has data, skipping sync.");
            if (onComplete != null) {
                onComplete.run(); // Call onComplete immediately if no sync is needed
            }
        }
    }








    // Đóng kết nối cơ sở dữ liệu
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }
}
