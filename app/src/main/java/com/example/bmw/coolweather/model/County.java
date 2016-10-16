package com.example.bmw.coolweather.model;

/**
 * Created by bmw on 2016/10/15.
 */

public class County {
    private int id;
    private String countyName;
    private String countyCode;
    private String countyPyName;
    private int cityId;

    public int getId(){
        return id;
    }

    public String getCountyName(){
        return countyName;
    }

    public String getCountyCode(){
        return countyCode;
    }

    public int getCityId(){
        return cityId;
    }

    public String getCountyPyName(){
        return countyPyName;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setCountyName(String name){
        countyName=name;
    }

    public void setCountyCode(String code){
        countyCode=code;
    }

    public void setCityId(int id){
        cityId=id;
    }

    public void setCountyPyName(String pyName){
        this.countyPyName=pyName;
    }
}
