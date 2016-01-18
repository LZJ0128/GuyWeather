package com.lzj.guyweather.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 1/18 0018.
 */
public class DbHelper extends SQLiteOpenHelper {

    /**
     * Province建表语句
     */
    public static final String CREATE_PROVINCE = "create table province (" +
            "id integer primary key autoincrement, " +
            "province_name text, " +
            "province_code text)";

    /**
     * City建表语句
     */
    public static final String CREATE_CITY = "create table city (" +
            "id integer primary key autoincrement, " +
            "city_name text," +
            "city_code text, " +
            "province_id integer)";

    /**
     * County建表语句
     */
    public static final String CREATE_County = "create table county (" +
            "id integer primary key autoincrement, " +
            "county_name text," +
            "county_code text," +
            "city_id integer)";

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db){
        //创建三张表
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_County);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
