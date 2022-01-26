package com.example.igallery.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    private static String name = "favorite.sqlite";
    private static int version = 1;

    public Database(@Nullable Context context) {
        super(context, name, null, version);
    }

    // truy van khong tra ket qua: CREATE, INSERT, DELETE, UPDATE...
    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    //truy van co tra ket qua:SELECT

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Favorite(Id INTEGER PRIMARY KEY AUTOINCREMENT, Path VARCHAR(200), dateAdded INTEGER, duration INTEGER, idMedia CHAR(100))");
        db.execSQL("CREATE TABLE IF NOT EXISTS Album(Id INTEGER PRIMARY KEY AUTOINCREMENT, AlbumName VARCHAR(200), idMedia CHAR(100), path VARCHAR(200), dateAdded LONG, duration LONG)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Search(Id INTEGER PRIMARY KEY AUTOINCREMENT, Text NVARCHAR(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion<2){
            updateToVersion2(db);
        }
    }

    private void updateToVersion2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE Favorite ADD COLUMN tnColorBg TEXT DEFAULT \"#ffffff\"");
    }
}
