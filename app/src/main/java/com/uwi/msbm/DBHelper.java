package com.uwi.msbm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.uwi.msbm.models.Course;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by shane on 11/22/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "msbm.db";
    public static final String COURSES_TABLE_NAME = "courses";

    public static final String COURSES_COLUMN_ID = "id";
    public static final String COURSES_COLUMN_CODE = "code";
    public static final String COURSES_COLUMN_SHORT_NAME = "short_name";
    public static final String COURSES_COLUMN_FULL_NAME = "full_name";
    public static final String COURSES_COLUMN_PARTICIPANT_COUNT = "participant_count";


    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table contacts (" +
                        COURSES_COLUMN_ID + " integer primary key, " +
                        COURSES_COLUMN_CODE + " integer, " +
                        COURSES_COLUMN_SHORT_NAME + " text, " +
                        COURSES_COLUMN_FULL_NAME + " text, " +
                        COURSES_COLUMN_PARTICIPANT_COUNT + " integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact(int code, String shortName, String fullName, int participantCount)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COURSES_COLUMN_CODE, code);
        contentValues.put(COURSES_COLUMN_SHORT_NAME, shortName);
        contentValues.put(COURSES_COLUMN_FULL_NAME, fullName);
        contentValues.put(COURSES_COLUMN_PARTICIPANT_COUNT, participantCount);

        db.insert(COURSES_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + COURSES_TABLE_NAME + " where id=" + id + "", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, COURSES_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String shortName, String code)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COURSES_COLUMN_SHORT_NAME, shortName);
        contentValues.put(COURSES_COLUMN_CODE, code);

        db.update(COURSES_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(COURSES_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<Course> getAllCourses()
    {
        ArrayList<Course> courseList = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + COURSES_TABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            Course course = new Course();

            course.setId(res.getInt(res.getColumnIndex(COURSES_COLUMN_ID)));
            course.setCode(res.getInt(res.getColumnIndex(COURSES_COLUMN_CODE)));
            course.setShortName(res.getString(res.getColumnIndex(COURSES_COLUMN_SHORT_NAME)));
            course.setFullName(res.getString(res.getColumnIndex(COURSES_COLUMN_FULL_NAME)));
            course.setParticipantCount(res.getInt(res.getColumnIndex(COURSES_COLUMN_PARTICIPANT_COUNT)));

            courseList.add(course);

            res.moveToNext();
        }

        res.close();

        return courseList;
    }
}