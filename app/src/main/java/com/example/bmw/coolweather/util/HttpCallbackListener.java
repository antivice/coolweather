package com.example.bmw.coolweather.util;

/**
 * Created by bmw on 2016/10/15.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
