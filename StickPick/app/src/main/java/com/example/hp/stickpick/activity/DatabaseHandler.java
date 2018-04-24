package com.example.hp.stickpick.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hp.stickpick.android_retrofit.api.Cv;
import com.example.hp.stickpick.bean.UserBean;

/**
 * Created by SHADAB AAZAM on 22-07-2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, Cv.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable());
    }

    private String createTable(){
        String query="create table "+Cv.TABLE_LOGIN+ "("
                +Cv.MOBILE+" TEXT,"
                +Cv.PASSWORD+" TEXT)";
        return  query;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public long insertStudent(UserBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Cv.MOBILE, bean.getMobile());
        values.put(Cv.PASSWORD, bean.getPassword());
        return db.insert(Cv.TABLE_LOGIN, null, values);
    }

    public boolean updateStudent(UserBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Cv.PASSWORD,bean.getPassword());
        return db.update(Cv.TABLE_LOGIN, contentValues, Cv.MOBILE + "=" + bean.getMobile(), null) > 0;
    }




    public boolean isAlreadyLoggedIn() {
        // Select single student Query
        String selectQuery = " select "+Cv.MOBILE+" , "+Cv.PASSWORD+" FROM "+Cv.TABLE_LOGIN;
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
        String selectQuery = "SELECT "+Cv.MOBILE+" FROM "+Cv.TABLE_LOGIN;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            mobile = cursor.getString(cursor.getColumnIndex(Cv.MOBILE));
        }

        return mobile;
    }

    public void deleteTable() {
//        String selectQuery = "DELETE FROM AutoLogin";
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor cursor = db.rawQuery(selectQuery, null);
        db.execSQL("delete from " + Cv.TABLE_LOGIN);
        db.close();


    }

    public Cursor getRecord() {
        // Select single student Query
        String selectQuery = " select "+Cv.MOBILE+" , "+Cv.PASSWORD+" FROM "+Cv.TABLE_LOGIN;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            cursor.getString(cursor.getColumnIndex(Cv.MOBILE));
            cursor.getString(cursor.getColumnIndex(Cv.PASSWORD));
        }
        return cursor;
    }

}
