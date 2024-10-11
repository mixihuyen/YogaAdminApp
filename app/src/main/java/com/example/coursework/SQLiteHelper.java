package com.example.coursework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "yoga_courses.db";
    private static final int DATABASE_VERSION = 3; // Update the version to trigger onUpgrade

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE YogaCourse (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "dayOfWeek TEXT, " +
                "time TEXT, " +
                "capacity INTEGER, " +
                "duration INTEGER, " +
                "price REAL, " +
                "type TEXT, " +
                "description TEXT, " +
                "imageUrl TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists
        db.execSQL("DROP TABLE IF EXISTS YogaCourse");
        // Create a new one
        onCreate(db);
    }
}
