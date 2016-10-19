package com.example.bmw.coolweather.model;

/**
 * Created by bmw on 2016/10/19.
 */

public class SevenDaysWeather {
    private String date;
    private String time;
    private String updateTime;
    private String tempature;
    private String sunRiseTime;
    private String sunSetTime;
    private String humidity;
    private String windCondition;
    private String rainPop;
    private String weatherDesp;
    private String pressure;

    //get属性
    public String getDate(){
        return date;
    }

    public String getTime(){
        return time;
    }

    public String getTempature(){
        return tempature;
    }

    public String getSunRiseTime(){
        return sunRiseTime;
    }

    public String getWindCondition(){
        return windCondition;
    }

    public String getRainPop(){
        return rainPop;
    }

    public String getSunSetTime(){
        return sunSetTime;
    }

    public String getHumidity(){
        return humidity;
    }

    public String getPressure(){
        return pressure;
    }

    public String getWeatherDesp(){
        return weatherDesp;
    }

    public String getUpdateTime(){
        return updateTime;
    }


    //set属性
    public void setDate(String str){
        date=str;
    }

    public void setTime(String str){
        time=str;
    }

    public void setTempature(String str){
        tempature=str;
    }

    public void setSunRiseTime(String str){
        sunRiseTime=str;
    }

    public void setRainPop(String str){
        rainPop=str;
    }

    public void setSunSetTime(String str){
        sunSetTime=str;
    }

    public void setWindCondition(String str){
        windCondition=str;
    }

    public void setHumidity(String str){
        humidity=str;
    }

    public void setPressure(String str){
        pressure=str;
    }

    public void setWeatherDesp(String str){
        weatherDesp=str;
    }

    public void setUpdateTime(String str){
        updateTime=str;
    }
}
