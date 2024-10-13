package com.example.coursework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2; // Cập nhật phiên bản cơ sở dữ liệu
    private static final String DATABASE_NAME = "YogaCourseDB";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng mới
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
                "isSynced INTEGER DEFAULT 0)"); // Thêm cột isSynced mặc định là 0
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Thêm cột isSynced nếu chưa có
            db.execSQL("ALTER TABLE YogaCourse ADD COLUMN isSynced INTEGER DEFAULT 0");
        }
    }
}
