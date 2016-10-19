package com.example.bmw.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bmw.coolweather.Adapter.SevenDaysForecastAdapter;
import com.example.bmw.coolweather.R;
import com.example.bmw.coolweather.model.SevenDaysWeather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bmw on 2016/10/19.
 */

public class SevenDaysWeatherForcastActivity extends Activity implements View.OnClickListener{
    private List<SevenDaysWeather> sevenDaysWeathers=new ArrayList<SevenDaysWeather>();
    private String cityName="";
    private String publishTime="";
    private boolean isFromChooseArea=false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seven_days_weather_forecast_layout);
        isFromChooseArea=getIntent().getBooleanExtra("is_from_choose_area",false);
        if (getSevenDaysWeather()){
            TextView textView=(TextView)findViewById(R.id.seven_days_location);
            textView.setText(cityName);
            TextView sevenDaysWeatherUpdateTime=(TextView)findViewById(R.id.seven_days_weather_update_time);
            sevenDaysWeatherUpdateTime.setText("未来七天天气\n"+publishTime);
            SevenDaysForecastAdapter sevenDaysForecastAdapter=new SevenDaysForecastAdapter(this,R.layout.seven_days_weather_layout,sevenDaysWeathers);
            ListView listView=(ListView)findViewById(R.id.seven_days_weather_forecast);
            listView.setAdapter(sevenDaysForecastAdapter);
        }
        Button backToSevenDaysWeather=(Button)findViewById(R.id.back_to_current_city_weather);
        Button queryOtherCitiesWeather=(Button)findViewById(R.id.query_other_city_weather);
        backToSevenDaysWeather.setOnClickListener(this);
        queryOtherCitiesWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.back_to_current_city_weather:
                finish();
                break;
            case R.id.query_other_city_weather:
                Intent intent=new Intent(SevenDaysWeatherForcastActivity.this,ChooseAreaActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private boolean getSevenDaysWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String response="";
        if (isFromChooseArea){
            response=prefs.getString("other_city_weather_response","");
        }else {
            response=prefs.getString("current_city_weather_response","");
        }
        if (TextUtils.isEmpty(response)){
            return false;
        }

        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject subJsonObject=jsonArray.getJSONObject(0);
            String status=subJsonObject.getString("status");
            if ("ok".equals(status)) {
                cityName=subJsonObject.getJSONObject("basic").getString("city");
                String updateTime=subJsonObject.getJSONObject("basic").getJSONObject("update").getString("loc");
                publishTime=updateTime+"发布";
                JSONArray dailyForecastSubJsonArray=subJsonObject.getJSONArray("daily_forecast");
                for (int i=0;i<dailyForecastSubJsonArray.length();i++){
                    SevenDaysWeather sevenDaysWeather=new SevenDaysWeather();
                    String date=dailyForecastSubJsonArray.getJSONObject(i).getString("date");//时间
                    String tempMin=dailyForecastSubJsonArray.getJSONObject(i).getJSONObject("tmp").getString("min");//最低温度
                    String tempMax=dailyForecastSubJsonArray.getJSONObject(i).getJSONObject("tmp").getString("max");//最低温度
                    String rainpop=dailyForecastSubJsonArray.getJSONObject(i).getString("pop");//降水概率
                    String pressure=dailyForecastSubJsonArray.getJSONObject(i).getString("pres");//气压
                    String windSpeed=dailyForecastSubJsonArray.getJSONObject(i).getJSONObject("wind").getString("spd");//风速(Kmph)
                    String windDirection=dailyForecastSubJsonArray.getJSONObject(i).getJSONObject("wind").getString("dir");//风向(方向)
                    String windScale=dailyForecastSubJsonArray.getJSONObject(i).getJSONObject("wind").getString("sc");//风力等级
                    String sunRiseTime=dailyForecastSubJsonArray.getJSONObject(i).getJSONObject("astro").getString("sr");//最低温度
                    String sunSetTime=dailyForecastSubJsonArray.getJSONObject(i).getJSONObject("astro").getString("ss");//最低温度
                    String dayWeather=dailyForecastSubJsonArray.getJSONObject(i).getJSONObject("cond").getString("txt_d");//白天天气
                    String nightWeather=dailyForecastSubJsonArray.getJSONObject(i).getJSONObject("cond").getString("txt_n");//夜晚天气


                    sevenDaysWeather.setDate(date);
                    sevenDaysWeather.setRainPop("降水概率:"+rainpop);
                    sevenDaysWeather.setTempature("温度:"+tempMin+"℃~"+tempMax+"℃");
                    sevenDaysWeather.setWindCondition("风力:"+windScale+" "+windDirection+"  风速:"+windSpeed+"Km/h");
                    sevenDaysWeather.setPressure("气压:"+Double.toString(Double.valueOf(pressure)/10)+"kpa");
                    sevenDaysWeather.setSunRiseTime("日出:"+sunRiseTime);
                    sevenDaysWeather.setSunSetTime("日落:"+sunSetTime);
                    //sevenDaysWeather.setUpdateTime(updateTime+"发布");
                    sevenDaysWeather.setWeatherDesp("白天:"+dayWeather+"\n晚上:"+nightWeather);
                    sevenDaysWeathers.add(sevenDaysWeather);
                }
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
