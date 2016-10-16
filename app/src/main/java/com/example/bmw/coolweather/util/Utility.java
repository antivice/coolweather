package com.example.bmw.coolweather.util;

import android.text.TextUtils;

import com.example.bmw.coolweather.db.CoolWeatherDB;
import com.example.bmw.coolweather.model.City;
import com.example.bmw.coolweather.model.County;
import com.example.bmw.coolweather.model.Province;

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
}
