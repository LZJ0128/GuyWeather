package com.lzj.guyweather.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 1/20 0020.
 * 用于解析和处理服务器返回的JSON数据
 */
public class JsonHelper {

    /**
     * 解析服务器返回的JSON数据， 并将解析出的数据存储到本地
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context, String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");

            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的数据存储到SharedPreferences文件中
     * @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1,
                                       String temp2, String weatherDesp, String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);//所选城市
        editor.putString("city_name", cityName);//城市名
        editor.putString("weather_code", weatherCode);//天气代号
        editor.putString("temp1", temp1);//温度
        editor.putString("temp2", temp2);//温度
        editor.putString("weather_desp", weatherDesp);//天气
        editor.putString("publish_time", publishTime);//发布时间
        editor.putString("current_time", sdf.format(new Date()));//当前时间
        editor.commit();
    }
}
