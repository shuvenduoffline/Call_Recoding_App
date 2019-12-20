package com.shuvenduoffline.callrecoding.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shuvenduoffline.callrecoding.datamodel.CallLog;

import java.util.ArrayList;
import java.util.Collections;

public class Database extends SQLiteOpenHelper {

    public static String NAME = "callRecorder";
    public static int VERSION = 1;

    String CREATE_CALL_RECORDS_TABLE = "CREATE TABLE records(_id INTEGER PRIMARY KEY,name TEXT, phone_number TEXT, start_date_time INTEGER, end_date_time INTEGER, path_to_recording TEXT )";
    public static String CALL_RECORDS_TABLE = "records";
    public static String CALL_RECORDS_TABLE_ID = "_id";
    public static String CALL_RECORDS_TABLE_PHONE_NUMBER = "phone_number";
    public static String CALL_RECORDS_TABLE_NAME = "name";
    public static String CALL_RECORDS_TABLE_START_DATE = "start_date_time";
    public static String CALL_RECORDS_TABLE_END_DATE = "end_date_time";
    public static String CALL_RECORDS_TABLE_RECORDING_PATH = "path_to_recording";


    private static Database instance;

    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context.getApplicationContext());
        }
        return instance;
    }

    private Database(Context context) {
        super(context, Database.NAME, null, Database.VERSION);
    }

    @Override
    public synchronized void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_CALL_RECORDS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private CallLog getCallLogFrom(Cursor cursor) {
        CallLog phoneCall = new CallLog();


        // String[] columnNames = cursor.getColumnNames();

        int index = cursor.getColumnIndex(CALL_RECORDS_TABLE_ID);
        phoneCall.setId(cursor.getInt(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_PHONE_NUMBER);
        phoneCall.setPhonenumber(cursor.getString(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_START_DATE);
        phoneCall.setStart_time(cursor.getLong(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_END_DATE);
        phoneCall.setEnd_time(cursor.getLong(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_RECORDING_PATH);
        phoneCall.setFilepath(cursor.getString(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_NAME);
        phoneCall.setName(cursor.getString(index));

        return phoneCall;
    }


    public synchronized ArrayList<CallLog> getAllCalls() {
        ArrayList<CallLog> array_list = new ArrayList<CallLog>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + Database.CALL_RECORDS_TABLE, null);
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                CallLog phoneCall = getCallLogFrom(cursor);
                array_list.add(phoneCall);
                cursor.moveToNext();
            }
            Collections.reverse(array_list);
            return array_list;
        } finally {
            db.close();
        }
    }


    public synchronized boolean addCall(CallLog phoneCall) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            ContentValues contentValues = new ContentValues();

            contentValues.put(CALL_RECORDS_TABLE_NAME, phoneCall.getName());
            contentValues.put(CALL_RECORDS_TABLE_PHONE_NUMBER, phoneCall.getPhonenumber());
            contentValues.put(CALL_RECORDS_TABLE_START_DATE, phoneCall.getStart_time());
            contentValues.put(CALL_RECORDS_TABLE_END_DATE, phoneCall.getEnd_time());
            contentValues.put(CALL_RECORDS_TABLE_RECORDING_PATH, phoneCall.getFilepath());


            long rowId = db.insert(Database.CALL_RECORDS_TABLE, null, contentValues);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public synchronized boolean updateCall(CallLog phoneCall) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();

            contentValues.put(CALL_RECORDS_TABLE_NAME, phoneCall.getName());
            contentValues.put(CALL_RECORDS_TABLE_PHONE_NUMBER, phoneCall.getPhonenumber());
            contentValues.put(CALL_RECORDS_TABLE_START_DATE, phoneCall.getStart_time());
            contentValues.put(CALL_RECORDS_TABLE_END_DATE, phoneCall.getEnd_time());
            contentValues.put(CALL_RECORDS_TABLE_RECORDING_PATH, phoneCall.getFilepath());

            db.update(Database.CALL_RECORDS_TABLE, contentValues, CALL_RECORDS_TABLE_ID + "=" + phoneCall.getId(), null);
            return true;
        } finally {
            db.close();
        }
    }

    public synchronized boolean removeCall(CallLog phoneCall) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(Database.CALL_RECORDS_TABLE, CALL_RECORDS_TABLE_ID + "=" + phoneCall.getId(), null);
            return true;
        } finally {
            db.close();
        }
    }

    public synchronized int count() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            int numRows = (int) DatabaseUtils.queryNumEntries(db, Database.CALL_RECORDS_TABLE);
            return numRows;
        } finally {
            db.close();
        }
    }

    public synchronized CallLog getCall(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + Database.CALL_RECORDS_TABLE + " where " + Database.CALL_RECORDS_TABLE_ID + "=" + id, null);
            if (!cursor.moveToFirst()) return null; // does not exist
            return getCallLogFrom(cursor);
        } finally {
            db.close();
        }
    }


}