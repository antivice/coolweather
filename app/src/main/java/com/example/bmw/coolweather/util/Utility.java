package com.example.bmw.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.bmw.coolweather.activity.CityWeatherActivity;
import com.example.bmw.coolweather.db.CoolWeatherDB;
import com.example.bmw.coolweather.model.City;
import com.example.bmw.coolweather.model.County;
import com.example.bmw.coolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * Created by bmw on 2016/10/15.
 */

public class Utility {

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            try {
                //ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(response.getBytes());
                XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser=factory.newPullParser();
                xmlPullParser.setInput(new StringReader(response));
                int eventType=xmlPullParser.getEventType();
                String provinceName="";
                String provinceCode="";
                while(eventType!=XmlPullParser.END_DOCUMENT){
                    String nodeName=xmlPullParser.getName();
                    switch (eventType){
                        case XmlPullParser.START_TAG:{
                            if("city".equals(nodeName)){
                                provinceCode=xmlPullParser.getAttributeValue(1);//对应"pyName"
                                provinceName=xmlPullParser.getAttributeValue(0);//对应"quName"
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG: {
                            if ("city".equals(nodeName)&& !TextUtils.isEmpty(provinceName)  && !TextUtils.isEmpty(provinceCode)) {
                                Province province=new Province();
                                province.setProvinceName(provinceName);
                                province.setProvinceCode(provinceCode);
                                coolWeatherDB.saveProvince(province);
                                provinceCode="";
                                provinceName="";
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    eventType=xmlPullParser.next();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try{
                XmlPullParserFactory xmlPullParserFactory=XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser=xmlPullParserFactory.newPullParser();
                xmlPullParser.setInput(new StringReader(response));
                int eventType=xmlPullParser.getEventType();
                String cityName="";
                String cityCode="";
                String cityPyname="";
                while (eventType!=XmlPullParser.END_DOCUMENT){
                    String nodeName=xmlPullParser.getName();
                    switch (eventType){
                        case XmlPullParser.START_TAG:{
                            if ("city".equals(nodeName)){
                                cityName=xmlPullParser.getAttributeValue(2);//对应"cityname"
                                cityPyname=xmlPullParser.getAttributeValue(5);//对应"pyname"
                                cityCode=xmlPullParser.getAttributeValue(17);//对应"url=10125010"
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:{
                            if ("city".equals(nodeName) && !TextUtils.isEmpty(cityName) && !TextUtils.isEmpty(cityCode) &&!TextUtils.isEmpty(cityPyname)){
                                City city=new City();
                                city.setCityCode(cityCode);
                                city.setCityName(cityName);
                                city.setCityPyName(cityPyname);
                                city.setProvinceId(provinceId);
                                coolWeatherDB.saveCity(city);
                                cityName="";
                                cityCode="";
                                cityPyname="";
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    eventType=xmlPullParser.next();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try{
                XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser=factory.newPullParser();
                xmlPullParser.setInput(new StringReader(response));
                int eventType=xmlPullParser.getEventType();
                String countyName="";
                String countyCode="";
                String countyPyname="";
                while (eventType!=XmlPullParser.END_DOCUMENT){
                    String nodeName=xmlPullParser.getName();
                    switch (eventType){
                        case XmlPullParser.START_TAG:{
                            if ("city".equals(nodeName)){
                                countyName=xmlPullParser.getAttributeValue(2);
                                countyCode=xmlPullParser.getAttributeValue(17);
                                countyPyname=xmlPullParser.getAttributeValue(5);
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:{
                            if ("city".equals(nodeName) && !TextUtils.isEmpty(countyCode) && !TextUtils.isEmpty(countyName)){
                                County county=new County();
                                county.setCountyName(countyName);
                                county.setCountyCode(countyCode);
                                county.setCountyPyName(countyPyname);
                                county.setCityId(cityId);
                                coolWeatherDB.saveCounty(county);
                                countyName="";
                                countyCode="";
                                countyPyname="";
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    eventType=xmlPullParser.next();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public synchronized static boolean handleWeatherResponse(Context context,String response){
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("HeWeather data service 3.0");
                JSONObject subJsonObject=jsonArray.getJSONObject(0);
                String status=subJsonObject.getString("status");
                if ("ok".equals(status)){
                    //解析出当天天气
                    JSONArray dailyForecastSubJsonArray=subJsonObject.getJSONArray("daily_forecast");

                    //此处目前只解析第一天的天气预报，可使用 for (int i=0;i<dailyForecastSubJsonArray.length();i++)解析未来七天的天气预报
                    JSONObject currentWeatherJsonObject=dailyForecastSubJsonArray.getJSONObject(0);
                    String minTmp=currentWeatherJsonObject.getJSONObject("tmp").getString("min");
                    String maxTmp=currentWeatherJsonObject.getJSONObject("tmp").getString("max");
                    String condFirst=currentWeatherJsonObject.getJSONObject("cond").getString("txt_d");
                    String condSecond=currentWeatherJsonObject.getJSONObject("cond").getString("txt_n");
                    String date=currentWeatherJsonObject.getString("date");

                    JSONObject basicJsonObject=subJsonObject.getJSONObject("basic");
                    String publishText=basicJsonObject.getJSONObject("update").getString("loc");
                    String weatherCond="";
                    if (condFirst.equals(condSecond)){
                        weatherCond=condFirst;
                    }else {
                        weatherCond=condFirst+"转"+condSecond;
                    }

                    saveWeatherInfo(context,publishText,date,weatherCond,minTmp,maxTmp);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public synchronized static boolean handleCurrentLocationWeatherResponse(Context context,String response){
        boolean bResult=false;
        if(TextUtils.isEmpty(response)){
            return false;
        }
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject subJsonObject=jsonArray.getJSONObject(0);
            String status=subJsonObject.getString("status");
            if ("ok".equals(status)){
                //解析出当天天气
                JSONArray dailyForecastSubJsonArray=subJsonObject.getJSONArray("daily_forecast");

                //此处目前只解析第一天的天气预报，可使用 for (int i=0;i<dailyForecastSubJsonArray.length();i++)解析未来七天的天气预报
                JSONObject currentWeatherJsonObject=dailyForecastSubJsonArray.getJSONObject(0);
                String minTmp=currentWeatherJsonObject.getJSONObject("tmp").getString("min");
                String maxTmp=currentWeatherJsonObject.getJSONObject("tmp").getString("max");
                String condFirst=currentWeatherJsonObject.getJSONObject("cond").getString("txt_d");
                String condSecond=currentWeatherJsonObject.getJSONObject("cond").getString("txt_n");
                String date=currentWeatherJsonObject.getString("date");

                JSONObject basicJsonObject=subJsonObject.getJSONObject("basic");
                String publishText=basicJsonObject.getJSONObject("update").getString("loc");
                String weatherCond="";
                if (condFirst.equals(condSecond)){
                    weatherCond=condFirst;
                }else {
                    weatherCond=condFirst+"转"+condSecond;
                }

                bResult=true;
                //saveWeatherInfo(context,publishText,date,weatherCond,minTmp,maxTmp);
                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putBoolean("get_crrent_city_weather",true);
                editor.putString("current_city_weather_desp",weatherCond);
                editor.putString("current_city_max_tmp",maxTmp);
                editor.putString("current_city_min_tmp",minTmp);
                editor.putString("current_city_publish",publishText);
                editor.putString("current_city_date",date);
                editor.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bResult;
    }

    public static void saveWeatherInfo(Context context,String publish,String currentDate,String weatherDesp,String minTmp,String maxTmp ){
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        //editor.putString("city_name",countyName);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("max_tmp",maxTmp);
        editor.putString("min_tmp",minTmp);
        editor.putString("publish",publish);
        editor.putString("current_date",currentDate);
        editor.commit();
    }

    public synchronized static boolean handleGeocoderResponse(Context context,String response){
        if (TextUtils.isEmpty(response)){
            return false;
        }
        try{
            JSONObject jsonObject=new JSONObject(response);
            int jsonStatus=jsonObject.getInt("status");
            if (0==jsonStatus){
                JSONObject subObject=jsonObject.getJSONObject("result");
                String currentLocation=subObject.getString("formatted_address");
                JSONObject addressComponentObject=subObject.getJSONObject("addressComponent");
                String district=addressComponentObject.getString("district");
                String cityname=addressComponentObject.getString("city");
                String provinceName=addressComponentObject.getString("province");

                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
                editor.putString("current_location",currentLocation);
                editor.putString("current_district",district);
                editor.putString("current_city",cityname);
                editor.putString("current_province",provinceName);
                editor.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
