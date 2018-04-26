package com.example.hp.stickpick.notification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hp.stickpick.android_retrofit.api.Cv;
import com.example.hp.stickpick.bean.UserBean;

/**
 * Created by Shadab Azam Farooqui on 24-Apr-18.
 */

public class NotificationDatabase extends SQLiteOpenHelper {

    public NotificationDatabase(Context context) {
        super(context, Cv.DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(""));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void createTable(SQLiteDatabase db) {
        if (db == null) {
            db = this.getWritableDatabase();
        }
        String query = "create table if not exists " + Cv.TABLE_NOTIFICATION + "("
                + Cv.MOBILE + " TEXT,"
                + Cv.SINGLE_EVENT_COUNT + " TEXT,"
                + Cv.VALUE_EVENT_COUNT + " TEXT)";
        db.execSQL(query);
    }

    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Cv.TABLE_NOTIFICATION);
        db.close();

    }


    public long insertEvents(String mobile, String singleEvent, String valueEvent) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Cv.MOBILE, mobile);
        values.put(Cv.SINGLE_EVENT_COUNT, singleEvent);
        values.put(Cv.VALUE_EVENT_COUNT, valueEvent);
        return db.insert(Cv.TABLE_NOTIFICATION, null, values);
    }

    public boolean updateSingleEvents(String singleEvent) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cv.SINGLE_EVENT_COUNT, singleEvent);
        return db.update(Cv.TABLE_NOTIFICATION, contentValues, Cv.MOBILE + "=" + getMob(), null) > 0;
    }

    public boolean updateValueEvents(String valueEvent) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cv.VALUE_EVENT_COUNT, valueEvent);
        return db.update(Cv.TABLE_NOTIFICATION, contentValues, Cv.MOBILE + "=" + getMob(), null) > 0;
    }

    public boolean isTableExist() {
        String selectQuery = " select * FROM " + Cv.TABLE_NOTIFICATION;
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
        String selectQuery = "SELECT " + Cv.MOBILE + " FROM " + Cv.TABLE_NOTIFICATION;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            mobile = cursor.getString(cursor.getColumnIndex(Cv.MOBILE));
        }
        return mobile;
    }

    public String getEvents() {
        // Select single student Query
        String single = null;
        String value = null;
        String selectQuery = "SELECT * FROM " + Cv.TABLE_NOTIFICATION;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            single = cursor.getString(cursor.getColumnIndex(Cv.SINGLE_EVENT_COUNT));
            value = cursor.getString(cursor.getColumnIndex(Cv.VALUE_EVENT_COUNT));
        }
        return single + "," + value;
    }
}
