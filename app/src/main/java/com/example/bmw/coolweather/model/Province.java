package com.example.bmw.coolweather.model;

/**
 * Created by bmw on 2016/10/15.
 */

public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public int getId(){
        return id;
    }

    public String getProvinceName(){
        return provinceName;
    }

    public String getProvinceCode(){
        return provinceCode;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setProvinceName(String name){
        provinceName=name;
    }

    public void setProvinceCode(String code){
        provinceCode=code;
    }
}
