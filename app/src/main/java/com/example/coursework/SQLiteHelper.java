package com.example.coursework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "YogaCourseDB";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng YogaCourse
        db.execSQL("CREATE TABLE YogaCourse (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "dayOfWeek TEXT, " +
                "time TEXT, " +
                "capacity INTEGER, " +
                "duration INTEGER, " +
                "price REAL, " +
                "type TEXT, " +
                "description TEXT, " +
                "imageUrl TEXT, " +
                "isSynced INTEGER DEFAULT 0)");

        // Tạo bảng ClassInstances
        db.execSQL("CREATE TABLE ClassInstances (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "courseId INTEGER, " +
                "date TEXT, " +
                "name TEXT, " +
                "teacher TEXT, " +
                "comments TEXT, " +
                "FOREIGN KEY(courseId) REFERENCES YogaCourse(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
