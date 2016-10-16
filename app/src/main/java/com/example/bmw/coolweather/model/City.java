package com.example.bmw.coolweather.model;

/**
 * Created by bmw on 2016/10/15.
 */

public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private String cityPyName;
    private int provinceId;

    public int getId(){
        return id;
    }

    public int getProvinceId(){
        return provinceId;
    }

    public String getCityName(){
        return cityName;
    }

    public String getCityCode(){
        return cityCode;
    }

    public String getCityPyName(){
        return cityPyName;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setCityName(String name){
        cityName=name;
    }

    public void  setCityCode(String code){
        cityCode=code;
    }

    public void setProvinceId(int id){
        this.provinceId=id;
    }

    public void setCityPyName(String pyName){
        this.cityPyName=pyName;
    }
}
