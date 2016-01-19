package com.lzj.guyweather.util;

import android.text.TextUtils;

import com.lzj.guyweather.entity.CityEntity;
import com.lzj.guyweather.entity.CountyEntity;
import com.lzj.guyweather.entity.ProvinceEntity;
import com.lzj.guyweather.helper.DBOperationHelper;

/**
 * Created by Administrator on 1/19 0019.
 * 服务器返回的数据是“代号|城市,代号|城市”格式的，提供此类来解析和处理这种数据
 */
public class Utility {

    /**
     * 解析和处理服务器返回的Province数据并保存到数据库
     */
    public synchronized static boolean handleProvincesResponse(DBOperationHelper dbOperationHelper, String response){

        if (!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if (allProvinces.length > 0){
                for (String p : allProvinces){
                    String[] array = p.split("\\|");
                    ProvinceEntity provinceEntity = new ProvinceEntity();
                    provinceEntity.setProvinceCode(array[0]);
                    provinceEntity.setProvinceName(array[1]);
                    //将解析出来的数据存储到ProvinceEntity类
                    dbOperationHelper.saveProvince(provinceEntity);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的City数据并保存到数据库
     */
    public static boolean handleCityResponse(DBOperationHelper dbOperationHelper, String response, int provinceId){

        if (!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if (allCities.length > 0){
                for (String c : allCities){
                    String[] array = c.split("\\|");
                    CityEntity cityEntity = new CityEntity();
                    cityEntity.setCityCode(array[0]);
                    cityEntity.setCityName(array[1]);
                    cityEntity.setProvinceId(provinceId);
                    //将解析出来的数据存储到CityEntity类
                    dbOperationHelper.saveCity(cityEntity);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的County数据并保存到数据库
     */
    public static boolean handleCountyResponse(DBOperationHelper dbOperationHelper, String response, int cityId){

        if (!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if (allCounties.length > 0){
                for (String c : allCounties){
                    String[] array = c.split("\\|");
                    CountyEntity countyEntity = new CountyEntity();
                    countyEntity.setCountyCode(array[0]);
                    countyEntity.setCountyName(array[1]);
                    countyEntity.setCityId(cityId);
                    //将解析出来的数据存储到CountyEntity类
                    dbOperationHelper.saveCounty(countyEntity);
                }
                return true;
            }
        }
        return false;
    }
}
