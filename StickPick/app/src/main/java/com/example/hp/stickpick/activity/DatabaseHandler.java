package com.example.hp.stickpick.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hp.stickpick.bean.UserBean;

/**
 * Created by SHADAB AAZAM on 22-07-2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "AutoLogin";
    public static final String MOBILE = "mobile";
    public static final String PASSWORD="password";
    public Cursor cursor;

    public DatabaseHandler(Context context) {
        super(context, "mydb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table AutoLogin (mobile TEXT,password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public long insertStudent(UserBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MOBILE, bean.getMobile());
        values.put(PASSWORD, bean.getPassword());
        return db.insert(TABLE_NAME, null, values);
    }

    public boolean updateStudent(UserBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PASSWORD,bean.getPassword());
        return db.update(TABLE_NAME, contentValues, MOBILE + "=" + bean.getMobile(), null) > 0;
    }


    public Cursor getRecord() {
        // Select single student Query
        String selectQuery = " select mobile , password FROM AutoLogin";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex("mobile"));
            cursor.getString(cursor.getColumnIndex("password"));
        }
        return cursor;
    }


    public boolean isAlreadyLoggedIn() {
        // Select single student Query
        String selectQuery = " select mobile , password FROM AutoLogin";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }


    public String getMob() {
        // Select single student Query
        String mobile = null;
        String selectQuery = "SELECT  mobile FROM AutoLogin";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            mobile = cursor.getString(cursor.getColumnIndex("mobile"));
        }

        return mobile;
    }

    public void deleteTable() {
        String selectQuery = "DELETE FROM AutoLogin";
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.rawQuery(selectQuery, null);
        db.execSQL("delete from " + TABLE_NAME);
        db.close();


    }


}
