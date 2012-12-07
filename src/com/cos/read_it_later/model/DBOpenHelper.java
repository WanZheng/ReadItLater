package com.cos.read_it_later.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBOpenHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "read_it_later.db";
    private static final int DB_VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Provider.TABLE_NAME + "(" +
                "_id INTEGER PRIMARY KEY," +
                "url TEXT," +
                "title TEXT," +
                "comment TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        while (++oldVersion <= newVersion) {
            switch (oldVersion) {
                /* Fill codes here */
                default:
                    break;
            }
        }
    }
}
