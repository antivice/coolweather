package com.example.bmw.coolweather.util;

import com.example.bmw.coolweather.db.CoolWeatherDB;

/**
 * Created by bmw on 2016/10/15.
 */

public class Utility {

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
        return false;
    }

    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        return false;
    }

    public synchronized static boolean handleCountrysResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        return false;
    }
}
