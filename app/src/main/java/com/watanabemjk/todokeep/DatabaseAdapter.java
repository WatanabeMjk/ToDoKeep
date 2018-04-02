package com.watanabemjk.todokeep;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseAdapter {
    static final int DATABASE_VERSION = 1;
    static final String TABLE_NAME = "notes";
    static final String COL_ID = "_id";
    static final String COL_YEAR = "year";
    static final String COL_MONTH = "month";
    static final String COL_DAY = "day";
    static final String COL_HOUR = "hour";
    static final String COL_MINUTE = "minute";
    static final String COL_DAYOFWEEK = "dayOfWeek";
    static final String COL_TITLE = "title";
    static final String COL_DETAIL = "detail";
    static final String COL_BACKCOLOR = "backColor";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    DatabaseAdapter(final Context context, String DATABASE_NAME){
        this.dbHelper = new DatabaseHelper(context,DATABASE_NAME);
    }

    void open() {
        this.database = this.dbHelper.getWritableDatabase();
    }

    void close(){
        this.dbHelper.close();
    }

    void deleteAllNotes(){
        database.delete(TABLE_NAME, null, null);
    }

    void deleteNote(int id){
        database.delete(TABLE_NAME, COL_ID + "=" + id, null);
    }

    void editDate(int id, int year, int month, int day, String dayOfWeek){
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_YEAR, year);
        values.put(COL_MONTH, month);
        values.put(COL_DAY, day);
        values.put(COL_DAYOFWEEK, dayOfWeek);

        String where =  COL_ID + "=?";
        String[] args = { String.valueOf(id) };
        database.update(TABLE_NAME, values, where, args);
    }

    void editTime(int id, int hour, int minute){
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HOUR, hour);
        values.put(COL_MINUTE, minute);

        String where =  COL_ID + "=?";
        String[] args = { String.valueOf(id) };
        database.update(TABLE_NAME, values, where, args);
    }

    void editPlan(int id, String title, String detail, int radioId) {
        int backColorId;
        switch (radioId){
            case R.id.first:
                backColorId = R.color.listview_back_white;
                break;
            case R.id.second:
                backColorId = R.color.listview_back_red;
                break;
            case R.id.third:
                backColorId = R.color.listview_back_blue;
                break;
            case R.id.fourth:
                backColorId = R.color.listview_back_orange;
                break;
            case R.id.fifth:
                backColorId = R.color.listview_back_green;
                break;
            case R.id.sixth:
                backColorId = R.color.listview_back_pink;
                break;
            case R.id.seventh:
                backColorId = R.color.listview_back_yellow;
                break;
            default:
                Log.d("デフォルト","");
                backColorId = R.color.listview_back_yellow;
                break;
        }
        Log.d("DatabaseAdapter", " / title : " + title + " / detail : " + detail + " / radioId : " + radioId + " / backColorId : " + backColorId);
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_DETAIL, detail);
        values.put(COL_BACKCOLOR, backColorId);

        String where =  COL_ID + "=?";
        String[] args = { String.valueOf(id) };
        database.update(TABLE_NAME, values, where, args);
    }

    Cursor getAllNotes(){
        return database.query(TABLE_NAME, null, null, null, null, null, null);
    }

    void saveNote(int year, int month, int day, int hour, int minute, String dayOfWeek, String title, String detail, int backColorId){
        ContentValues values = new ContentValues();
        values.put(COL_YEAR, year);
        values.put(COL_MONTH, month);
        values.put(COL_DAY, day);
        values.put(COL_HOUR, hour);
        values.put(COL_MINUTE, minute);
        values.put(COL_DAYOFWEEK, dayOfWeek);
        values.put(COL_TITLE, title);
        values.put(COL_DETAIL, detail);
        values.put(COL_BACKCOLOR, backColorId);
        this.database.insertOrThrow(TABLE_NAME, null, values);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context, String DATABASE_NAME) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE_NAME + "("
                            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COL_YEAR + " INTEGER NOT NULL,"
                            + COL_MONTH + " INTEGER NOT NULL,"
                            + COL_DAY + " INTEGER NOT NULL,"
                            + COL_HOUR + " INTEGER NOT NULL,"
                            + COL_MINUTE + " INTEGER NOT NULL,"
                            + COL_DAYOFWEEK + " TEXT NOT NULL,"
                            + COL_TITLE + " TEXT NOT NULL,"
                            + COL_DETAIL + " TEXT NOT NULL,"
                            + COL_BACKCOLOR + " INTEGER NOT NULL );"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        }
    }
}
