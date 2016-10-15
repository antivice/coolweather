package com.example.bmw.coolweather.model;

/**
 * Created by bmw on 2016/10/15.
 */

public class Country {
    private int id;
    private String countryName;
    private String countryCode;
    private int cityId;

    public int getId(){
        return id;
    }

    public String getCountryName(){
        return countryName;
    }

    public String getCountryCode(){
        return countryCode;
    }

    public int getCityId(){
        return cityId;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setCountryName(String name){
        countryName=name;
    }

    public void setCountryCode(String code){
        countryCode=code;
    }

    public void setCityId(int id){
        cityId=id;
    }
}
