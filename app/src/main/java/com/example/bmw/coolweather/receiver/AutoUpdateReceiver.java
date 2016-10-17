package com.example.bmw.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.bmw.coolweather.service.AutoUpdateService;

/**
 * Created by bmw on 2016/10/17.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        Log.i("service","onReceive executed");
        Intent serviceIntent=new Intent(context, AutoUpdateService.class);
        context.startService(serviceIntent);
    }
}
