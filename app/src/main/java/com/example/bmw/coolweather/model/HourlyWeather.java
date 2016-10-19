package com.example.bmw.coolweather.model;

/**
 * Created by bmw on 2016/10/19.
 */

public class HourlyWeather {
    private String date;
    private String time;
    private String tempature;
    private String pm25;
    private String suggestion;
    private String windCondition;
    private String rainPop;
    private String airQuality;
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

    public String getPm25(){
        return pm25;
    }

    public String getWindCondition(){
        return windCondition;
    }

    public String getRainPop(){
        return rainPop;
    }

    public String getSuggestion(){
        return suggestion;
    }

    public String getAirQuality(){
        return airQuality;
    }

    public String getPressure(){
        return pressure;
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

    public void setPm25(String str){
        pm25=str;
    }

    public void setRainPop(String str){
        rainPop=str;
    }

    public void setSuggestion(String str){
        suggestion=str;
    }

    public void setWindCondition(String str){
        windCondition=str;
    }

    public void setAirQuality(String str){
        airQuality=str;
    }

    public void setPressure(String str){
        pressure=str;
    }
}
