package com.example.bmw.coolweather.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bmw.coolweather.R;
import com.example.bmw.coolweather.util.HttpCallbackListener;
import com.example.bmw.coolweather.util.HttpUtil;
import com.example.bmw.coolweather.util.Utility;

/**
 * Created by bmw on 2016/10/16.
 */

public class CityWeatherActivity extends Activity {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView currentDateText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_weather_layout);

        weatherInfoLayout=(LinearLayout)findViewById(R.id.city_weather_info_layout);
        cityNameText=(TextView)findViewById(R.id.city_name);
        publishText=(TextView)findViewById(R.id.publish_text);
        currentDateText=(TextView)findViewById(R.id.current_date);
        weatherDespText=(TextView)findViewById(R.id.weather_desp);
        temp1Text=(TextView)findViewById(R.id.temp1);
        temp2Text=(TextView)findViewById(R.id.temp2);

        String countyCode=getIntent().getStringExtra("county_code");
        String countyName=getIntent().getStringExtra("county_name");
        if (!TextUtils.isEmpty(countyCode) && !TextUtils.isEmpty(countyName)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeather(countyCode,countyName);
        }else {
            showWeather();
        }
    }

    private void queryWeather(String countyCode,String countyName){
        String address="https://api.heweather.com/x3/weather?cityid=CN"+countyCode+"&key=61db33bb544c4cdaaee7c69fc8e709bb";
        queryFromServer(address,countyName,"countyCode");
    }

    private void showWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        temp2Text.setText(prefs.getString("max_tmp",""));
        temp1Text.setText(prefs.getString("min_tmp",""));
        publishText.setText(prefs.getString("publish",""));
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

    public void saveWeatherInfo(String countyName,String publish,String currentDate,String weatherDesp,String minTmp,String maxTmp){
        cityNameText.setText(countyName);
        publishText.setText(publish+"发布");
        currentDateText.setText(currentDate);
        weatherDespText.setText(weatherDesp);
        temp1Text.setText(minTmp);
        temp2Text.setText(maxTmp);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

    private void queryFromServer(final String address,final String countyName,final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)){
                    Utility.handleWeatherResponse(CityWeatherActivity.this,countyName,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }else if ("weatherCode".equals(type)){
                    ;
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }
}
