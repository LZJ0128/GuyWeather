package com.lzj.guyweather.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzj.guyweather.entity.CityEntity;
import com.lzj.guyweather.entity.CountyEntity;
import com.lzj.guyweather.entity.ProvinceEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/18 0018.
 * 数据库操作帮助类
 */
public class DBOperationHelper {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "guy_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static DBOperationHelper mDBOperationHelper;

    private SQLiteDatabase mDB;


    /**
     * 将构造方法私有化
     */
    private DBOperationHelper(Context context){
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, VERSION);
        mDB = dbOpenHelper.getWritableDatabase();
    }

    /**
     * 获取DBOperationHelper的实例
     */
    public synchronized static DBOperationHelper getInstance(Context context){
        if (mDBOperationHelper == null){
            mDBOperationHelper = new DBOperationHelper(context);
        }
        return mDBOperationHelper;
    }

    /**
     * 将Province实例存储到数据库
     */
    public void saveProvince(ProvinceEntity provinceEntity){
        if (provinceEntity != null){
            ContentValues cv = new ContentValues();
            cv.put("province_name", provinceEntity.getProvinceName());
            cv.put("province_code", provinceEntity.getProvinceCode());
            mDB.insert("Province", null, cv);
        }
    }

    /**
     * 从数据库读取全国所有的Province信息
     */
    public List<ProvinceEntity> loadProvinces(){
        List<ProvinceEntity> provinceList = new ArrayList<ProvinceEntity>();
        String sql = "select * from province";
        Cursor cursor = mDB.rawQuery(sql, null);
        if (cursor.moveToFirst()){
            while (cursor.moveToNext()){
                ProvinceEntity entity = new ProvinceEntity();
                //先根据key得到列下标，进而得到该列的value
                entity.setProvinceId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                entity.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(entity);
            }
        }
        cursor.close();
        return provinceList;
    }

    /**
     * 将City实例存储到数据库
     */
    public void saveCity(CityEntity cityEntity){
        if (cityEntity != null){
            ContentValues cv = new ContentValues();
            cv.put("city_name", cityEntity.getCityName());
            cv.put("city_code", cityEntity.getCityCode());
            cv.put("province_id", cityEntity.getProvinceId());
            mDB.insert("city", null, cv);
        }
    }

    /**
     * 从数据库读取全国所有的City信息
     */
    public List<CityEntity> loadCities(int provinceId){
        List<CityEntity> cityList = new ArrayList<CityEntity>();
        String sql = "select * from city where province_id=?";
        Cursor cursor = mDB.rawQuery(sql, new String[]{String.valueOf(provinceId)});
        if (cursor.moveToFirst()){
            while (cursor.moveToNext()){
                CityEntity entity = new CityEntity();
                entity.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                entity.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                cityList.add(entity);
            }
        }
        cursor.close();
        return cityList;
    }

    /**
     * 将County实例存储到数据库
     */
    public void saveCounty(CountyEntity countyEntity){
        if (countyEntity != null){
            ContentValues cv = new ContentValues();
            cv.put("county_name", countyEntity.getCountyName());
            cv.put("county_code", countyEntity.getCountyCode());
            cv.put("city_id", countyEntity.getCityId());
            mDB.insert("County", null, cv);
        }
    }


    /**
     * 从数据库读取全国所有的County信息
     */
    public List<CountyEntity> loadCounties(int cityId){
        List<CountyEntity> countyList = new ArrayList<CountyEntity>();
        String sql = "select * from county where city_id=?";
        Cursor cursor = mDB.rawQuery(sql, new String[]{String.valueOf(cityId)});
        if (cursor.moveToFirst()){
            while (cursor.moveToNext()){
                CountyEntity entity = new CountyEntity();
                entity.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
                entity.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                entity.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                countyList.add(entity);
            }
        }
        cursor.close();
        return countyList;
    }

}
