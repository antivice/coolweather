package com.example.bmw.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.bmw.coolweather.receiver.AutoUpdateReceiver;
import com.example.bmw.coolweather.util.HttpCallbackListener;
import com.example.bmw.coolweather.util.HttpUtil;
import com.example.bmw.coolweather.util.Utility;

/**
 * Created by bmw on 2016/10/17.
 */

public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.i("service","onStartCommand executed");
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=60*60*1000;
        long trigerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this, AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,trigerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    private void updateWeather(){
        Log.i("service","updateWeather executed");
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String countyCode=prefs.getString("county_code","");
        if (!TextUtils.isEmpty(countyCode)){
            Log.i("service","updateWeather,county_code="+countyCode);
            String address="https://api.heweather.com/x3/weather?cityid=CN"+countyCode+"&key=61db33bb544c4cdaaee7c69fc8e709bb";
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Utility.handleWeatherResponse(AutoUpdateService.this,response);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
